package com.example.demo.config.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
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
public class MybatisCursorBatchConfig extends BaseConfig {
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Bean
	@StepScope
	MyBatisCursorItemReader<Employee> mybatisCursorReader() {
		
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);
		
		return new MyBatisCursorItemReaderBuilder<Employee>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.example.demo.repository.EmployeeMapper.findByGender")
				.parameterValues(parameterValues)
				.build();
	}
	
	@Bean
	Step exportMybatisCursorStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
		return new StepBuilder("ExportMybatisCursorStep", jobRepository)
				.<Employee, Employee>chunk(10, transactionManager)
				.reader(mybatisCursorReader()).listener(readListener)
				.processor(this.genderConvertProcessor)
				.writer(csvWriter()).listener(writeListener)
				.build();
	}
	
	@Bean("MybatisCursorJob")
	Job exportMybatisCursorJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, Step exportMybatisCursorStep) throws Exception{
		return new JobBuilder("ExportMybatisCursorJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(exportMybatisCursorStep)
				.build();
	}
}
