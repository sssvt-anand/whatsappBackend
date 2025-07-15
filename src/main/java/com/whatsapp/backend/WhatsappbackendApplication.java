package com.whatsapp.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WhatsappbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhatsappbackendApplication.class, args);
	}

}
