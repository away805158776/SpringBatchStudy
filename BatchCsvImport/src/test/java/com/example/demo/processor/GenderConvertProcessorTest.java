package com.example.demo.processor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.domain.model.Employee;



@ExtendWith(MockitoExtension.class)
@DisplayName("Tasklet1のUnitTest")
public class GenderConvertProcessorTest {
	
	@InjectMocks
	private GenderConvertProcessor processor;
	
	@Test
	@DisplayName("男性という文字列が1に変換されていること")
	public void convertMale() throws Exception {
		
		Employee employee = new Employee();
		employee.setGenderString("男性");
		
		Employee result = processor.process(employee);
		
		assertThat(result.getGender()).isEqualTo(1);
	}
	
	@Test
	@DisplayName("女性という文字列が2に変換されていること")
	public void convertFemale() throws Exception {
		
		Employee employee = new Employee();
		employee.setGenderString("女性");
		
		Employee result = processor.process(employee);
		
		assertThat(result.getGender()).isEqualTo(2);
	}

	@Test
	@DisplayName("変換が失敗して例外が発生すること")
	public void convertFail() throws Exception {
		
		Employee employee = new Employee();
		
		Employee result = processor.process(employee);
		
		assertThat(result).isNull();
	}

	
}
