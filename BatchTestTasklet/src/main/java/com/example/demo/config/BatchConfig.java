package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
	
	@Autowired
	@Qualifier("Tasklet1")
	private Tasklet tasklet1;

	@Autowired
	@Qualifier("Tasklet2")
	private Tasklet tasklet2;
	
	@Bean
	Step testStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("Step1", jobRepository)
				.tasklet(tasklet1, transactionManager)
				.build();
	}

	@Bean
	Step testStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("Step2", jobRepository)
				.tasklet(tasklet2, transactionManager)
				.build();
	}
	
	@Bean
	Job testTaskletJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step testStep1, Step testStep2) {
		return new JobBuilder("TaskletJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(testStep1)
				.next(testStep2)
				.build();
	}
}
