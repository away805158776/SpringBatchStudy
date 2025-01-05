package com.example.demo.config.jpa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class JpaPagingBatchConfig extends BaseConfig {
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Bean
	@StepScope
	JpaPagingItemReader<Employee> jpaPagingReader() {
		
		String sql = "select * from employee where gender = :genderParam order by id";
		
		JpaNativeQueryProvider<Employee> queryProvider = new JpaNativeQueryProvider<>();
		queryProvider.setSqlQuery(sql);
		queryProvider.setEntityClass(Employee.class);
		
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);
		
		return new JpaPagingItemReaderBuilder<Employee>()
				.entityManagerFactory(entityManagerFactory)
				.name("jpaPagingItemReader")
				.queryProvider(queryProvider)
				.parameterValues(parameterValues)
				.pageSize(5)
				.build();
	}
	
	@Bean
	Step exportJpaPagingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
		return new StepBuilder("ExportJpaPagingStep", jobRepository)
				.<Employee, Employee>chunk(10, transactionManager)
				.reader(jpaPagingReader()).listener(readListener)
				.processor(this.genderConvertProcessor)
				.writer(csvWriter()).listener(writeListener)
				.build();
	}
	
	@Bean("JpaPagingJob")
	Job exportJpaPagingJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step exportJpaPagingStep) throws Exception{
		return new JobBuilder("ExportJpaPagingJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(exportJpaPagingStep)
				.build();
	}
}
