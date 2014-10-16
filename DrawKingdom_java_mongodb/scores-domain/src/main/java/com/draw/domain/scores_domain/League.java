package com.draw.domain.scores_domain;

import org.springframework.data.annotation.Id;

public class League {
	
	@Id
	private String id;
	
	private String countryName;
	
	
	private Integer devision;
	
	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Integer getDevision() {
		return devision;
	}

	public void setDevision(Integer devision) {
		this.devision = devision;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	

}
