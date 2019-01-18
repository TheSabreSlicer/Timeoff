package me.connick.timeoff;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;

public class Employee {

  @Id
  private String employeeId; // internal ID for mongo use
  private int hours; // number of available hours of PTO
  private Map<String, Integer> requests; // map of all past, present and future PTO

  public Employee() {}

  public Employee(int hours) {
    this.hours = hours;
    this.requests = new HashMap<String, Integer>();
  }

  public String getId() {
    return employeeId;
  }

  public int getHours() {
    return hours;
  }

  public void setHours(int newHours) {
    this.hours = newHours;
  }

  public Map<String, Integer> getRequests(){
    return this.requests;
  }

  // takes a number of hours, and a date, and returns true if successful or false otherwise
  public boolean timeoffRequest(int numHours, int year, int month, int day) throws DateTimeException{
    // make sure the employee has enough hours and that the requested hours are between 1-24
    if(numHours > this.hours | numHours < 1 | numHours > 24){
      return false;
    }
    // if the date is in the past, return false, this will also verify the passed date is valid
    if(LocalDate.now().isAfter(LocalDate.of(year, month, day))){
      return false;
    }
    // convert to string
    String date = LocalDate.of(year, month, day).toString();
    this.hours -= numHours;
    // putIfAbsent returns null if key isn't already mapped, otherwise returns value already stored
    if(this.requests.putIfAbsent(date, numHours) != null){
      int stored = this.requests.get(date);
      this.requests.put(date, stored+numHours);
    }
    return true;
  }
}
