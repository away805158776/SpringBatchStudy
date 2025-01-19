package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

	@Autowired
	private Partitioner samplePartitioner;

	@Autowired
	private Tasklet workerTasklet;
		
	@Bean
	TaskExecutor asyncTaskExecutor() {
		return new SimpleAsyncTaskExecutor("worker_");
	}
	
	@Bean
	Step workerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("WorkerStep", jobRepository)
				.tasklet(workerTasklet, transactionManager)
				.build();
	}

	@Bean
	Step partitionStep(JobRepository jobRepository, 
			PlatformTransactionManager transactionManager,
			Step workerStep) {
		return new StepBuilder("partitionStep", jobRepository)
				.partitioner("WorkerStep", samplePartitioner)
				.step(workerStep)
				.gridSize(3)
				.taskExecutor(asyncTaskExecutor())
				.build();
	}
	
	@Bean
	Job partitionJob(JobRepository jobRepository, 
			PlatformTransactionManager transactionManager,
			Step partitionStep) throws Exception{
		return new JobBuilder("PartitionJob", jobRepository)
				.start(partitionStep)
				.build();
	}	
}
