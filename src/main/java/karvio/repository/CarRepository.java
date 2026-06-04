package karvio.repository;

import karvio.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByUserId(Long userId);

    List<Car> findByIdIn(List<Long> carIds);

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);
}
