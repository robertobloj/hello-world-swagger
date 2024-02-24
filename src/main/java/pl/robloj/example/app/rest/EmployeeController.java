package pl.robloj.example.app.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.robloj.example.app.dto.Employee;
import pl.robloj.example.app.repository.EmployeeRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class EmployeeController {

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    /**
     * Look up all employees, and transform them into a REST collection resource. Then return them through Spring Web's
     * {@link ResponseEntity} fluent API.
     */
    @GetMapping(path = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(path = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<Employee>> newEmployee(@RequestBody Employee employee) {

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

    /**
     * Look up a single {@link Employee} and transform it into a REST resource. Then return it through Spring Web's
     * {@link ResponseEntity} fluent API.
     *
     * @param id identifier
     */
    @GetMapping(path = "/employees/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<EntityModel<Employee>> findOne(@PathVariable long id) {

        return repository.findById(id)
                .map(employee -> EntityModel.of(employee,
                        linkTo(methodOn(EmployeeController.class).findOne(employee.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update existing employee then return a Location header.
     *
     * @param employee employee
     * @param id identifier
     * @return none
     */
    @PutMapping(path = "/employees/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<Void> updateEmployee(@RequestBody Employee employee, @PathVariable long id) throws URISyntaxException {

        employee.setId(id);
        repository.save(employee);

        Link newlyCreatedLink = linkTo(methodOn(EmployeeController.class).findOne(id)).withSelfRel();
        return ResponseEntity.noContent().location(new URI(newlyCreatedLink.getHref())).build();

    }
}
