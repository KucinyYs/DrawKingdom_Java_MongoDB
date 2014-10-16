package com.draw.domain.scores_domain;

import org.springframework.data.annotation.Id;

public class Referee {

	@Id
	private String refereeName;

	public String getRefereeName() {
		return refereeName;
	}

	public void setRefereeName(String refereeName) {
		this.refereeName = refereeName;
	}
	
	
	
}
