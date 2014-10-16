package com.draw.stats.stats_calculator.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.draw.domain.scores_domain.League;

public interface LeagueRepository extends MongoRepository<League, String>{

	League findByCountryNameAndDevision(String countryName, Integer devision);
	
}
