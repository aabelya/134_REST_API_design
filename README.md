# REST API Architecture Module Practical Task

## TASK 1. BUILDING A REST API

### 1. Set Up the Project Environment _(step 1-3 = 55 points)_
- **Create a New Spring Boot Project:**
    ![img.png](img.png)

- **Configure the Application Properties:** 
    - [application.properties](src/main/resources/application.properties)
    - [application-dev.properties](src/main/resources/application-dev.properties)\
    - [application-prod.properties](src/main/resources/application-prod.properties)
    

### 2. Define the Data Model _(5 points)_
- **Create a User Entity:**
    - [User.java](src/main/java/com/epam/module4/domain/User.java)
  
- **Create a User Repository:**
    - [UserRepository.java](src/main/java/com/epam/module4/dao/UserRepository.java)
  
#### Implementation notes
User entity consist of 
- `id` (primary key) \
data fields: 
- `username` (non null, unique)
- `password` (non null)
- `email` (unique)
- `comment` \
permission fields
- `role` (non null)
- `authorities` (non null) \
and some service fields
- 'createdBy' (non null)
- 'updatedBy' (non null)
- 'createdAt' (non null)
- 'lastUpdatedAt' (non null)

Snippet from the log
```
Hibernate: 
    create sequence users_seq start with 1 increment by 50
Hibernate: 
    create table users (
        id integer not null,
        created_at timestamp(6) not null,
        last_updated_at timestamp(6) not null,
        authorities varchar(255) not null,
        comment varchar(255),
        created_by varchar(255) not null,
        email varchar(255) unique,
        password varchar(255) not null,
        updated_by varchar(255) not null,
        username varchar(255) not null unique,
        role enum ('ADMIN','OBSERVER','USER') not null,
        primary key (id)
    )   
```

### 3. Implement CRUD Operations _(50 points)_
- **Create a User Service:**
    - [UserService.java](src/main/java/com/epam/module4/service/UserService.java)
- **Create a User Controller:**
    - [UserController.java](src/main/java/com/epam/module4/controller/UserController.java)

#### Implementation notes

##### Available endpoints:

| Title            | Method | URL                       | Description                            | Requires authorization | 
|------------------|--------|---------------------------|----------------------------------------|------------------------|
| greeting         | GET    | /users/hello              | Prints hello message when user logs in | yes                    |
| register         | POST   | /users/register           | Register and log in as new user        | no                     |
| createUser       | POST   | /users/create             | Creates new user                       | yes                    |
| getAllUsers      | GET    | /users                    | Get all users                          | yes                    |
| getUser by id    | GET    | /users/{id}               | Get user by id                         | yes                    |
| getUser by name  | GET    | /users/by-name/{username} | Get user by name                       | yes                    |
| updateUser       | PUT    | /users/{id}               | Update user by id                      | yes                    |
| updateUserAccess | PUT    | /users/access/{id}        | Update user permissions                | yes                    |
| deleteUser       | DELETE | /users/{id}               | Delete user by id                      | yes                    |

##### Solutions/tools used
- Spring Web - for building restful API
- Spring HATEOAS - for hypermedia support
- Spring Bean Validation with groups - for request parameters validation
- BCryptPasswordEncoder - for password encoding
- Jackson ObjectMapper - for json/object conversion 
- Spring Boot Actuator - just because

#### Demo

- admin user created during startup
 ![img_2.png](img_2.png)


- setting up bash
![img_3.png](img_3.png)


- testing health endpoint 
![img_4.png](img_4.png)


- logging in as admin (using spring default login endpoint)
- creating new user 'john' as admin
![img_5.png](img_5.png)


- registering as new user 'jane'
- viewing created user
![img_6.png](img_6.png)


- logging in as john (using spring default login endpoint)
- updating an email and comment
![img_7.png](img_7.png)
 

- as admin assigning john the OBSERVER role
![img_8.png](img_8.png)


- check validation - as admin attempt to create user without password
- check validation - as admin attempt to set malformed email
- check validation - as admin attempt to manipulate createdBy field
- check validation - as admin attempt to supply extra parameters when updating acces
![img_12.png](img_12.png)


- as admin creating and deleting a user
- checking user no longer exists
- ![img_9.png](img_9.png)


- as admin viewing all the users
![img_10.png](img_10.png)



### 4. Implement Authentication and Authorization _(5 points)_
- **Configure Security:**
  - [DbUserDetailsService.java](src/main/java/com/epam/module4/service/DbUserDetailsService.java)
  - [WebSecurityConfig.java](src/main/java/com/epam/module4/config/WebSecurityConfig.java)

#### Implementation notes
- `DbUserDetailsService` is a custom `UserDetailsService` that uses app's h2 database to authenticate and authorized users
- `WebSecurityConfig` controls endpoint access based on user `role` and `authorities`
- Additional checks implemented on the controller level to ensure that non-privileged can only read/update themselves

#### Demo 
- logging in as jane with wrong password (using spring default login endpoint)
- logging in as jane (using spring default login endpoint)
![img_11.png](img_11.png)


- as jane (`USER` role - non-privileged) successfully viewing/updating their own user
- as jane (`USER` role - non-privileged) failing to view/update other users
![img_13.png](img_13.png)

  
- as john (`OBSERVER` role - privileged) successfully to view/update other users
![img_14.png](img_14.png)


- as john (doesn't have `CREATE` or `DELETE` authority) failing to create/delete users
- as admin (has `CREATE` and `DELETE` authority) successfully creating/deleting users
![img_15.png](img_15.png)


- as john (doesn't have `MANAGE_ACCESS` authority) failing to change role
- as admin (has `MANAGE_ACCESS` authority) successfully changing role
![img_16.png](img_16.png)


### 5. Implement Error Handling _(5 points)_
- **Create a Global Exception Handler:**
  - [ErrorHandlingControllerAdvice.java](src/main/java/com/epam/module4/controller/ErrorHandlingControllerAdvice.java)
  - [ExtendedErrorAttributes.java](src/main/java/com/epam/module4/controller/ExtendedErrorAttributes.java)
- **Define Error Response Structure:**
  - [ValidationErrorResponse.java](src/main/java/com/epam/module4/controller/validation/ValidationErrorResponse.java)

#### Implementation notes
- Handling is implement partially with `@ControllerAdvice` and partially by customizing error attributes of the default `BasicErrorController` with `ExtendedErrorAttributes`
- 3 application exceptions were created: `RequestConflictException.java`,  `UserOperationNotAuthorizedException.java` and `UserNotFoundException.java` 
- `@ResponseStatus(code = ...)` is used to set default status for the 3 application exceptions

#### Demo

- `ErrorHandlingControllerAdvice` handles SQL exception
![img_17.png](img_17.png)


- `ExtendedErrorAttributes` uses `ValidationErrorResponse` to collect validation fails
![img_18.png](img_18.png)

### 6. Implement Versioning _(5 points)_
Versioning is implemented for `/users` endpoint as content negotiation: 
- `application/json` returns plain DTOs
- `application/hal+json` supports pagination and HATEOAS

| application/json           | application/hal+json | 
|----------------------------|--------|
| ![img_19.png](img_19.png)  | ![img_20.png](img_20.png) |



### 7. Implement Pagination _(5 points)_
- **Add Pagination to the User Controller:**
![img_21.png](img_21.png)




