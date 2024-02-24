package pl.robloj.example.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pl.robloj.example.app.dto.Employee;
import pl.robloj.example.app.repository.EmployeeRepository;

@Component
public class DatabaseLoader {
    @Bean
    CommandLineRunner init(EmployeeRepository repository) {
        return args -> {
            repository.save(new Employee("John", "Smith", "teacher"));
            repository.save(new Employee("Kate", "Bush", "actress"));
            repository.save(new Employee("Olivier", "Great", "driver"));
            repository.save(new Employee("Huston", "Problem", "astronaut"));
            repository.save(new Employee("Jack", "Oregano", "farmer"));
        };
    }
}
