package me.connick.timeoff;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class TimeoffController {

	@Autowired
	private EmployeeRepository repository;

	@RequestMapping("/timeoff/create")
    public Employee create(@RequestParam(value="hours", defaultValue="0") int hours) {
				Employee newEmployee = new Employee(hours);
				repository.save(newEmployee);
        return newEmployee;
    }

}
