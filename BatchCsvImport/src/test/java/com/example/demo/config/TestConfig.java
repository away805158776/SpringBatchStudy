// 本に書いたこの部分は間違いです、これを使うと14.3.2インポートの結合テストが実行エラーになる
/**
 * エラーメッセージ：
 *  org.springframework.beans.factory.BeanCreationException: 
 *  Error creating bean with name 'jobLauncherApplicationRunner' defined in class path resource 
 *  [org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration.class]: 
 *  Job name must be specified in case of multiple jobs:
 */


/*	
package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {
	
	@Autowired
//	@Qualifier("JdbcJob")
	@Qualifier("CsvImportJdbcJob")
	private Job jdbcJob;

    @Bean
    @Primary
    Job testJob() {
		return jdbcJob;
	}
}
 */