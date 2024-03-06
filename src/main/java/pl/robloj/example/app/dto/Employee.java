package pl.robloj.example.app.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.pl.PESEL;
import pl.robloj.example.app.dto.validators.IBAN;

import java.util.List;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long id;

    @NotBlank(message = "First name is a mandatory field")
    @Size(min = 3, max = 32, message = "First name must have at least 3 chars and maximum 32 chars")
    private String firstName;

    @NotBlank(message = "Last name is a mandatory field")
    @Size(min = 2, max = 64, message = "Last name must have at least 2 chars and maximum 64 chars")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "age of employee is mandatory")
    @Min(value = 18, message = "only adults can work")
    @Max(value = 65, message = "pensioners cannot work")
    private Integer age;

    @PESEL(message="Unique id for polish citizen is invalid")
    private String pesel;

    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    @URL(protocol = "https")
    private String linkedInProfile;

    @IBAN(message = "Employee must provide valid account number to receive salary")
    private String accountNumber;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Salary> salaries;

}
