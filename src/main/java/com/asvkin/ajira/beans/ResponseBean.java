package com.asvkin.ajira.beans;

import lombok.Data;

import java.util.List;

@Data
public class ResponseBean {
	private String msg;
	private List<Device> devices;
	
	public ResponseBean(String message) {
		this.msg = message;
		this.devices = null;
	}
}
