package com.jun.smartlineup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartlineupApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartlineupApplication.class, args);
	}

}
