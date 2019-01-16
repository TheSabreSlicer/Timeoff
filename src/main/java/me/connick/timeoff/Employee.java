package me.connick.timeoff;

import org.springframework.data.annotation.Id;

public class Employee {

    @Id
    private String id;
    private int availableHours;

    public Employee(int hours) {
        this.availableHours = hours;
    }

    public String getId() {
        return id;
    }

    public int getHours() {
        return availableHours;
    }

    public void setHours(int newHours) {
        this.availableHours = newHours;
    }

    public void subtractHours(int amount) {
        this.availableHours -= amount;
    }
}