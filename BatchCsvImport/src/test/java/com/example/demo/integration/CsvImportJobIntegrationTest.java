package com.example.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;

import com.example.demo.BatchCsvImportApplication;
import com.example.demo.domain.model.Employee;

@SpringBatchTest
@ContextConfiguration(classes = {BatchCsvImportApplication.class})
//@SpringBootTest(classes = BatchCsvImportApplication.class)
//@ContextConfiguration(classes = {
//		SkipImportBatchConfig.class,     
//		MyBatisImportBatchConfig.class, // mybatisWriterを提供
//	    GenderConvertProcessor.class,   // genderConvertProcessorを提供
//	    ExistsCheckProcessor.class,      // existsCheckProcessorを提供
//	    ReadListener.class,
//	    ProcessListener.class,
//	    WriteListener.class,
//	    SampleProperty.class,
//	    EmployeeSkipListener.class,
//	    MyBatisImportBatchConfig.class
//})
@DisplayName("CsvImportJobのIntegrationTest")
public class CsvImportJobIntegrationTest {
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static String SQL = "select * from employee order by id";
	
	private RowMapper<Employee> rowMapper = new BeanPropertyRowMapper<Employee>(Employee.class);
	
	@Test
	@DisplayName("ユーザーがインポートされていること")
	public void jobTest() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addLong("time", System.currentTimeMillis()) // ユニークなパラメータ
//                .toJobParameters();
//        
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		
		jobExecution.getStepExecutions().forEach(stepExecution -> assertThat(ExitStatus.COMPLETED).isEqualTo(stepExecution.getExitStatus()));
		
		assertThat(ExitStatus.COMPLETED).isEqualTo(jobExecution.getExitStatus());
		
		List<Employee> resultList = jdbcTemplate.query(SQL, rowMapper);
		
		assertThat(resultList.size()).isEqualTo(2);
		
		Employee employee1 = resultList.get(0);
		assertThat(employee1.getName()).isEqualTo("テストユーザー1");

		Employee employee2 = resultList.get(1);
		assertThat(employee2.getName()).isEqualTo("テストユーザー2");
	}
}
