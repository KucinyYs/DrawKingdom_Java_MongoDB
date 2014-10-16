package com.draw.domain.scores_domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;


public class Match {
    

    @Id
    private String id;
	
    @DBRef
	private Team homeTeam;
    
    @DBRef
    private Team awayTeam;

    @DBRef
    private Referee referee;
    
    @DBRef(lazy = true)
    private Season season;
        
    private int homeGoals;
    
    private int awayGoals;
    
    private Date matchDate;
    
	public Team getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}

	public Team getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(Date matchDate) {
		this.matchDate = matchDate;
	}

	public int getHomeGoals() {
		return homeGoals;
	}

	public void setHomeGoals(int homeGoals) {
		this.homeGoals = homeGoals;
	}

	public int getAwayGoals() {
		return awayGoals;
	}

	public void setAwayGoals(int awayGoals) {
		this.awayGoals = awayGoals;
	}

	public Referee getReferee() {
		return referee;
	}

	public void setReferee(Referee referee) {
		this.referee = referee;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}


	
    
}
