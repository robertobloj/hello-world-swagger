package pl.robloj.example.app.dto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.*;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Employee {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    private String role;

    public Employee(String firstName, String lastName, String role) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

}
