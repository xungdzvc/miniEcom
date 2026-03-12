package com.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MiniECommerceApplication {

	public static void main(String[] args) {
		System.out.println("---Backend Service Started---");
		SpringApplication.run(MiniECommerceApplication.class, args);
	}

}
