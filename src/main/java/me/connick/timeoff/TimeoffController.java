package me.connick.timeoff;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

// Controls all REST API endpoint requests
@RestController
public class TimeoffController {

	@Autowired
	private EmployeeRepository repository;

	// returns a JSON object of all employees
	@RequestMapping(value = "/timeoff/employees", method=RequestMethod.POST, produces={"application/json"})
	public ResponseEntity<Employee> create(@RequestParam(value="hours", defaultValue="0") int hours, HttpServletRequest request) throws URISyntaxException{
		Employee newEmployee = new Employee(hours);
		repository.save(newEmployee);
		HttpHeaders respHead = new HttpHeaders();
		URI resource = new URI(request.getRequestURL().toString()+newEmployee.getId());
   	respHead.setLocation(resource);
		return new ResponseEntity<Employee>(newEmployee, respHead, HttpStatus.CREATED);
	}

	// returns a JSON object of all employees
	@RequestMapping(value = "/timeoff/employees", method=RequestMethod.GET, produces={"application/json"})
	public ResponseEntity<List<Employee>> queryAll() {
		List<Employee> allEmployees = repository.findAll();
		return new ResponseEntity<List<Employee>>(allEmployees, HttpStatus.OK);
	}

	// returns a JSON object of a specific employee
	@RequestMapping(value = "/timeoff/employees/{id}", method=RequestMethod.GET, produces={"application/json"})
	public ResponseEntity<Employee> query(@PathVariable("id") String id) throws EmployeeNotFoundException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		return new ResponseEntity<Employee>(e, HttpStatus.OK);
	}

	// deletes all employees in the database
	@RequestMapping(value = "/timeoff/employees", method=RequestMethod.DELETE)
	public ResponseEntity<String> deleteAll() {
		repository.deleteAll();
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	// deletes a specific employee in the database
	@RequestMapping(value = "/timeoff/employees/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<String> delete(@PathVariable("id") String id) throws EmployeeNotFoundException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		repository.deleteById(id);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Employee")  // 404
	public class EmployeeNotFoundException extends RuntimeException {
		// ...
	}

}
