package com.asvkin.ajira.api.receivers;

import org.springframework.stereotype.Service;

@Service
public interface Receiver {
	Object process(String data);
}
