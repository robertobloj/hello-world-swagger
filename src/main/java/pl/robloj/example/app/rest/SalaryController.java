package pl.robloj.example.app.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.robloj.example.app.dto.Salary;
import pl.robloj.example.app.repository.SalaryRepository;

import java.util.List;

@RestController
public class SalaryController {

    private final SalaryRepository repository;

    public SalaryController(SalaryRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/salaries/employee/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        operationId = "GET_salaries_by_employee_id",
        summary = "Find all salaries related to employee without HATEOAS",
        description = "Method allows to find ALL salaries, notice that there is no pagination"
        )
    @ApiResponses({
        @ApiResponse(responseCode = "200",description = "Salaries with specified employee id found")
    })
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<Salary>> findSalariesForEmployee(@PathVariable long employeeId) {
        val salaries = repository.findByEmployeeId(employeeId);
        return ResponseEntity.ok(salaries);
    }

    @PostMapping(path = "/salaries/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        operationId = "POST_new_salary",
        summary = "Create new salary entity without HATEOAS response",
        description = "Method allows to create new salary"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Salary created",
            content = { @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Salary.class)
            )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request, check details what went wrong",
            content=@Content
        )
    })
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<Salary> createSalary(@Valid @RequestBody Salary salary) {
        try {
            Salary savedSalary = repository.save(salary);
            return new ResponseEntity<>(savedSalary, HttpStatus.CREATED);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
