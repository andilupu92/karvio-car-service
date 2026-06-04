package karvio.controller;

import karvio.dto.request.CarRequest;
import karvio.dto.request.FuelRequest;
import karvio.dto.response.CarResponse;
import karvio.dto.response.CarWithDetailsResponse;
import karvio.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    CarController(CarService carService){
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> getCarsFromUser(@RequestHeader("X-User-Id") Long userId) {
        return new ResponseEntity<>(carService.getCarsFromUser(userId), HttpStatus.OK);
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<CarWithDetailsResponse>> getAllCarsWithExpenses(@RequestHeader("X-User-Id") Long userId) {
        return new ResponseEntity<>(carService.getAllCarsWithExpenses(userId), HttpStatus.OK);
    }

    @PostMapping("/byExpenses")
    public ResponseEntity<List<CarResponse>> getDetailsCars(@RequestBody List<Long> carIds) {
        return new ResponseEntity<>(carService.getDetailsCars(carIds), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CarResponse> add(@RequestHeader("X-User-Id") Long userId,
                                           @Valid @RequestBody CarRequest carRequest) {
        return new ResponseEntity<>(carService.save(userId, carRequest), HttpStatus.CREATED);
    }

    @PostMapping("/addFuel")
    public ResponseEntity<CarResponse> addFuel(@Valid @RequestBody FuelRequest fuelRequest) {
        return new ResponseEntity<>(carService.saveFuel(fuelRequest), HttpStatus.CREATED);
    }

    @PostMapping("/financialHealth/{monthlyAverage}/{carId}")
    public ResponseEntity<Void> updateFinancialHealth(@PathVariable BigDecimal monthlyAverage,
                               @PathVariable Long carId) {
        carService.updateFinancialHealth(monthlyAverage, carId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/byUser/{userId}")
    public ResponseEntity<Void> deleteAllCarsByUser(@PathVariable Long userId) {
        carService.deleteAllCarsByUser(userId);
        return ResponseEntity.noContent().build();
    }
}
