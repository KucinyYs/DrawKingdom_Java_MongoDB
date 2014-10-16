package com.shlezi.draws.data.draw_data.utils.csv;

import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;

public class CsvUtils {
	public static final String SCORES_URL_FIXTURES_KEY = "scores.url.fixtures";
	public static final String SCORES_LEAGUE_NAME_KEY = "scores.leagueName.";
	public static final String SCORES_END_YEAR_KEY = "scores.endYear";
	public static final String SCORES_START_YEAR_KEY = "scores.startYear";
	public static final String SCORES_LEAGUES_KEY = "scores.leagues";
	public static final String SCORES_URL_PREFIX_KEY = "scores.url.prefix";
	public static final String DIV_COLUMN = "Div";
	public static final String MATCH_DATE_COLUMN = "Date";
	public static final String AWAY_TEAM_COLUMN = "AwayTeam";
	public static final String HOME_TEAM_COLUMN = "HomeTeam";
	public static final String REFEREE_COLUMN = "Referee";
	public static final String AWAY_TEAM_GOALS_COLUMN = "FTAG";
	public static final String HOME_TEAM_GOALS_COLUMN = "FTHG";
	public static CSVFormat csvFormat = CSVFormat.EXCEL.withHeader(DIV_COLUMN,MATCH_DATE_COLUMN,HOME_TEAM_COLUMN,AWAY_TEAM_COLUMN,HOME_TEAM_GOALS_COLUMN,AWAY_TEAM_GOALS_COLUMN,REFEREE_COLUMN).withSkipHeaderRecord(true);
	public static Pattern FILE_NAME_PATTERN = Pattern.compile("(.+)/(\\d+)/(\\w+)(\\d+).csv");
	public static final String SCORES_DEVISION_PREFIX = "scores.devision.";

}
