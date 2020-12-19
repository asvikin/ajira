package com.asvkin.ajira.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ResponseBean {
	private String msg;
	private List<Device> devices;
	
	public ResponseBean(String message) {
		this.msg = message;
		this.devices = null;
	}
}
