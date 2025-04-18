package com.example.demo.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class Employee {

	@Id
	private Integer id;
	
	private String name;
	
	private Integer age;
	
	private Integer gender;
	
	@Transient
	private String genderString;
	
	public void convertGenderIntToString() {
		if(gender == 1) {
			genderString = "男性";
		}else if(gender == 2) {
			genderString = "女性";
		}else {
			String errorMsg = "Gender is invalid:" + gender;
			throw new IllegalStateException(errorMsg);
		}
	}
	
}

