package com.richards.projectmanagement;

import org.springframework.boot.SpringApplication;

public class TestProjectManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(ProjectManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
