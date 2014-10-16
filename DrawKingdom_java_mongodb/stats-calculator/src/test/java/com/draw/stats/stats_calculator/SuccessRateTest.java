package com.draw.stats.stats_calculator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.draw.domain.scores_domain.League;
import com.draw.domain.scores_domain.Season;
import com.draw.stats.stats_calculator.repositories.MatchRepository;
import com.draw.stats.stats_calculator.repositories.SeasonRepository;
import com.draw.stats.stats_calculator.simulators.DrawSeqSimulator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"draw-data-context.xml"})
public class SuccessRateTest {

	private static final int maxStayPower = 10;

	@Autowired
	private DrawSeqSimulator simulator;

	@Autowired
	private MongoOperations mongo;

	@Autowired
	private SeasonRepository SeasonRepository;

	@Autowired
	private MatchRepository matchRepository;


	private static List<Integer> stayPowerList = IntStream.rangeClosed(1, maxStayPower).boxed()
			.collect(Collectors.toList());

	int progress = 0;
	
	@Test
	public void getRate() throws IOException{
		String csvStayPowersStr = StringUtils.arrayToCommaDelimitedString(stayPowerList.toArray());
		List<League> allLeagues = mongo.findAll(League.class);
		int leagueProgressWeight = (int) (((double)100)/allLeagues.size());
		for (League league : allLeagues) {
			handleLeague(csvStayPowersStr, league);
			progress += leagueProgressWeight;
		}
	}


	public void handleLeague(String csvStayPowersStr, League league)
			throws IOException {
		List<Season> seasons = SeasonRepository.findByLeagueAndYearLessThan(league,2015);
		int seasonProgressWeight = (int) (((double)100)/seasons.size());
		for (Season season : seasons) {
			handleSeason(csvStayPowersStr, league, season);	
			progress+=(seasonProgressWeight/10);
			System.out.println("progress: " + progress);
		}
	}


	public void handleSeason(String csvStayPowersStr, League league,
			Season season) throws IOException {
		Map<Integer, Short> stayPowerMap = new HashMap<Integer, Short>(maxStayPower - 1);
		Writer fileWriter = getWriter(league, season);
		for(int stayPower = 1; stayPower <= maxStayPower; stayPower++){				
			Integer strategySuccess = simulator.getStrategySuccess(league.getCountryName(),league.getDevision(),season.getYear(),stayPower);
			stayPowerMap.put(stayPower, strategySuccess.shortValue());
		}
		printResultsToWriter(csvStayPowersStr,stayPowerMap, fileWriter);
		fileWriter.close();
	}


	public Writer getWriter(League league, Season season) throws IOException {
		String fileName = league.getCountryName()+"-" + league.getDevision().toString() + "_" + season.getYear() + ".csv";
		Writer fileWriter = new FileWriter(new File("results",fileName));
		return fileWriter;
	}


	public void printResultsToWriter(String csvStayPowersStr, Map<Integer, Short> stayPowerMap,
			Writer fileWriter) throws IOException {
		String successCsvStr = stayPowerList.stream().map(sp -> stayPowerMap.get(sp).toString()).collect(Collectors.joining(","));
		fileWriter.append(csvStayPowersStr + System.lineSeparator());
		fileWriter.append(successCsvStr);
		fileWriter.flush();
	}

}
