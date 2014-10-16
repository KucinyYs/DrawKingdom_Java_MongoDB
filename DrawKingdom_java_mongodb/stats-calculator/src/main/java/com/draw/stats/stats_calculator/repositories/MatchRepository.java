package com.draw.stats.stats_calculator.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.draw.domain.scores_domain.Match;
import com.draw.domain.scores_domain.Season;
import com.draw.domain.scores_domain.Team;

public interface MatchRepository extends MongoRepository<Match, String> {

	
	List<Match> findByHomeTeamOrAwayTeam(Team homeTeam, Team awayTeam, Sort sort);
	List<Match> findByHomeTeamOrAwayTeam(Team homeTeam, Team awayTeam);
	List<Match> findByAwayTeamAndSeason(Team team,Season season);
	List<Match> findByHomeTeamAndSeason(Team team,Season season);
	
	

}
