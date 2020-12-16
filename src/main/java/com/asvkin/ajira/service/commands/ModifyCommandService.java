package com.asvkin.ajira.service.commands;

import com.asvkin.ajira.beans.ResponseBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("MODIFY-SERVICE")
public class ModifyCommandService extends CommandService {
	@Override
	public ResponseBean processData(Map<String, String> headers, String endPoint, String data) {
		return null;
	}
}
