package com.example.demo.chunk;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@StepScope
@Slf4j
public class HelloWriter implements ItemWriter<String> {

	@Override
    public void write(Chunk<? extends String> chunk) throws Exception {
		List<? extends String> items = chunk.getItems();
        log.info("writer: {}", items);
        log.info("===========");
    }

//	@Override
//    public void write(Chunk<? extends String> items) throws Exception {
//        log.info("writer: {}", items);
//        log.info("===========");
//    }

	
//	@Override
//	public void write(Chunk<? extends String> chunk) throws Exception {
//		// TODO Auto-generated method stub
//		
//	}
	
	// The following code is wrong,error is:
	//The method write(List<? extends String>) of type HelloWriter must override or implement a supertype method
//	@Override
//    public void write(List<? extends String> items) throws Exception {
//        log.info("writer: {}", items);
//        log.info("===========");
//    }
}