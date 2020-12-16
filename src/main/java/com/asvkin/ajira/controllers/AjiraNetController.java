package com.asvkin.ajira.controllers;

import com.asvkin.ajira.beans.ResponseBean;
import com.asvkin.ajira.service.AjiraNetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AjiraNetController {
	@Autowired
	private AjiraNetService ajiraNetService;
	
	@PostMapping("/ajiranet/process")
	public ResponseBean ajiraNetProcess(@RequestBody String data) {
		return ajiraNetService.processData(data);
	}
}
