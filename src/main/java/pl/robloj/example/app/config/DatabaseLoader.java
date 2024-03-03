package pl.robloj.example.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pl.robloj.example.app.dto.Employee;
import pl.robloj.example.app.dto.Salary;
import pl.robloj.example.app.repository.EmployeeRepository;
import pl.robloj.example.app.repository.SalaryRepository;

import java.util.List;

@Component
public class DatabaseLoader {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:employees.json")
    private Resource employeesResourceFile;

    @Value("classpath:salaries.json")
    private Resource salariesResourceFile;

    @Bean
    CommandLineRunner init(EmployeeRepository employeeRepository, SalaryRepository salaryRepository) {
        return args -> {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Employee> employees = objectMapper.readValue(
                    employeesResourceFile.getFile(),
                    typeFactory.constructCollectionType(List.class, Employee.class)
                );
            employees.forEach(employeeRepository::save);

            List<Salary> salaries = objectMapper.readValue(
                    salariesResourceFile.getFile(),
                    typeFactory.constructCollectionType(List.class, Salary.class)
            );
            salaries.forEach(salaryRepository::save);
        };
    }
}
