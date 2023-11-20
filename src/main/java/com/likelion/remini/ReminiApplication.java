package com.likelion.remini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ReminiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReminiApplication.class, args);
	}

}
