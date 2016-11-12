package com.hui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AutoEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoEmailApplication.class, args);
	}
}
