package com.draw.stats.stats_calculator.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.draw.domain.scores_domain.League;
import com.draw.domain.scores_domain.Season;

public interface SeasonRepository extends MongoRepository<Season, String> {

	List<Season> findByLeagueAndYearLessThan(League league,int year);
	
	Season findByLeagueAndYear(League league, Integer year);
	
	

}
