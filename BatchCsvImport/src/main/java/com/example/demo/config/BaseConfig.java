package com.example.demo.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.domain.model.Employee;

@Configuration
public abstract class BaseConfig {
	
	@Autowired
	@Qualifier("GenderConvertProcessor")
	protected ItemProcessor<Employee, Employee> genderConvertProcessor;
	
	@Autowired
	@Qualifier("ExistsCheckProcessor")
	protected ItemProcessor<Employee, Employee> existsCheckProcessor;
	
	@Autowired
	protected ItemReadListener<Employee> readListener;
	
	@Autowired
	protected ItemProcessListener<Employee, Employee> processListener;
	
	@Autowired
	protected ItemWriteListener<Employee> writeListener;

	@Autowired
	protected SampleProperty property;
	
	@Bean
	@StepScope
	FlatFileItemReader<Employee> csvReader(){
		String[] nameArray = new String[] {"id", "name", "age", "genderString"};
		
		return new FlatFileItemReaderBuilder<Employee>()
				.name("employeeCsvReader")
				.resource(new ClassPathResource(property.getCsvPath()))
				.linesToSkip(1)
				.encoding(StandardCharsets.UTF_8.name())
				.delimited()
				.names(nameArray)
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {
					{
						setTargetType(Employee.class);
					}
				})
				.build();
	}
	
	@Bean
	@StepScope
	ItemProcessor<Employee, Employee> compositeProcessor(){
		
		CompositeItemProcessor<Employee, Employee> compositeProcessor = new CompositeItemProcessor<>();
		
		compositeProcessor.setDelegates(Arrays.asList(validationProcessor(),
				this.existsCheckProcessor, 
				this.genderConvertProcessor));
		
		return compositeProcessor;
	}
	
	@Bean
	@StepScope
	BeanValidatingItemProcessor<Employee> validationProcessor(){
		BeanValidatingItemProcessor<Employee> validationProcessor = new BeanValidatingItemProcessor<>();
		
		validationProcessor.setFilter(true);
		
		return validationProcessor;
	}
}