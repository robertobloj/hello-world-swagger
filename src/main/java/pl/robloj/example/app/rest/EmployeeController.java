package pl.robloj.example.app.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.robloj.example.app.dto.Employee;
import pl.robloj.example.app.repository.EmployeeRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping(path = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Find all employees",
        description = "Method allows to find ALL employees, notice that there is no pagination",
        responses = {
            @ApiResponse(responseCode = "200",description = "Employee with specified id found")
        })
    ResponseEntity<CollectionModel<EntityModel<Employee>>> findAll() {

        List<EntityModel<Employee>> employees = StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(employee -> EntityModel.of(employee,
                        linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(
                        employees,
                        linkTo(methodOn(EmployeeController.class).findAll()).withSelfRel()
                    )
            );
    }

    @GetMapping(path = "/employees/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find employee by id", description = "Method allows to find existing employee",
        responses = {
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
    ResponseEntity<EntityModel<Employee>> findOne(@PathVariable long id) {
        return repository.findById(id)
                .map(employee -> EntityModel.of(employee,
                        linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new employee", description = "Method allows to create new employee",
        responses = {
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
    ResponseEntity<EntityModel<Employee>> createEmployee(@Valid @RequestBody Employee employee) {
        try {
            Employee savedEmployee = repository.save(employee);
            EntityModel<Employee> employeeResource = EntityModel.of(
                    savedEmployee,
                    linkTo(methodOn(EmployeeController.class).findOne(savedEmployee.getId())).withSelfRel()
                );
            return ResponseEntity
                    .created(new URI(employeeResource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
                    .body(employeeResource);
        }
        catch (URISyntaxException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(path = "/employees/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update the employee", description = "Method allows to update existing employee",
        responses = {
            @ApiResponse(responseCode = "204", description = "Employee updated", content=@Content)
        })
    ResponseEntity<Void> updateEmployee(@Valid @RequestBody Employee employee, @NotNull @PathVariable long id)
            throws URISyntaxException {

        employee.setId(id);
        repository.save(employee);

        Link newlyCreatedLink = linkTo(methodOn(EmployeeController.class).findOne(id)).withSelfRel();
        return ResponseEntity.noContent().location(new URI(newlyCreatedLink.getHref())).build();
    }
}
