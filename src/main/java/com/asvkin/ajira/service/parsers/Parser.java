package com.asvkin.ajira.service.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface Parser {
	Logger log = LoggerFactory.getLogger(Parser.class);
	
	/**
	 * Fetches query parameters from the provided URI Path.
	 * For example:
	 * For input URI path, {@code "/something/with?queryParam1=Value1&queryParam2=Value2"},
	 * response will contain, {@code Map{queryParam1=Value1, queryParam2=Value2}}
	 *
	 * @param uriPath URI Path.
	 * @return Map&lt;String, String&gt;
	 */
	static Map<String, String> getQueryParams(String uriPath) {
		if (Objects.isNull(uriPath) || uriPath.isEmpty() || uriPath.indexOf('?') == -1) {
			log.info("No query parameters identified");
			return new HashMap<>();
		}
		Map<String, String> queries = new HashMap<>();
		String[] queryParams = uriPath.substring(uriPath.indexOf('?') + 1).split("&");
		for (String queryParam : queryParams) {
			int equalsToPosition = queryParam.indexOf('=');
			if (equalsToPosition == -1) {
				log.info("Invalid query parameter: {}, Skipping...", queryParam);
			}
			queries.put(queryParam.substring(0, equalsToPosition), queryParam.substring(equalsToPosition + 1));
		}
		return queries;
	}
	
	/**
	 * Fetches path parameters from the URI Path and URI Template.
	 * For example:
	 * For input URI Path, {@code "/something/with/GOLD/in/the/middle"} and
	 * URI Template, {@code "/something/with/{what}/in/the/middle"}, response will contain,
	 * {@code Map{what=GOLD}}
	 *
	 * @param uriPath     URI Path.
	 * @param uriTemplate URI Template.
	 * @return Map&lt;String, String&gt;
	 */
	static Map<String, String> getPathParams(String uriPath, String uriTemplate) {
		if (Objects.isNull(uriPath) || uriPath.isEmpty() || Objects.isNull(uriTemplate) || uriTemplate.isEmpty()) {
			log.error("Empty uriPath or uriTemplate provided.");
			return new HashMap<>();
		}
		String[] uriPathNames = uriPath.split("/");
		String[] uriTemplateNames = uriTemplate.split("/");
		Map<String, String> queries = new HashMap<>();
		if (uriPathNames.length != uriTemplateNames.length) {
			log.error("uriPath length and uriTemplate length is not matching.");
			return new HashMap<>();
		}
		for (int i = 0; i < uriPathNames.length; i++) {
			if (uriTemplateNames[i].indexOf("{") == 0) {
				queries.put(
						uriTemplateNames[i]
								.substring(uriTemplateNames[i].indexOf("{") + 1, uriTemplateNames[i].indexOf("}")),
						uriPathNames[i]
				);
			}
		}
		return queries;
	}
	
	<T> T parse(String data, Class<T> returnType);
}
