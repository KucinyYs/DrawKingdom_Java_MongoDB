package com.shlezi.draws.data.draw_data;

import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.AWAY_TEAM_COLUMN;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.AWAY_TEAM_GOALS_COLUMN;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.DIV_COLUMN;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.HOME_TEAM_COLUMN;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.HOME_TEAM_GOALS_COLUMN;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.MATCH_DATE_COLUMN;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.SCORES_END_YEAR_KEY;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.SCORES_LEAGUES_KEY;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.SCORES_LEAGUE_NAME_KEY;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.SCORES_START_YEAR_KEY;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.SCORES_URL_FIXTURES_KEY;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.SCORES_URL_PREFIX_KEY;
import static com.shlezi.draws.data.draw_data.utils.csv.CsvUtils.csvFormat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import com.draw.domain.scores_domain.Fixture;
import com.draw.domain.scores_domain.League;
import com.draw.domain.scores_domain.Match;
import com.draw.domain.scores_domain.Season;
import com.draw.domain.scores_domain.Team;
import com.shlezi.draws.data.draw_data.utils.DrawConfiguration;
import com.shlezi.draws.data.draw_data.utils.csv.CsvUtils;



public class DrawDomainCsvToMongoHelper {

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);



	static ApplicationContext ctx = new GenericXmlApplicationContext("draw-data-context.xml");
	static MongoOperations mongoOperation = (MongoOperations)ctx.getBean("mongoTemplate");
	private static final Logger LOGGER = Logger.getLogger(DrawDomainCsvToMongoHelper.class);
	
	static protected File localDir = new File("C:\\Users\\Ishai\\Downloads\\csvs");
	
	static private boolean shouldWriteToLocalDir = true;
	
	static{
		PropertyConfigurator.configure("log4j.properties");
	}
	
	public static void main(String [] args) throws  Exception{
		Set<String> filesToPoll = loadCsvConfiguration();
		iterateCsvFiles(filesToPoll);
//		populateFixtures();
	}


	public static Set<String> loadCsvConfiguration() throws Exception {
		String scoresURLPrefix = DrawConfiguration.getInstance().getProperty(SCORES_URL_PREFIX_KEY);
		String leaguesStr = DrawConfiguration.getInstance().getProperty(SCORES_LEAGUES_KEY);
		Integer endYear = Integer.valueOf(DrawConfiguration.getInstance().getProperty(SCORES_END_YEAR_KEY));
		Integer startYear = Integer.valueOf(DrawConfiguration.getInstance().getProperty(SCORES_START_YEAR_KEY));
		Set<String> filesToPoll = new HashSet<String>();
		Set<String> leagueSet = StringUtils.commaDelimitedListToSet(leaguesStr);
		for (String league : leagueSet) {
			String devisionsStr = DrawConfiguration.getInstance().getProperty(CsvUtils.SCORES_DEVISION_PREFIX + league);
			Set<String> devisionsSet = StringUtils.commaDelimitedListToSet(devisionsStr);
			for (String devision : devisionsSet) {				
				addLeagueCsvs(scoresURLPrefix, endYear, startYear, filesToPoll,
						league,devision);
			}
		}
		return filesToPoll;
	}


	public static void addLeagueCsvs(String scoresURLPrefix, Integer endYear,
			Integer startYear, Set<String> filesToPoll, String league, String devision)
					throws Exception {
		StringBuilder uriBase = new StringBuilder(scoresURLPrefix);
		int year = startYear;
		while (year < endYear) {
			addSpecificYear(filesToPoll, league, uriBase.toString(), year, devision);
			year++;
		}
	}


	public static void addSpecificYear(Set<String> filesToPoll, String league,
			String uriBase, int year, String devision) throws Exception {
		String years = year + "" + (year + 1);
		StringBuilder stringBuilder = new StringBuilder(uriBase + years + "/");
		stringBuilder.append(league);
		stringBuilder.append(devision + ".csv");
		String path = stringBuilder.toString();
		filesToPoll.add(path);
	}


	public static void populateFixtures() throws Exception{
		String fixturesFileUrl = DrawConfiguration.getInstance().getProperty(SCORES_URL_FIXTURES_KEY);
		BufferedReader csvReader = getCsvReader(fixturesFileUrl);
		CSVParser csvParser = null;
		try{
			csvParser = new CSVParser(csvReader, csvFormat);
			String leaguesStr = DrawConfiguration.getInstance().getProperty(SCORES_LEAGUES_KEY);
			for (CSVRecord csvRecord : csvParser.getRecords()) {
				if(!leaguesStr.contains(csvRecord.get(DIV_COLUMN))){
					continue;
				}
				Fixture fixture = new Fixture();
				fixture.setHomeTeam(csvRecord.get(HOME_TEAM_COLUMN));
				fixture.setAwayTeam(csvRecord.get(AWAY_TEAM_COLUMN));
				mongoOperation.save(fixture);
			}
		}finally{
			if(csvParser != null){
				csvParser.close();
			}
		}
		System.out.println("finished reading fixtures file " + fixturesFileUrl);
	}




	public static void iterateCsvFiles(Set<String> filesToPoll) throws IOException,
	FileNotFoundException, Exception {
		for (String csvFile : filesToPoll) {
			try{
				handleSingleCsvFile(csvFile);
				LOGGER.info("successfully finished with file: " + csvFile);
			}catch (Exception e){
				LOGGER.error("failed with file " + csvFile,e);
			}
		}
	}


	public static void handleSingleCsvFile(String csvFile) throws IOException,
			MalformedURLException, Exception {
		CSVParser csvParser = null;
		try{				
			Matcher matcher = CsvUtils.FILE_NAME_PATTERN.matcher(csvFile);
			boolean matches = matcher.matches();
			if(!matches){
				throw new IllegalStateException("invalid csv file path " + csvFile);
			}
			Integer year = Integer.valueOf("20" + matcher.group(2).substring(2));
			String countryKey = matcher.group(3);
			String country = DrawConfiguration.getInstance().getProperty(SCORES_LEAGUE_NAME_KEY + countryKey);
			Integer devision = Integer.valueOf(matcher.group(4));
			League league = getOrCreateLeague(devision, country);
			Season season = generateLeagueSeason(league, year);
			BufferedReader in = getCsvReader(csvFile);
			writeToLocalDisk(csvFile,league.getCountryName(),league.getDevision(),season.getYear());
			csvParser = new CSVParser(in, csvFormat);
			
			List<CSVRecord> records = csvParser.getRecords();
			for (CSVRecord csvRecord : records) {
				try{					
					registerMatch(season, csvRecord);
				}catch (Exception e){
					LOGGER.error("failed parsing csv record " + csvRecord.toString(),e);
				}
			}
		}finally{
			if(csvParser != null){
				csvParser.close();
			}
		}
	}


	public static BufferedReader writeToLocalDisk(String csvFile,String countryName, int devision, int year) throws IOException,
			MalformedURLException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtils.copy(new URL(csvFile).openStream(), os);
		byte[] data = os.toByteArray();
		File outputFile = new File(localDir,countryName + "-" + devision + "-" +year + ".csv");
		IOUtils.copy(new ByteArrayInputStream(data), new FileWriter(outputFile));
		InputStreamReader originalSteam = new InputStreamReader(new ByteArrayInputStream(data));
		
		return new BufferedReader(originalSteam);
	}


	public static League getOrCreateLeague(Integer devision, String country) {
		Query leagueQuery = new Query(Criteria.where("countryName").is(country).
				andOperator(Criteria.where("devision").is(devision)));
		League league = mongoOperation.findOne(leagueQuery, League.class);
		if(league == null){
			league = new League();
			league.setCountryName(country);
			league.setDevision(devision);
			mongoOperation.save(league);
			return mongoOperation.findOne(leagueQuery, League.class);
		}
		return league;
	}


	public static BufferedReader getCsvReader(String csvFile)
			throws IOException, MalformedURLException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(new URL(csvFile).openStream()));
		return in;
	}


	private static Season generateLeagueSeason(League league, int year ) {
		Query seasonQuery = new Query(Criteria.where("year").is(year).and("league").is(league));
		Season sameSeason = mongoOperation.findOne(seasonQuery, Season.class);
		if(sameSeason != null){
			LOGGER.warn(String.format("WARNING - overriding season %s-%d", league.getCountryName(),year));
			mongoOperation.remove(sameSeason);
			mongoOperation.remove(new Query(Criteria.where("season").is(sameSeason)), Match.class);
		}
		Season season = new Season();
		season.setYear(year);
		season.setLeague(league);
		mongoOperation.save(season);
		return mongoOperation.findOne(seasonQuery, Season.class);
	}

	private static void registerMatch(Season season, CSVRecord csvRecord) throws Exception {
		Team homeTeam = getHomeTeam(season, csvRecord);
		Team awayTeam = getAwayTeam(season, csvRecord);
		Match match = new Match();
		match.setHomeGoals(Integer.valueOf(csvRecord.get(HOME_TEAM_GOALS_COLUMN)));
		match.setAwayGoals(Integer.valueOf(csvRecord.get(AWAY_TEAM_GOALS_COLUMN)));
		match.setHomeTeam(homeTeam);
		match.setAwayTeam(awayTeam);
		match.setMatchDate(SIMPLE_DATE_FORMAT.parse(csvRecord.get(MATCH_DATE_COLUMN)));
		match.setSeason(season);
		mongoOperation.save(match);
		
	}


	private static Team getAwayTeam(Season season, CSVRecord csvRecord) {
		Team awayTeam = getOrAddTeam(season, csvRecord.get(AWAY_TEAM_COLUMN));
		return awayTeam;
	}

	private static Team getHomeTeam(Season season, CSVRecord csvRecord) {
		Team homeTeam = getOrAddTeam(season, csvRecord.get(HOME_TEAM_COLUMN));
		return homeTeam;
	}



	private static Team getOrAddTeam(Season season, String teamName) {
		Team team = mongoOperation.findOne(new Query(Criteria.where("teamName").is(teamName).
				andOperator(Criteria.where("countryName").is(season.getLeague().getCountryName()))), Team.class);
		if(team == null){
			team = new Team();
			team.setTeamName(teamName);
			team.setCountryName(season.getLeague().getCountryName());
			season.getTeams().add(team);
			mongoOperation.save(team);
			mongoOperation.save(season);
		}
		return team;
	}




}
