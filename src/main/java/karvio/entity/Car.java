package karvio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "energy_type", updatable = false, nullable = false)
    private String energyType;

    @Column(name = "kilometers", nullable = false)
    private Integer kilometers;

    @Column(name = "initial_kilometers", updatable = false)
    private Integer initialKilometers;

    @Column(name = "year", updatable = false, nullable = false)
    private Integer year;

    @Column(name = "consumption", precision = 4, scale = 2)
    private BigDecimal consumption;

    @Column(name = "liters")
    private BigDecimal liters;

    @Column(name = "mechanical_health")
    private Integer mechanicalHealth;

    @Column(name = "financial_health")
    private Integer financialHealth;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
