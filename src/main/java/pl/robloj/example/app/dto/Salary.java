package pl.robloj.example.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.robloj.example.app.dto.groups.DecimalConstraint;
import pl.robloj.example.app.dto.groups.NotNullConstraint;

import java.math.BigDecimal;
import java.util.Date;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date salaryTimestamp;

    @NotNull(groups = NotNullConstraint.class)
    @DecimalMin(value = "1000", message = "Salary must be at least 1000 EUR", groups = DecimalConstraint.class)
    @DecimalMax(value = "1000000", message = "Salary can't be greater than 100000 EUR monthly", groups = DecimalConstraint.class)
    private BigDecimal amount;

    private Boolean annualBonus;

}
