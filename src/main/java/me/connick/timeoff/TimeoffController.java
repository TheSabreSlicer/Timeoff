package me.connick.timeoff;

import java.util.Map;
import java.util.List;
import java.net.URISyntaxException;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
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

	// handles POST for creating new employees, takes optional "hours" parameter
	@RequestMapping(value = "/timeoff/employees/", method=RequestMethod.POST, produces={"application/json"})
	public ResponseEntity<Employee> employeePost(@RequestParam(value="hours", defaultValue="0") int hours,
																										HttpServletRequest request)
																										throws URISyntaxException {
		Employee newEmployee = new Employee(hours);
		repository.save(newEmployee);
		HttpHeaders respHeaders = new HttpHeaders();
		URI resource = new URI(request.getRequestURL().toString()+newEmployee.getId());
   	respHeaders.setLocation(resource);
		return new ResponseEntity<Employee>(newEmployee, respHeaders, HttpStatus.CREATED);
	}

	// returns a JSON object of all employees
	@RequestMapping(value = "/timeoff/employees/", method=RequestMethod.GET, produces={"application/json"})
	public ResponseEntity<List<Employee>> employeeGetAll() {
		List<Employee> allEmployees = repository.findAll();
		return new ResponseEntity<List<Employee>>(allEmployees, HttpStatus.OK);
	}

	// returns a JSON object of a specific employee's hours
	@RequestMapping(value = "/timeoff/employees/{id}", method=RequestMethod.GET, produces={"application/json"})
	public ResponseEntity<String> employeeGet(@PathVariable("id") String id) throws EmployeeNotFoundException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		String returnStr = "{\n\t\"hours\": " + e.getHours() +"\n}";
		return new ResponseEntity<String>(returnStr, HttpStatus.OK);
	}

	// allows a PUT to update number of hours
	@RequestMapping(value = "/timeoff/employees/{id}", method=RequestMethod.PUT, produces={"application/json"})
	public ResponseEntity<String> employeePut(@PathVariable("id") String id, @RequestParam(value="hours") int hours)
																												throws EmployeeNotFoundException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		e.setHours(hours);
		repository.save(e);
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	// deletes a specific employee in the database
	@RequestMapping(value = "/timeoff/employees/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<String> employeeDelete(@PathVariable("id") String id) throws EmployeeNotFoundException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		repository.deleteById(id);
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	// deletes all employees in the database
	@RequestMapping(value = "/timeoff/employees/", method=RequestMethod.DELETE)
	public ResponseEntity<String> employeeDeleteAll() {
		repository.deleteAll();
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	// handles POST for new PTO request
	@RequestMapping(value = "/timeoff/requests/", method=RequestMethod.POST, produces={"application/json"})
	public ResponseEntity<Map<String, Integer>> requestPost(@RequestParam(value="id") String id,
																					@RequestParam(value="hours", defaultValue="0") int numHours,
																					@RequestParam(value="year") int year,
																					@RequestParam(value="month") int month,
																					@RequestParam(value="day") int day,
																					HttpServletRequest request)
																					throws URISyntaxException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		e.timeoffRequest(numHours, year, month, day);
		repository.save(e);

		HttpHeaders respHeaders = new HttpHeaders();
		URI resource = new URI(request.getRequestURL().toString()+e.getId());
   	respHeaders.setLocation(resource);
		return new ResponseEntity<Map<String, Integer>>(e.getRequests(), respHeaders, HttpStatus.CREATED);
	}

	// returns all scheduled PTO of a specific employee
	@RequestMapping(value = "/timeoff/requests/{id}", method=RequestMethod.GET, produces={"application/json"})
	public ResponseEntity<Map<String, Integer>> requestGet(@PathVariable("id") String id) throws EmployeeNotFoundException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		return new ResponseEntity<Map<String, Integer>>(e.getRequests(), HttpStatus.OK);
	}

	// handles a 404 response if a specific employee is not found in the repository
	@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No Such Employee")
	public class EmployeeNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
