package pl.robloj.example.app.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.robloj.example.app.dto.Salary;
import pl.robloj.example.app.repository.SalaryRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class SalaryController {

    private final SalaryRepository repository;

    public SalaryController(SalaryRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/salaries/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find salary by id", description = "Method allows to find salary by salary id",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Salary with specified id found",
                content = { @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Salary.class)
                )}
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Salary with specified id NOT found",
                content = @Content
            )
        })
    ResponseEntity<EntityModel<Salary>> findOne(@PathVariable long id) {
        return repository.findById(id)
                .map(salary -> EntityModel.of(salary,
                        linkTo(methodOn(SalaryController.class).findOne(salary.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/salaries", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Find all salaries related to employee",
            description = "Method allows to find ALL salaries, notice that there is no pagination",
            responses = {
                    @ApiResponse(responseCode = "200",description = "Salaries with specified employee id found")
            })
    ResponseEntity<CollectionModel<EntityModel<Salary>>> findSalariesForEmployee(@RequestBody Long employeeId) {

        List<EntityModel<Salary>> salaries = repository.findByEmployeeId(employeeId).stream()
                .map(salary -> EntityModel.of(salary,
                        linkTo(methodOn(EmployeeController.class).findOne(employeeId)).withSelfRel(),
                        linkTo(methodOn(SalaryController.class).findSalariesForEmployee(employeeId)).withRel("salaries")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
            CollectionModel.of(salaries,
                linkTo(methodOn(SalaryController.class).findOne(employeeId)).withSelfRel()
            )
        );
    }

    @PostMapping(path = "/salaries", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new salary entity", description = "Method allows to create new salary",
        responses = {
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
    ResponseEntity<EntityModel<Salary>> createSalary(@Valid @RequestBody Salary salary) {
        try {
            Salary savedSalary = repository.save(salary);
            EntityModel<Salary> salaryResource = EntityModel.of(
                    savedSalary,
                    linkTo(methodOn(SalaryController.class).findOne(savedSalary.getId())).withSelfRel()
            );
            return ResponseEntity
                    .created(new URI(salaryResource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
                    .body(salaryResource);
        }
        catch (URISyntaxException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
