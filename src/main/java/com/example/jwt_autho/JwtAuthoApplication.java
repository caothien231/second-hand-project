package com.example.jwt_autho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication
@PropertySource("classpath:env.properties")
public class JwtAuthoApplication {

	//testing git
	public static void main(String[] args) {
		SpringApplication.run(JwtAuthoApplication.class, args);
		
	}

}
