package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

	@Autowired
	@Qualifier("FirstTasklet")
	private Tasklet firstTasklet;

	@Autowired
	@Qualifier("SecondTasklet")
	private Tasklet secondTasklet;
	
	@Autowired
	@Qualifier("ThirdTasklet")
	private Tasklet thirdTasklet;
	
	@Bean
	Step firstStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("FirstStep", jobRepository)
				.tasklet(firstTasklet, transactionManager)
				.build();
	}

	@Bean
	Step secondStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("SecondStep", jobRepository)
				.tasklet(secondTasklet, transactionManager)
				.build();
	}

	@Bean
	Step thirdStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("ThirdStep", jobRepository)
				.tasklet(thirdTasklet, transactionManager)
				.build();
	}

	@Bean
	Flow firstFlow(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step firstStep) {
		return new FlowBuilder<SimpleFlow>("FirstFlow")
				.start(firstStep)
				.build();
	}
	
	@Bean
	Flow secondFlow(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step secondStep) {
		return new FlowBuilder<SimpleFlow>("SecondFlow")
				.start(secondStep)
				.build();
	}

	@Bean
	Flow thirdFlow(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step thirdStep) {
		return new FlowBuilder<SimpleFlow>("ThirdFlow")
				.start(thirdStep)
				.build();
	}
	
	@Bean
	TaskExecutor asyncTaskExecutor() {
		return new SimpleAsyncTaskExecutor("concurrent_");
	}

	@Bean
	Flow splitFlow(JobRepository jobRepository, 
			PlatformTransactionManager transactionManager,
			Flow secondFlow,
			Flow thirdFlow) {
		return new FlowBuilder<SimpleFlow>("splitFlow")
				.split(asyncTaskExecutor())
				.add(secondFlow, thirdFlow)
				.build();
	}
	
	@Bean
	Job concurrentJob(JobRepository jobRepository, 
			PlatformTransactionManager transactionManager,
			Flow firstFlow,
			Flow splitFlow) throws Exception{
		return new JobBuilder("ConcurrentJob", jobRepository)
				.incrementer(new RunIdIncrementer())
					.start(firstFlow)
					.next(splitFlow)
					.build()
				.build();
	}	
}
