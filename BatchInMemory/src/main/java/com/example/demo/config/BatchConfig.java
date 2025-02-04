package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.domain.Employee;

@Configuration
public class BatchConfig {
	
	@Autowired
	private ItemReader<Employee> employeeReader;
	
	@Bean
	@ConfigurationProperties("spring.datasource.primary")
	DataSourceProperties h2Properties() {
	    return new DataSourceProperties();
	  }
	
	@Bean
	@ConfigurationProperties("spring.datasource.secondary")
	DataSourceProperties postgreProperties() {
	    return new DataSourceProperties();
	  }
	
	@BatchDataSource
	@Bean
	DataSource h2DataSource() {
	    return h2Properties()
	        .initializeDataSourceBuilder()
	          .build();
	  }
	
	@Primary
	@Bean
	DataSource postgreDataSource() {
	    return postgreProperties()
	        .initializeDataSourceBuilder()
	          .build();
	  }
	
	@Autowired
	@Qualifier("JpaWriter")
	private ItemWriter<Employee> jpaWriter;
	
	private static final String INSERT_EMPLOYEE_SQL = "INSERT INTO employee(id, name, age, gender)" + "VALUES(:id, :name, :age, :gender)";
	
	@Bean
	@StepScope
	JdbcBatchItemWriter<Employee> jdbcWriter() {
		BeanPropertyItemSqlParameterSourceProvider<Employee> provider = new BeanPropertyItemSqlParameterSourceProvider<>();
		return new JdbcBatchItemWriterBuilder<Employee>()
				.itemSqlParameterSourceProvider(provider)
				.sql(INSERT_EMPLOYEE_SQL)
				.dataSource(postgreDataSource())
				.build();
	}
	
	@Bean
	Step inMemoryStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("InMemoryStep", jobRepository)
				.<Employee, Employee>chunk(1, transactionManager)
				.reader(employeeReader)
				.writer(jpaWriter)
				.build();
	}
	
	@Bean
	Job inMemoryJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step inMemoryStep) {
		return new JobBuilder("InMemoryJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(inMemoryStep)
				.build();
	}	
}
