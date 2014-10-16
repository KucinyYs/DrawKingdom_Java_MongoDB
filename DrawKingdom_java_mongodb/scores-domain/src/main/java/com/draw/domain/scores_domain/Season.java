package com.draw.domain.scores_domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;


public class Season {

    @Id
    private String id;

    @DBRef(lazy = true)
    private List<Team> teams = new ArrayList<Team>();

    private Integer year;

    @DBRef
    private League league;
    
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public League getLeague() {
		return league;
	}

	public void setLeague(League league) {
		this.league = league;
	}

	public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }



}
