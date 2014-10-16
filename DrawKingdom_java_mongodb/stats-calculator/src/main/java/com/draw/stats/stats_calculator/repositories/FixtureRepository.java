package com.draw.stats.stats_calculator.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.draw.domain.scores_domain.Fixture;

public interface FixtureRepository extends MongoRepository<Fixture, String> {
	
	public Fixture findByHomeTeamOrAwayTeam(String homeTeam, String awayTeam);
}
