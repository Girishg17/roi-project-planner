package com.github.rblessings;

import org.springframework.boot.SpringApplication;

public class TestRoiProjectPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.from(RoiProjectPlannerApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}
}
