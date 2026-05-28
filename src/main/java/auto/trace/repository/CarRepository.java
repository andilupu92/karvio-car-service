package auto.trace.repository;

import auto.trace.entity.Car;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByUserId(Long userId);

    List<Car> findByIdIn(List<Long> carIds);

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);
}
