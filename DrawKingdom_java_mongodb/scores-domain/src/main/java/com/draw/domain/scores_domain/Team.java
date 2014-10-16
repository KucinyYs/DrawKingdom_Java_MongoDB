package com.draw.domain.scores_domain;

import org.springframework.data.annotation.Id;


public class Team {

	@Id
	private String teamName;
	
	private String countryName;
	
	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	
}
