package pl.robloj.example.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pl.robloj.example.app.dto.Employee;
import pl.robloj.example.app.dto.EmployeeRole;
import pl.robloj.example.app.repository.EmployeeRepository;

@Component
public class DatabaseLoader {
    @Bean
    CommandLineRunner init(EmployeeRepository repository) {
        return args -> {
            repository.save(Employee.builder()
                    .firstName("John")
                    .lastName("Smith")
                    .email("john@gmail.com")
                    .pesel("82090672857")
                    .age(60)
                    .role(EmployeeRole.CEO).build()
                );
            repository.save(Employee.builder()
                    .firstName("Kate")
                    .lastName("Bush")
                    .email("kate@yahoo.com")
                    .pesel("87110791842")
                    .age(36)
                    .role(EmployeeRole.DIRECTOR).build()
                );
            repository.save(Employee.builder()
                    .firstName("Olivier")
                    .lastName("Great")
                    .email("olivier@outlook.com")
                    .pesel("91050225183")
                    .age(45)
                    .role(EmployeeRole.MANAGER).build()
                );
            repository.save(Employee.builder()
                    .firstName("Huston")
                    .lastName("Problem")
                    .email("huston@proton.com")
                    .pesel("93030495432")
                    .age(21)
                    .role(EmployeeRole.DEVELOPER).build()
                );
            repository.save(Employee.builder()
                    .firstName("Jack")
                    .lastName("Oregano")
                    .email("jack@oregano.eu")
                    .pesel("89110288975")
                    .age(25)
                    .role(EmployeeRole.TESTER).build()
                );
        };
    }
}
