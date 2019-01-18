# Timeoff
#### A Spring Boot application that manages PTO requests for employees through a REST API.

#### Requirements:
A running local installation of mongodb is required.

To run tests (macOS):
```
./gradlew test
```

To run tests (Windows):
```
gradlew.bat test
```

To run (macOS):
```
./gradlew bootRun
```

To run tests (Windows):
```
gradlew.bat bootRun
```

The API provides two endpoints:

##### Employees Endpoint

```
POST <base-url>/timeoff/employees/
```
description: creates a new employee in the database
required parameters: none
optional parameters: hours
notes: hours must be 0 or greater

```
PUT <base-url>/timeoff/employees/{id}
```
description: updates specified employee in the database
required parameters: hours
optional parameters: none
notes: hours must be 0 or greater

```
GET <base-url>/timeoff/employees/{id}
```
description: gets hours of specified employee
required parameters: none
optional parameters: none
notes: none

```
GET <base-url>/timeoff/employees/
```
description: gets all employees in database
required parameters: none
optional parameters: none
notes: none

```
DELETE <base-url>/timeoff/employees/
```
description: deletes all employees in the database
required parameters: none
optional parameters: none
notes: none

```
DELETE <base-url>/timeoff/employees/{id}
```
description: deletes an employee from the database
required parameters: none
optional parameters: none
notes: none


##### Requests Endpoint

```
POST <base-url>/timeoff/requests/
```
description: creates a new PTO record in the database if possible
required parameters: id, hours, year, month, day
optional parameters: hours
notes: hours must be between 1-24, date must be valid and not in the past

```
GET <base-url>/timeoff/requests/{id}
```
description: gets all PTO records for the specified employee
required parameters: none
optional parameters: hours
notes: none
