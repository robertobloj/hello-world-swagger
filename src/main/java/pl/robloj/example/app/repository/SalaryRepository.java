package pl.robloj.example.app.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.robloj.example.app.dto.Salary;

import java.util.List;

public interface SalaryRepository extends CrudRepository<Salary, Long> {

    @Query(value = "SELECT * FROM salary where employee_id = ?1", nativeQuery = true)
    List<Salary> findByEmployeeId(Long employeeId);

}
