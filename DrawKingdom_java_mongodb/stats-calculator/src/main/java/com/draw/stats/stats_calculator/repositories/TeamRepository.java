package com.draw.stats.stats_calculator.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.draw.domain.scores_domain.Team;

public interface TeamRepository extends MongoRepository<Team, String> {

	List<Team> findByCountryName(String countryName);
	
}
