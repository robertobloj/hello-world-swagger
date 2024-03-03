package pl.robloj.example.app.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.robloj.example.app.dto.Employee;
import pl.robloj.example.app.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/employees/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        operationId = "GET_employees",
        summary = "Find all employees (without HATEOAS)",
        description = "Method allows to find ALL employees, notice that there is no pagination"
        )
    @ApiResponses({
        @ApiResponse(responseCode = "200",description = "Employee with specified id found")
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<Employee>> findAll() {
        List<Employee> target = new ArrayList<>();
        Iterable<Employee> result = repository.findAll();
        result.forEach(target::add);
        return ResponseEntity.ok(target);
    }

    @GetMapping(path = "/employees/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        operationId = "GET_employee_by_id",
        summary = "Find employee by id without HATEOAS", description = "Method allows to find existing employee"
        )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee with specified id found",
                    content = { @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Employee.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Employee with specified id NOT found",
                    content = @Content
            )
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Employee> findOne(@PathVariable long id) {
        val entity = repository.findById(id);
        return ResponseEntity.of(entity);
    }

    @PostMapping(path = "/employees/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        operationId = "POST_new_employee",
        summary = "Create new employee without HATEOAS", description = "Method allows to create new employee"
        )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Employee created",
                    content = { @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Employee.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request, check details what went wrong",
                    content=@Content
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        try {
            Employee savedEmployee = repository.save(employee);
            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(path = "/employees/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        operationId = "PUT_employee_update",
        summary = "Update the employee without HATEOAS",
        description = "Method allows to update existing employee"
        )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Employee updated", content=@Content)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> updateEmployee(@Valid @RequestBody Employee employee, @NotNull @PathVariable long id) {
        employee.setId(id);
        repository.save(employee);
        return ResponseEntity.noContent().build();
    }
}
