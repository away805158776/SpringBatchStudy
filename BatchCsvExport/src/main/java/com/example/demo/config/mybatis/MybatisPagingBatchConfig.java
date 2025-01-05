package com.example.demo.config.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

@Configuration
public class MybatisPagingBatchConfig extends BaseConfig {
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Bean
	@StepScope
	MyBatisPagingItemReader<Employee> mybatisPagingReader() {
		
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);
		
		return new MyBatisPagingItemReaderBuilder<Employee>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.example.demo.repository.EmployeeMapper.findByGenderPaging")
				.parameterValues(parameterValues)
				.pageSize(10)
				.build();
	}
	
	@Bean
	Step exportMybatisPagingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
		return new StepBuilder("ExportMybatisPagingStep", jobRepository)
				.<Employee, Employee>chunk(10, transactionManager)
				.reader(mybatisPagingReader()).listener(readListener)
				.processor(this.genderConvertProcessor)
				.writer(csvWriter()).listener(writeListener)
				.build();
	}
	
	@Bean("MybatisPagingJob")
	Job exportMybatisPagingJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step exportMybatisPagingStep) throws Exception{
		return new JobBuilder("ExportMybatisPagingJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(exportMybatisPagingStep)
				.build();
	}
}
