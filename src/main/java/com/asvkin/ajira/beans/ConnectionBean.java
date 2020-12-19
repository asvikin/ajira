package com.asvkin.ajira.beans;

import lombok.Data;

import java.util.List;

@Data
public class ConnectionBean {
	private String source;
	private List<String> targets;
}
