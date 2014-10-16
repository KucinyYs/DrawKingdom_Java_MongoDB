package com.draw.stats.stats_calculator.descriptors;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.draw.domain.scores_domain.Match;
import com.draw.domain.scores_domain.Team;
import com.draw.stats.stats_calculator.repositories.MatchRepository;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;


public class DrawHistoryDescriptor implements StatsDescriptor{


	private static final Sort SORT_BY_MATCH_DATE = new Sort(Direction.DESC,"matchDate");

	@Autowired
	private MatchRepository matchRepository;
	
	private boolean spanDataToPercentage = false;



	public Multimap<Short, Team> getRanking(Collection<Team> teams, Date date) {
		int minGamesWithNoDraw = Integer.MAX_VALUE;
		int maxGamesWithNoDraw = 0;
		Map<Team, Integer> teamsToStreacks = new HashMap<Team, Integer>();
		for (Team team : teams) {
			List<Match> teamGames = matchRepository.findByHomeTeamOrAwayTeam(team, team, SORT_BY_MATCH_DATE);
			int gamesWithNoDraw = getDrawlessStreak(date, teamGames);
			if(gamesWithNoDraw > maxGamesWithNoDraw){
				maxGamesWithNoDraw = gamesWithNoDraw;
			}else if (gamesWithNoDraw < minGamesWithNoDraw){
				minGamesWithNoDraw = gamesWithNoDraw;
			}
			teamsToStreacks.put(team, gamesWithNoDraw);
		}
		Multimap<Short, Team> results = TreeMultimap.create(Ordering.natural(),Ordering.arbitrary());
		int delta = maxGamesWithNoDraw - minGamesWithNoDraw;
		if(delta > 0){			
			populateResults(minGamesWithNoDraw, teamsToStreacks, results, delta);
		}
		return results;
	}

	public int getDrawlessStreak(Date date, List<Match> teamGames) {
		int gamesWithNoDraw = 0;
		for (Match match : teamGames) {
			if(match.getMatchDate().after(date)){
				continue;
			}
			if(match.getAwayGoals() == match.getHomeGoals()){
				break;
			}
			gamesWithNoDraw++;
		}
		return gamesWithNoDraw;
	}

	private void populateResults(int minGamesWithNoDraw,
			Map<Team, Integer> teamsToStreacks, Multimap<Short, Team> results,
			int delta) {
		for (Entry<Team, Integer> teamAndStreack : teamsToStreacks.entrySet()) {
			Integer streack = teamAndStreack.getValue();
			Short score;
			if(spanDataToPercentage){				
				double factor = (double)(streack - minGamesWithNoDraw)/(double)delta;
				score = (short) (factor * 100);
			}else{
				score = streack.shortValue();
			}
			results.put(score,teamAndStreack.getKey());
		}
	}



	public MatchRepository getMatchRepository() {
		return matchRepository;
	}
	
	public void setMatchRepository(MatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}
}
