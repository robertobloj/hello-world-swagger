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
import pl.robloj.example.app.repository.EmployeeRepository;

import java.util.List;

@Component
public class DatabaseLoader {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:employees.json")
    private Resource resourceFile;

    @Bean
    CommandLineRunner init(EmployeeRepository repository) {
        return args -> {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            List<Employee> employees = objectMapper.readValue(
                    resourceFile.getFile(),
                    typeFactory.constructCollectionType(List.class, Employee.class)
                );
            employees.forEach(repository::save);
        };
    }
}
