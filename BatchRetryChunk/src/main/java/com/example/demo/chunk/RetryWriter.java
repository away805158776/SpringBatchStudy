package com.example.demo.chunk;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@StepScope
@Slf4j
public class RetryWriter implements ItemWriter<String> {

	@Override
	public void write(Chunk<? extends String> items) throws Exception {
		items.forEach(item -> log.info("Writer:{}", item));
	}

}