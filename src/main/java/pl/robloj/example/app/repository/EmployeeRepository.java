package pl.robloj.example.app.repository;

import org.springframework.data.repository.CrudRepository;
import pl.robloj.example.app.dto.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
}
