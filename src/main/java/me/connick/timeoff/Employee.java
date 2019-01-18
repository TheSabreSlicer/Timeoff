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
    if(numHours > this.hours){
      return false;
    }
    String date = LocalDate.of(year, month, day).toString();
    this.hours -= numHours;
    // putIfAbsent returns null if key isn't already mapped, otherwise returns value already stored
    if(this.requests.putIfAbsent(date, numHours) != null){
      int check = this.requests.get(date);
      this.requests.put(date, check+numHours);
    }
    return true;
  }
}
