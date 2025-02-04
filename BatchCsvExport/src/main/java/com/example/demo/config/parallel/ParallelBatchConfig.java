package com.example.demo.config.parallel;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.repeat.RepeatOperations;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

@Configuration
public class ParallelBatchConfig extends BaseConfig {
	
	@Autowired
	private JdbcPagingItemReader<Employee> jdbcPagingReader;
	
//	@Bean
//	TaskExecutor asyncTaskExecutor() {
//		return new SimpleAsyncTaskExecutor("parallel_");
//	}

	@Bean
	TaskExecutor asyncTaskExecutor() {
	    SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("parallel_");
	    executor.setConcurrencyLimit(3); // 並列スレッド数を3に設定
	    return executor;
	}

	@Bean
	RepeatOperations customRepeatOperations() {
	    RepeatTemplate repeatTemplate = new RepeatTemplate();
	    return repeatTemplate;
	}	
	
	
	@Bean
	Step exportParallelStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
		return new StepBuilder("ExportParallelStep", jobRepository)
				.<Employee, Employee>chunk(10, transactionManager)
				.reader(jdbcPagingReader).listener(readListener)
				.processor(this.genderConvertProcessor)
				.writer(csvWriter()).listener(writeListener)
//				.taskExecutor(asyncTaskExecutor())
//				.throttleLimit(3)
	            .taskExecutor(asyncTaskExecutor()) // TaskExecutorを使用
	            .stepOperations(customRepeatOperations()) // カスタムRepeatOperationsを設定				
				.build();
	}
	
	@Bean
	Job exportParallelJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step exportParallelStep) throws Exception{
		return new JobBuilder("ExportParallelJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(exportParallelStep)
				.build();
	}
}


