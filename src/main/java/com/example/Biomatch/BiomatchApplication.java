package com.example.Biomatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BiomatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiomatchApplication.class, args);
	}

}
