package me.connick.timeoff;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Employee {

  @Id
  private String employeeId; // internal ID for mongo use
  private int hours; // number of available hours of PTO
  private Map<String, Integer> requests; // map of all past, present and future PTO

  public Employee() {}

  public Employee(int hours) throws InvalidHoursException {
    if(hours < 0){
      throw new InvalidHoursException();
    }
    this.hours = hours;
    this.requests = new HashMap<String, Integer>();
  }

  public String getId() {
    return employeeId;
  }

  public int getHours() {
    return hours;
  }

  // sets a new number of hours
  public void setHours(int newHours) throws InvalidHoursException {
    if(newHours < 0){
      throw new InvalidHoursException();
    }
    this.hours = newHours;
  }

  // returns requests map
  public Map<String, Integer> getRequests(){
    return this.requests;
  }

  // takes a number of hours, and a date, and attempts to schedule a PTO request
  public void timeoffRequest(int numHours, int year, int month, int day)
    throws DateTimeException, InvalidDateException, TooFewHoursException, HoursRangeException {
    // make sure the employee has enough hours available
    if(numHours > this.hours){
      throw new TooFewHoursException();
    }
    // make sure the hours requested are in the valid range
    if(numHours < 1 | numHours > 24){
      throw new HoursRangeException();
    }
    // try converting date to string, catching errors
    String date;
    try{
      date = LocalDate.of(year, month, day).toString();
    } catch(DateTimeException e){
      throw new InvalidDateException();
    }
    // make sure the date is not in the past
    if(LocalDate.now().isAfter(LocalDate.of(year, month, day))){
      throw new InvalidDateException();
    }
    this.hours -= numHours;
    // putIfAbsent returns null if key isn't already mapped, otherwise returns value already stored
    if(this.requests.putIfAbsent(date, numHours) != null){
      int stored = this.requests.get(date);
      this.requests.put(date, stored+numHours);
    }
  }

  // various exception handlers for employee related errors

  // handles a response if supplied hours are < 0
	@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Hours Cannot be < 0")
	public class InvalidHoursException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

  // handles a response if invalid date is supplied
	@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Invalid or Past Date")
	public class InvalidDateException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	// handles a response if invalid date is supplied
	@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Too Few Hours Available")
	public class TooFewHoursException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

  // handles a response if invalid date is supplied
	@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Requested Hours must be between 1-24")
	public class HoursRangeException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
}
