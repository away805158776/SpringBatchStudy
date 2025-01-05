package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
	
	@Autowired
	private ItemReader<String> reader;
	
	@Autowired
	private ItemProcessor<String, String> processor;
	
	@Autowired
	private ItemWriter<String> writer;

	@Autowired
	private JobExecutionListener jobListener;
	
	@Autowired
	private StepExecutionListener stepListener;
	
    @Bean
    Step chunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("HelloChunkStep", jobRepository)
				.<String, String>chunk(3, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.listener(stepListener)
				.build();
	}
    

    @Bean
    Job chunkJob (JobRepository jobRepository, PlatformTransactionManager transactionManager, Step chunkStep) throws Exception{
		return new JobBuilder("HelloWorldChunkJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(chunkStep)
				.listener(jobListener)
				.build();
	}
}








