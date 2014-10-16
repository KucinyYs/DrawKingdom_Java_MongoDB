package com.draw.stats.stats_calculator;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.draw.domain.scores_domain.Fixture;
import com.draw.domain.scores_domain.Team;
import com.draw.stats.stats_calculator.descriptors.DrawHistoryDescriptor;
import com.draw.stats.stats_calculator.repositories.FixtureRepository;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"draw-data-context.xml"})
public class FixtureTest {

	private static final int SCORE_THRESHOLD = 0;

	@Autowired
	private DrawHistoryDescriptor drawHistoryDescriptor;
	
	@Autowired
	private FixtureRepository fixtureRepository;
	
	@Autowired
	private MongoOperations mongo;
		
	@Test
	public void getRelevantTeams(){
		List<Team> teams = mongo.findAll(Team.class);
		Date date = new Date();
		Multimap<Short, Team> rankings = drawHistoryDescriptor.getRanking(teams, date);
		Multimap<Integer,String> hotFixtures = TreeMultimap.create();
		Set<String> doneTeams = new HashSet<String>();
		for (Entry<Short, Team> entry : rankings.entries()) {
			if(entry.getKey() < SCORE_THRESHOLD){
				continue;
			}
			if(doneTeams.contains(entry.getValue().getTeamName())){
				continue;
			}
			Team team = entry.getValue();
			Fixture fixture = fixtureRepository.findByHomeTeamOrAwayTeam(team.getTeamName(),team.getTeamName());
			if(fixture != null){
				String otherTeam = team.getTeamName().equals(fixture.getAwayTeam()) ? fixture.getHomeTeam() : fixture.getAwayTeam();
				Short score = getOtherTeamScore(rankings, otherTeam);
				if(score < SCORE_THRESHOLD){
					continue;
				}
				Integer conmbinedScore = score + entry.getKey();
				hotFixtures.put(conmbinedScore , team.getTeamName() + " VS " + otherTeam);
				doneTeams.add(otherTeam);
			}
		}
		for (Entry<Integer, String> entry : hotFixtures.entries()) {
			System.out.println("fixture " + entry.getValue() + " has score: " + entry.getKey());
		}
	}

	public Short getOtherTeamScore(Multimap<Short, Team> rankings,
			String otherTeam) {
		Short score = 0;
		for (Entry<Short, Team> entry : rankings.entries()) {
			if(entry.getValue().getTeamName().equals(otherTeam)){
				score = entry.getKey();
			}
		}
		return score;
	}
}
