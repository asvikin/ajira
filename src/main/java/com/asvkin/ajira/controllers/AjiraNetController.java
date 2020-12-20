package com.asvkin.ajira.controllers;

import com.asvkin.ajira.api.receivers.Receiver;
import com.asvkin.ajira.service.AjiraNetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AjiraNetController {
	@Autowired
	private AjiraNetService ajiraNetService;
	@Autowired
	private Receiver receiver;
	
	@PostMapping("/ajiranet/process")
	public Object ajiraNetProcess(@RequestBody String data) {
		return receiver.process(data);
		//Without reflections
//		return ajiraNetService.processData(data);
	}
}
