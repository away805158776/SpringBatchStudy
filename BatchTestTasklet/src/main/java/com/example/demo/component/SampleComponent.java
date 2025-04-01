package com.example.demo.component;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class SampleComponent {
	
	public int random(){
		Random random = new Random();
		int randomInt = random.nextInt(100);
		return randomInt;
	}
	
}
