package com.meufty.workoutplanner;

import com.meufty.workoutplanner.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class WorkoutplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkoutplannerApplication.class, args);
	}

}
