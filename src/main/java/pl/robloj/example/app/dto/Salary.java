package pl.robloj.example.app.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Salary {
    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @PastOrPresent(message = "salary timestamp can't be in the future")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp salaryTimestamp;

    @DecimalMin(value = "1000", message = "Salary must be at least 1000 EUR")
    @DecimalMax(value = "1000000", message = "Salary can't be greater than 100000 EUR monthly")
    private BigDecimal amount;

    private Boolean annualBonus;

}
