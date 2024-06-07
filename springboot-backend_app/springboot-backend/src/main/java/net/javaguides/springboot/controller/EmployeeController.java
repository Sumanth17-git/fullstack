package net.javaguides.springboot.controller;

import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.repository.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    @GetMapping
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        List<Employee> employees = employeeRepository.findAll();
        try {
            logger.info("Response: {}", objectMapper.writeValueAsString(employees));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing response", e);
        }
        return employees;
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        logger.info("Creating new employee: {}", employee);
        Employee savedEmployee = employeeRepository.save(employee);
        try {
            logger.info("Response: {}", objectMapper.writeValueAsString(savedEmployee));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing response", e);
        }
        return savedEmployee;
    }

    @GetMapping("{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable long id) {
        logger.info("Fetching employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Employee not found with id: {}", id);
                    return new ResourceNotFoundException("Employee not exist with id:" + id);
                });
        try {
            logger.info("Response: {}", objectMapper.writeValueAsString(employee));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing response", e);
        }
        return ResponseEntity.ok(employee);
    }

    @PutMapping("{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable long id, @RequestBody Employee employeeDetails) {
        logger.info("Updating employee with id: {}", id);
        Employee updateEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Employee not found with id: {}", id);
                    return new ResourceNotFoundException("Employee not exist with id: " + id);
                });
        updateEmployee.setFirstName(employeeDetails.getFirstName());
        updateEmployee.setLastName(employeeDetails.getLastName());
        updateEmployee.setEmailId(employeeDetails.getEmailId());

        employeeRepository.save(updateEmployee);
        try {
            logger.info("Response: {}", objectMapper.writeValueAsString(updateEmployee));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing response", e);
        }
        return ResponseEntity.ok(updateEmployee);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable long id) {
        logger.info("Deleting employee with id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Employee not found with id: {}", id);
                    return new ResourceNotFoundException("Employee not exist with id: " + id);
                });

        employeeRepository.delete(employee);
        logger.info("Deleted employee with id: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
