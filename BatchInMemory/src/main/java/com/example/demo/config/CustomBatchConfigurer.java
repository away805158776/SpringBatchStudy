//package com.example.demo.config;
//
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
//import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import jakarta.annotation.PostConstruct;
//
//@Component
//@Configuration
//public class CustomBatchConfigurer{
//	
//	private JobRepository jobRepository;
//	private JobExplorer jobExplorer;
//	private JobLauncher jobLauncher;
//	private PlatformTransactionManager transactionManager;
//	
//	@PostConstruct
//	public void init() {
//		JobRepositoryFactoryBean jobRepositoryFactory = new JobRepositoryFactoryBean();
//		
//		try {
//			this.transactionManager = new ResourcelessTransactionManager();
//			jobRepositoryFactory.setTransactionManager(transactionManager);
//			jobRepositoryFactory.afterPropertiesSet();
//			this.jobRepository = jobRepositoryFactory.getObject();
//			
//			JobExplorerFactoryBean jobExplorerFactory = new JobExplorerFactoryBean(jobRepositoryFactory);
//			jobExplorerFactory.afterPropertiesSet();
//			this.jobExplorer = jobExplorerFactory.getObject();
//			
//			SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
//			jobLauncher.setJobRepository(jobRepository);
//			jobLauncher.afterPropertiesSet();
//			this.jobLauncher = jobLauncher;
//		} catch (Exception e) {
//			throw new IllegalStateException("Initialization failure", e);
//		}
//	}
//
//	@Override
//	public JobRepository getJobRepository() throws Exception {
//		return jobRepository;
//	}
//	
//	@Override
//	public JobExplorer getJobExplorer() {
//		return jobExplorer;
//	}
//	
//	@Override
//	public JobLauncher getJobLauncher() {
//		return jobLauncher;
//	}
//	
//	@Override
//	public PlatformTransactionManager getTransactionManager() {
//		return transactionManager;
//	}
//}



//package com.example.demo.config;
//
//import javax.sql.DataSource;
//
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
//import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.SimpleAsyncTaskExecutor;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//
////@Configuration
////@Component
//@EnableBatchProcessing
//public class CustomBatchConfigurer {
//
//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
//
//    // 构造函数注入 DataSource 和 PlatformTransactionManager
//    public CustomBatchConfigurer(DataSource dataSource, PlatformTransactionManager transactionManager) {
//        this.dataSource = dataSource;
//        this.transactionManager = transactionManager;
//    }
//
//    @Bean(name = "customJobRepository")
//    JobRepository jobRepository() throws Exception {
//        // 配置 JobRepositoryFactoryBean
//        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
//        factoryBean.setDataSource(dataSource);  // 注入 DataSource
//        factoryBean.setTransactionManager(transactionManager);  // 注入事务管理器
//        return factoryBean.getObject();  // 返回 JobRepository 实例
//    }
//
//    @Bean(name = "customJobExplorer")
//    JobExplorer jobExplorer(JobRepository jobRepository) throws Exception {
//        // 配置 JobExplorerFactoryBean
//        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
//        factoryBean.setDataSource(dataSource);  // 注入 DataSource
//        return factoryBean.getObject();  // 返回 JobExplorer 实例
//    }
//
//    @Bean(name = "customJobLauncher")
//    JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
//        // 使用 TaskExecutorJobLauncher 代替废弃的 SimpleJobLauncher
//        TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();  // 可以根据需要选择不同的 TaskExecutor
//        TaskExecutorJobLauncher taskExecutorJobLauncher = new TaskExecutorJobLauncher();
//        taskExecutorJobLauncher.setJobRepository(jobRepository);  // 注入 JobRepository
//        taskExecutorJobLauncher.setTaskExecutor(taskExecutor);  // 注入 TaskExecutor
//        taskExecutorJobLauncher.afterPropertiesSet();  // 初始化 TaskExecutorJobLauncher
//        return taskExecutorJobLauncher;  // 返回 TaskExecutorJobLauncher 实例
//    }
//
//    @Bean
//    PlatformTransactionManager transactionManager() {
//        // 你可以继续使用 ResourcelessTransactionManager 或其他事务管理器
//        return new ResourcelessTransactionManager();
//    }
//}

//package com.example.demo.config;
//
//import javax.sql.DataSource;
//
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
//import org.springframework.jdbc.support.JdbcTransactionManager;
//
//@Configuration
//@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
//public class CustomBatchConfigurer {
//
//    // Infrastructure beans
//
//    @Bean
//    @Primary
//    DataSource batchDataSource() {
//        return new EmbeddedDatabaseBuilder()
//            .setType(EmbeddedDatabaseType.H2)
//            .addScript("/org/springframework/batch/core/schema-h2.sql")
//            .generateUniqueName(true)
//            .build();
//    }
//
//    @Bean
//    JdbcTransactionManager batchTransactionManager(DataSource dataSource) {
//        return new JdbcTransactionManager(dataSource);
//    } 
//
//    // "Business" beans
//
//    // Add jobs, steps, etc here
//
//}


//package com.example.demo.config;
//
//import javax.sql.DataSource;
//
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
//import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
//import org.springframework.jdbc.support.JdbcTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//@EnableBatchProcessing(dataSourceRef = "batchDataSource", transactionManagerRef = "batchTransactionManager")
//public class CustomBatchConfigurer {
//    
//    @Bean(name = "batchDataSource")
//    DataSource batchDataSource() {
//        return new EmbeddedDatabaseBuilder()
//            .setType(EmbeddedDatabaseType.H2)
//            .addScript("/org/springframework/batch/core/schema-h2.sql")
//            .generateUniqueName(true)
//            .build();
//    }
//
//
//    @Bean
//    PlatformTransactionManager batchTransactionManager(@Qualifier("batchDataSource")DataSource batchDataSource) {
//        return new JdbcTransactionManager(batchDataSource);
//    }
//}