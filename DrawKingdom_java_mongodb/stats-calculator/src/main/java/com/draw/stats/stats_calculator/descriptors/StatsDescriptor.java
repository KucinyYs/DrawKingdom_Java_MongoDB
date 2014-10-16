package com.draw.stats.stats_calculator.descriptors;

import java.util.Collection;
import java.util.Date;

import com.draw.domain.scores_domain.Team;
import com.google.common.collect.Multimap;

public interface StatsDescriptor {
	Multimap<Short,Team> getRanking(Collection<Team> teams, Date date);

}
