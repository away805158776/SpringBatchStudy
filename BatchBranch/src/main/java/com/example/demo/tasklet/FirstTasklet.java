package com.example.demo.tasklet;


import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("FirstTasklet")
@StepScope
@Slf4j
public class FirstTasklet implements Tasklet {
	
	@Value("${batch.branch.value}")
	private String batchBranchValue;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		
		log.info("batchBranchValue:" + batchBranchValue);
		
		if("0".equals(batchBranchValue)) {
			contribution.setExitStatus(ExitStatus.COMPLETED);
		} else {
			contribution.setExitStatus(ExitStatus.FAILED);
		}
		
		return RepeatStatus.FINISHED;
	}
}