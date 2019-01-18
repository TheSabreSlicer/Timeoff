package me.connick.timeoff;

import java.util.Map;
import java.time.DateTimeException;
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

	// handles POST for creating new employees
	@RequestMapping(value = "/timeoff/employees/", method=RequestMethod.POST, produces={"application/json"})
	public ResponseEntity<Employee> employeePost(@RequestParam(value="hours", defaultValue="0") int hours, HttpServletRequest request) throws URISyntaxException{
		Employee newEmployee = new Employee(hours);
		repository.save(newEmployee);
		HttpHeaders respHead = new HttpHeaders();
		URI resource = new URI(request.getRequestURL().toString()+newEmployee.getId());
   	respHead.setLocation(resource);
		return new ResponseEntity<Employee>(newEmployee, respHead, HttpStatus.CREATED);
	}

	// returns a JSON object of all employees
	@RequestMapping(value = "/timeoff/employees/", method=RequestMethod.GET, produces={"application/json"})
	public ResponseEntity<List<Employee>> employeeGetAll() {
		List<Employee> allEmployees = repository.findAll();
		return new ResponseEntity<List<Employee>>(allEmployees, HttpStatus.OK);
	}

	// returns a JSON object of a specific employee
	@RequestMapping(value = "/timeoff/employees/{id}", method=RequestMethod.GET, produces={"application/json"})
	public ResponseEntity<Employee> employeeGet(@PathVariable("id") String id) throws EmployeeNotFoundException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		return new ResponseEntity<Employee>(e, HttpStatus.OK);
	}

	// deletes all employees in the database
	@RequestMapping(value = "/timeoff/employees/", method=RequestMethod.DELETE)
	public ResponseEntity<String> employeeDeleteAll() {
		repository.deleteAll();
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	// deletes a specific employee in the database
	@RequestMapping(value = "/timeoff/employees/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<String> employeeDelete(@PathVariable("id") String id) throws EmployeeNotFoundException {
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		repository.deleteById(id);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	// handles POST for new PTO request
	@RequestMapping(value = "/timeoff/requests/", method=RequestMethod.POST, produces={"application/json"})
	public ResponseEntity<Employee> requestPost(@RequestParam(value="id") String id,
																					@RequestParam(value="hours", defaultValue="0") int numHours,
																					@RequestParam(value="year") int year,
																					@RequestParam(value="month") int month,
																					@RequestParam(value="day") int day,
																					HttpServletRequest request)
																					throws URISyntaxException, InvalidDateException, InvalidPTORequest{
		Employee e = repository.findByEmployeeId(id);
		if(e == null){
			throw new EmployeeNotFoundException();
		}
		try {
			boolean isValidPTO = e.timeoffRequest(numHours, year, month, day);
			repository.save(e);
			if(!isValidPTO){
				throw new InvalidPTORequest();
			}
		} catch(DateTimeException exception) {
			throw new InvalidDateException();
		}

		HttpHeaders respHead = new HttpHeaders();
		URI resource = new URI(request.getRequestURL().toString()+e.getId());
   	respHead.setLocation(resource);
		return new ResponseEntity<Employee>(e, respHead, HttpStatus.CREATED);
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

	// Custom Exception definitions for handling various errors

	// handles a 404 response if a specific employee is not found
	@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No Such Employee")
	public class EmployeeNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	// handles a response if invalid date is supplied
	@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Invalid Date")
	public class InvalidDateException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	// handles a response if invalid date is supplied
	@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Not Enough Hours")
	public class InvalidPTORequest extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

}
