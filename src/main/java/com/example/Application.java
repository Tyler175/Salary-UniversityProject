package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
//DTO is not used, i'm using JsonView instead, i don't know if this is correct in this case
//Get user by name doesn't work properly in case if we send full name instead it's part AND it doesn't work with email
//You don't use pagination
//You name your components, attributes, css-classes in different ways
//After you CHANGE user role to accountant, workerInfo still not null

