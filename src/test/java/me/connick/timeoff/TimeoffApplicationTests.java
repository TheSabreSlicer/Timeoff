package me.connick.timeoff;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TimeoffApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Before
	public void deleteAllBeforeTests() throws Exception {
		employeeRepository.deleteAll();
	}

	@Test // tests POST to create new employee
	public void shouldCreateEmployee() throws Exception {
		mockMvc.perform(post("/timeoff/employees/").param("hours", "24")).andExpect(
		status().isCreated()).andExpect(
		header().string("Location", containsString("/timeoff/employees/"))); // makes sure new location is returned
	}

	@Test // tests GET to fetch hours of newly created employee
	public void shouldRetrieveEmployee() throws Exception {

		// POST new employee
		MvcResult mvcResult = mockMvc.perform(post("/timeoff/employees/").param("hours", "24")).andExpect(
		status().isCreated()).andReturn();

		// get and make sure it returns correct hours
		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
		jsonPath("$.hours").value("24"));
	}

	@Test // tests GET to fetch all employees
	public void shouldRetrieveEmployees() throws Exception {

		// POST new employee
		mockMvc.perform(post("/timeoff/employees/").param("hours", "24")).andExpect(
		status().isCreated()).andReturn();

		// POST second employee
		mockMvc.perform(post("/timeoff/employees/").param("hours", "32")).andExpect(
		status().isCreated()).andReturn();

		// get and make sure it returns correct hours
		mockMvc.perform(get("/timeoff/employees/")).andExpect(status().isOk()).andExpect(
		jsonPath("$.length()").value("2"));
	}

	@Test // tests PUT to update employee hours
	public void shouldUpdateEmployee() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/timeoff/employees/").param("hours", "24")).andExpect(
		status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put(location).param("hours", "32")).andExpect(
		status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
		jsonPath("$.hours").value("32"));
	}

	@Test // tests DELETE to delete employee
	public void shouldDeleteEmployee() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/timeoff/employees/").content(
		"{\"hours\": 24}")).andExpect(
		status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}

	@Test // tests DELETE to delete all employees
	public void shouldDeleteAllEmployees() throws Exception {

		mockMvc.perform(post("/timeoff/employees/").content(
		"{\"hours\": 24}")).andExpect(
		status().isCreated()).andReturn();

		mockMvc.perform(post("/timeoff/employees/").content(
		"{\"hours\": 24}")).andExpect(
		status().isCreated()).andReturn();

		mockMvc.perform(delete("/timeoff/employees/")).andExpect(status().isNoContent());

		mockMvc.perform(get("/timeoff/employees/")).andExpect(status().isOk()).andExpect(
		jsonPath("$.length()").value("0"));
	}

	@Test
	public void shouldCreateRequest() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/timeoff/employees/").param("hours", "24")).andExpect(
		status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		location = location.substring(location.length()-24);
		System.out.println(location); // TODO remove

		mockMvc.perform(post("/timeoff/requests/").param("id", location).param(
		"hours", "8").param("year", "2019").param("month", "1").param("day", "25")).andExpect(
		status().isCreated()).andReturn();
	}

	@Test // tests GET to fetch requests of an employee
	public void shouldRetrieveEmployeeRequests() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/timeoff/employees/").param("hours", "24")).andExpect(
		status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		location = location.substring(location.length()-24);
		System.out.println(location); // TODO remove

		mvcResult = mockMvc.perform(post("/timeoff/requests/").param("id", location).param(
		"hours", "8").param("year", "2019").param("month", "1").param("day", "25")).andExpect(
		status().isCreated()).andReturn();
		location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
		jsonPath("$.2019-01-25").value("8"));
	}

}
