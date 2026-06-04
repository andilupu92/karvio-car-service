package karvio.service;

import karvio.client.DocumentServiceClient;
import karvio.dto.client.ExpenseRequest;
import karvio.dto.client.ExpenseResponse;
import karvio.dto.request.CarRequest;
import karvio.dto.request.FuelRequest;
import karvio.dto.response.CarResponse;
import karvio.dto.response.CarWithDetailsResponse;
import karvio.entity.Car;
import karvio.exception.ResourceNotFoundException;
import karvio.mapper.CarMapper;
import karvio.repository.CarRepository;
import karvio.utils.RangeScore;
import karvio.utils.RangeScoringEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final DocumentServiceClient documentServiceClient;

    public List<CarResponse> getCarsFromUser(Long userId) {
        return carMapper.toResponseList(carRepository.findByUserId(userId));
    }

    public List<CarWithDetailsResponse> getAllCarsWithExpenses(Long userId) {
        List<Car> cars = carRepository.findByUserId(userId);

        if (cars.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> carIds = cars.stream()
                                .map(Car::getId)
                                .toList();

        List<ExpenseResponse> allExpenses = documentServiceClient.getExpensesForCars(carIds);

        Map<Long, BigDecimal> totalAmountByCarId = allExpenses.stream()
                .collect(Collectors.groupingBy(
                        ExpenseResponse::carId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                ExpenseResponse::amount,
                                BigDecimal::add
                        )
                ));

        return cars.stream()
                .map(car -> new CarWithDetailsResponse(
                        car.getId(),
                        car.getName(),
                        car.getConsumption(),
                        carMapper.calculateHealthScore(car.getFinancialHealth(), car.getMechanicalHealth()),
                        totalAmountByCarId.getOrDefault(car.getId(), BigDecimal.ZERO)
                ))
                .toList();
    }

    public CarResponse save(Long userId, CarRequest carRequest) {
        Car c;

        c = carMapper.toEntity(carRequest);
        c.setInitialKilometers(c.getKilometers());
        c.setUserId(userId);

        return carMapper.toResponse(carRepository.save(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException("Car not found with id: " + id);
        }

        documentServiceClient.deleteAllDocumentsByCar(id);
        carRepository.deleteById(id);
    }

    public List<CarResponse> getDetailsCars(List<Long> carIds) {

        List<Car> cars = carRepository.findByIdIn(carIds);

        return carMapper.toResponseList(cars);
    }

    @Transactional
    public CarResponse saveFuel(FuelRequest fuelRequest) {

        Car car;
        car = carRepository.findById(fuelRequest.carId())
                    .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + fuelRequest.carId()));

        BigDecimal consumption = getConsumption(fuelRequest, car);

        List<RangeScore> consumptionRanges = List.of(
                new RangeScore(null, BigDecimal.valueOf(6), 100),
                new RangeScore(BigDecimal.valueOf(6), BigDecimal.valueOf(7), 95),
                new RangeScore(BigDecimal.valueOf(7), BigDecimal.valueOf(8), 90),
                new RangeScore(BigDecimal.valueOf(8), BigDecimal.valueOf(9), 80),
                new RangeScore(BigDecimal.valueOf(9), BigDecimal.valueOf(10), 70),
                new RangeScore(BigDecimal.valueOf(10), BigDecimal.valueOf(11), 60),
                new RangeScore(BigDecimal.valueOf(11), null, 50)
        );
        int mechanicalScore = RangeScoringEngine.calculate(consumption, consumptionRanges);

        car.setConsumption(consumption);
        car.setKilometers(fuelRequest.kilometers());
        car.setMechanicalHealth(mechanicalScore);

        BigDecimal monthlyAverage = documentServiceClient.getMonthlyAverage(fuelRequest.carId(),
                new ExpenseRequest(fuelRequest.carId(),
                        13L, // is expenseTypeId for fuel -> I will change in the future
                        fuelRequest.amount(),
                        fuelRequest.date()));

        car.setFinancialHealth(getFinancialHealth(monthlyAverage));

        return carMapper.toResponse(carRepository.save(car));

    }

    @Transactional
    public void updateFinancialHealth(BigDecimal monthlyAverage, Long carId) {

        Car car;
        car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + carId));

        car.setFinancialHealth(getFinancialHealth(monthlyAverage));
        carRepository.save(car);

        log.info("Successfully updated financial health for car: {}", carId);
    }

    private static int getFinancialHealth(BigDecimal monthlyAverage) {
        List<RangeScore> expenseRanges = List.of(
                new RangeScore(null, BigDecimal.valueOf(1000), 100),
                new RangeScore(BigDecimal.valueOf(1000), BigDecimal.valueOf(2000), 80),
                new RangeScore(BigDecimal.valueOf(2000), null, 60)
        );
        return RangeScoringEngine.calculate(monthlyAverage, expenseRanges);
    }

    private static BigDecimal getConsumption(FuelRequest fuelRequest, Car car) {
        BigDecimal newKilometers = BigDecimal.valueOf(fuelRequest.kilometers())
                .subtract(BigDecimal.valueOf(car.getInitialKilometers()));

        if (newKilometers.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Current kilometers (" + fuelRequest.kilometers() +
                            ") must be greater than past kilometers (" +
                            car.getKilometers() + ")"
            );
        }

        BigDecimal totalLiters = fuelRequest.liters();
        if (car.getLiters() != null)
            totalLiters = fuelRequest.liters().add(car.getLiters());

        car.setLiters(totalLiters);

        return totalLiters
                .divide(newKilometers, 10, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void deleteAllCarsByUser(Long userId) {
        if (carRepository.existsByUserId(userId)) {
            carRepository.deleteByUserId(userId);
        }
    }
}
