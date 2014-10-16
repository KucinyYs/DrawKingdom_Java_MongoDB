package com.draw.stats.stats_calculator;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.draw.domain.scores_domain.Team;
import com.draw.stats.stats_calculator.descriptors.DrawPercentageDescriptor;
import com.google.common.collect.Multimap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"draw-data-context.xml"})
public class PercentageDescriptorTest {

	@Autowired
	private DrawPercentageDescriptor drawPercentageDescriptor;
	
	@Autowired
	private MongoOperations mongo;
	
	@Test
	public void findTeams(){
		List<Team> allTeams = mongo.findAll(Team.class);
		Multimap<Short, Team> rankings = drawPercentageDescriptor.getRanking(allTeams, new Date());
		DrawHistoryDescriptorTest.printResults(rankings, drawPercentageDescriptor.getClass().getName());
	}
}
