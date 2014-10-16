package com.draw.domain.scores_domain;

import org.springframework.data.annotation.Id;

public class Fixture {

	@Id
	private String id;
	
	private String homeTeam;
	
	private String awayTeam;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}
	
	
}
