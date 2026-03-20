package com.bustracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusTrackingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusTrackingBackendApplication.class, args);
	}
}