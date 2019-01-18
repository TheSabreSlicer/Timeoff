package me.connick.timeoff;

import org.springframework.data.mongodb.repository.MongoRepository;

// repository that contains employees and stores them in a local mongodb database
public interface EmployeeRepository extends MongoRepository<Employee, String> {
  // finds employees by their employeeId
  public Employee findByEmployeeId(String employeeId);
}
