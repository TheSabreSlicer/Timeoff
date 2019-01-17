package me.connick.timeoff;

import org.springframework.data.annotation.Id;

public class Employee {

  @Id
  private String employeeId; // internal ID for mongo use
  private int hours;

  public Employee() {}

  public Employee(int hours) {
    this.hours = hours;
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

  public void subtractHours(int amount) {
    this.hours -= amount;
  }
}
