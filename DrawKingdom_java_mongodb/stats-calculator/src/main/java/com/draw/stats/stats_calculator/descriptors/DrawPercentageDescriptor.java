package com.draw.stats.stats_calculator.descriptors;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.draw.domain.scores_domain.Match;
import com.draw.domain.scores_domain.Team;
import com.draw.stats.stats_calculator.repositories.MatchRepository;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class DrawPercentageDescriptor implements StatsDescriptor {
	
	@Autowired
	private MatchRepository matchRepository;
	


	public Multimap<Short, Team> getRanking(Collection<Team> teams, Date date) {
		Multimap<Short, Team> results = TreeMultimap.create(Ordering.natural(),Ordering.arbitrary());
		for (Team team : teams) {
			List<Match> teamMatches = matchRepository.findByHomeTeamOrAwayTeam(team, team);
			int numOfDraws = getNumOfDraws(date, teamMatches);
			double drawPercentage = (double) numOfDraws/(double)teamMatches.size();
			Short score = (short) (drawPercentage * 100);
			results.put( score, team);
		}
		return results;
	}



	public int getNumOfDraws(Date date, List<Match> teamMatches) {
		int numOfDraws = 0;
		for (Match match : teamMatches) {
			if(match.getMatchDate().after(date)){
				continue;
			}
			if(match.getAwayGoals() == match.getHomeGoals()){
				numOfDraws++;
			}
		}
		return numOfDraws;
	}
	
	

}
