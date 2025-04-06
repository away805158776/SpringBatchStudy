package com.example.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.example.demo.BatchCsvExportApplication;
import com.example.demo.config.SampleProperty;



@SpringBatchTest
@ContextConfiguration(classes = {BatchCsvExportApplication.class})

/**
 *なぜかapplication.ymlが読み込みがうまく、spring.batch.job.enabled=falseを読み取れないので、
 *こちらでアノテーションとして設定します。
 *Job name must be specified in case of multiple jobsを解決するために。
 */
@TestPropertySource(properties = {"spring.batch.job.enabled=false"})

@DisplayName("CsvExportJobのIntegrationTest")
public class CsvExportIntegrationTest {
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	// here different from the book
	@Autowired
	@Qualifier("ExportJdbcCursorJob")
	private Job exportJdbcCursorJob;
	
	@Autowired
	private SampleProperty property;
	
	private static final String EXPECTED_FILE_PATH = "src/test/resources/file/result.csv";
	
	@Test
	@Sql("/sql/test_data.sql")
	@DisplayName("ファイルが出力されていること")
	public void checkStatus() throws Exception {

		// here different from the book
		jobLauncherTestUtils.setJob(exportJdbcCursorJob);
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
	        
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		
		
//		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		
		
		jobExecution.getStepExecutions().forEach(stepExecution -> assertThat(ExitStatus.COMPLETED).isEqualTo(stepExecution.getExitStatus()));
		
		assertThat(ExitStatus.COMPLETED).isEqualTo(jobExecution.getExitStatus());
		
//		AssertFile.assertFileEquals(
//				new FileSystemResource(EXPECTED_FILE_PATH),
//				new FileSystemResource(property.outputPath()));
		
		// 使用 AssertJ 比较文件
	    File expectedFile = new FileSystemResource(EXPECTED_FILE_PATH).getFile();
	    File actualFile = new FileSystemResource(property.outputPath()).getFile();
	    assertThat(actualFile).hasSameTextualContentAs(expectedFile);
	    
	    
	}
	
	
}
