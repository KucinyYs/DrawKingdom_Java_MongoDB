package com.draw.stats.stats_calculator.simulators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import com.draw.domain.scores_domain.League;
import com.draw.domain.scores_domain.Match;
import com.draw.domain.scores_domain.Season;
import com.draw.domain.scores_domain.Team;
import com.draw.stats.stats_calculator.repositories.LeagueRepository;
import com.draw.stats.stats_calculator.repositories.MatchRepository;
import com.draw.stats.stats_calculator.repositories.SeasonRepository;
import com.draw.stats.stats_calculator.repositories.TeamRepository;

public class DrawSeqSimulator implements DrawSimulator{

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private MatchRepository matchRepository;
	
	@Autowired
	private SeasonRepository seasonRepository;
	
	@Autowired
	private LeagueRepository leagueRepository;
	
	@Autowired
	private MongoOperations mongoOperations;

	private int sequence = 1;


	public int getStrategySuccess(String countryName, int devision, int year, int sequence){
		int tmpSeq = sequence;
		try{			
			this.sequence = sequence;
			return getStrategySuccess(countryName, devision, year);
		}finally{
			this.sequence = tmpSeq;
		}
	}
	private int getStrategySuccess(String countryName, int devision, int year){
		League league = leagueRepository.findByCountryNameAndDevision(countryName, devision);
		Season season = seasonRepository.findByLeagueAndYear(league, year);
		List<Team> allTeams = teamRepository.findByCountryName(countryName);
		Map<Team, List<Integer>> teamStreaks = buildTeamStreaks(season, allTeams);
		int success = calculateSuccess(teamStreaks);

		return success;
	}


	private Map<Team, List<Integer>> buildTeamStreaks(Season season,
			List<Team> allTeams) {
		Map<Team, List<Integer>> teamStreaks = new HashMap<Team, List<Integer>>();
		for (Team team : allTeams) {
			List<Match> matches = getSortedMatchesForYear(season, team);
			if(matches.isEmpty()){
				continue;
			}
			List<Integer> drawStreaks = calculateDrawStreaksForYear(matches);
			teamStreaks.put(team, drawStreaks);
		}
		return teamStreaks;
	}


	private List<Integer> calculateDrawStreaksForYear(List<Match> matches) {
		List<Integer> drawStreaks = new ArrayList<Integer>(matches.size());
		int lastStreak = 0;
		for (Match match : matches) {				
			if(match.getAwayGoals() == match.getHomeGoals()){
				lastStreak++;
			}else{
				lastStreak = 0;
			}
			drawStreaks.add(lastStreak);
		}
		return drawStreaks;
	}


	private List<Match> getSortedMatchesForYear(Season season, Team team) {
		List<Match> homeMatches = matchRepository.findByHomeTeamAndSeason(team, season);
		List<Match> awayMatches = matchRepository.findByAwayTeamAndSeason(team, season);
		List<Match> matches = new ArrayList<Match>(homeMatches);
		matches.addAll(awayMatches);
		Comparator<Match> matchDateComperator = (m1, m2) -> (m1.getMatchDate().before(m2.getMatchDate()) ? 
				1 : -1);
		matches.sort(matchDateComperator);
		return matches;
	}


	private int calculateSuccess(Map<Team, List<Integer>> teamStreaks) {
		Entry<Team, List<Integer>> sampleEntry = teamStreaks.entrySet().iterator().next();
		int amountOfMatches = sampleEntry.getValue().size();
		int amountOfBets = 0, success = 0;
		for (int currentRound = 0; currentRound < amountOfMatches - sequence; currentRound++) {
			Map<Team, Integer> currentLeaders = getLeadersForRound(teamStreaks,currentRound);
			amountOfBets += currentLeaders.keySet().size();
			for (Team leader : currentLeaders.keySet()) {
				for(int seqIndex = 1; seqIndex <= sequence; seqIndex++){	
					List<Integer> streaks = teamStreaks.get(leader);
					if(streaks.size() != amountOfMatches){
						return -1;
					}
					if(streaks.get(currentRound+seqIndex) == streaks.get(currentRound) + 1){
						success++;
						break;						
					}
				}
			}
		}
		return (success * 100) / amountOfBets;
	}

	private Map<Team, Integer> getLeadersForRound(
			Map<Team, List<Integer>> teamStreaks, int round) {
		Integer currentMax = 0;
		Map<Team,Integer> currentLeaders = new HashMap<Team,Integer>();
		for (Entry<Team, List<Integer>> entry : teamStreaks.entrySet()) {
			Integer teamStreak = entry.getValue().get(round);
			if(teamStreak > currentMax){
				currentLeaders.clear();
				currentMax = teamStreak;
				currentLeaders.put(entry.getKey(), teamStreak);
			}else if ( teamStreak == currentMax){
				currentLeaders.put(entry.getKey(), teamStreak);
			}
		}
		return currentLeaders;
	}


	public int getSequence() {
		return sequence;
	}


	public void setSequence(int sequence) {
		this.sequence = sequence;
	}




}
