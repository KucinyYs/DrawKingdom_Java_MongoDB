package com.draw.stats.stats_calculator;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.draw.domain.scores_domain.Team;
import com.draw.stats.stats_calculator.descriptors.DrawHistoryDescriptor;
import com.google.common.collect.Multimap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"draw-data-context.xml"})
public class DrawHistoryDescriptorTest {
	

	@Autowired
	private DrawHistoryDescriptor drawHistoryDescriptor;
	
	@Autowired
	private MongoOperations mongo;
	
	@Test
	public void getRelevantTeams(){
		List<Team> teams = mongo.findAll(Team.class);
		Date date = new Date();
		Multimap<Short, Team> rankings = drawHistoryDescriptor.getRanking(teams, date);
		printResults(rankings, drawHistoryDescriptor.getClass().getName());
	}

	public static void printResults(Multimap<Short, Team> rankings,
			String descriptorName) {
		for (Entry<Short, Team> ranking : rankings.entries()) {
			System.out.println("[" + descriptorName +"] team: " + ranking.getValue().getTeamName() + " got score: " + ranking.getKey());
		}
	}

}
