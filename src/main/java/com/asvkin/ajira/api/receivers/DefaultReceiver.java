package com.asvkin.ajira.api.receivers;

import com.asvkin.ajira.api.annotations.*;
import com.asvkin.ajira.api.exceptions.ApiException;
import com.asvkin.ajira.exception.InvalidCommandException;
import com.asvkin.ajira.exception.InvalidContentTypeException;
import com.asvkin.ajira.exception.NotFoundException;
import com.asvkin.ajira.service.parsers.Parser;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

@Component
@Primary
@Slf4j
public class DefaultReceiver implements Receiver {
	private static final HashMap<String, List<EndpointHandler>> lookup = new HashMap<>();
	private static final Class<? extends Annotation> API_REQUEST_CONTROLLER_CLASS = ApiRequestController.class;
	private static final Class<? extends Annotation> API_REQUEST_CLASS = ApiRequest.class;
	private static final Class<? extends Annotation> API_REQUEST_BODY = ApiRequestBody.class;
	private static final Class<? extends Annotation> API_PATH_VARIABLE = ApiPathVariable.class;
	private static final Class<? extends Annotation> API_QUERY_PARAMETER = ApiQueryParameter.class;
	private static final Class<? extends Annotation> API_REQUEST_HEADER = ApiRequestHeader.class;
	private static final String CONTENT_TYPE = "content-type";
	private static final String DEFAULT_CONTENT_TYPE = "application/json";
	private static final String DEFAULT_VALUE = "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n";
	private static final String SPACE = " ";
	private static final String NEW_LINE = "\r\n";
	private static final String DOUBLE_NEW_LINE = NEW_LINE + NEW_LINE;
	private static final String FORWARD_SLASH = "/";
	private static final String REGEX_QUESTION_MARK = "\\?";
	
	//Static block which calls init method to initialize the cache.
	static {
		init();
	}
	
	@Autowired
	ApplicationContext ctx;
	
	/**
	 * Static initializing method which runs on boot start-up that caches the URL to {@link EndpointHandler} for
	 * usage in runtime.
	 */
	private static void init() {
		Reflections reflections = new Reflections("com.asvkin.ajira");
		Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(API_REQUEST_CONTROLLER_CLASS);
		for (Class<?> clazz : allClasses) {
			ApiRequestController classAnnotationInstance =
					(ApiRequestController) clazz.getAnnotation(API_REQUEST_CONTROLLER_CLASS);
			for (final Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(API_REQUEST_CLASS)) {
					ApiRequest methodAnnotationInstance = (ApiRequest) method.getAnnotation(API_REQUEST_CLASS);
					String wholePath = classAnnotationInstance.path() + methodAnnotationInstance.path();
					lookup.computeIfAbsent(wholePath, k -> new ArrayList<>()).add(
							new EndpointHandler(clazz, method, wholePath, methodAnnotationInstance.method())
					);
				}
			}
		}
	}
	
	/**
	 * Handles the incoming request and routes to relevant controller handler after parsing the input information.
	 *
	 * @param data Data.
	 * @return Object
	 */
	@Override
	public Object process(String data) {
		try {
			if (Objects.isNull(data) || data.isEmpty())
				throw new ApiException("No payload received", HttpStatus.BAD_REQUEST);  //No Payload Received.
			ApiRequest.ApiRequestType requestMethod = getRequestMethodType(data);   //Fetch request method from
			// payload data.
			String endpoint = getEndPoint(data); //Fetch endpoint data.
			List<EndpointHandler> endpointHandlers = findEndpointHandler(endpoint); //Fetch endpoint handler POJO.
			EndpointHandler endpointHandler = null;
			for (EndpointHandler handler : endpointHandlers) {      //Find the right endpoint handler out of multiple
				// handlers based on request method.
				if (handler.getRequestMethod().equals(requestMethod)) {
					endpointHandler = handler;
					break;
				}
			}
			if (Objects.isNull(endpointHandler))
				throw new ApiException("No handler found", HttpStatus.NOT_FOUND);
			Map<String, String> headers = getHeaders(data); //Fetch headers from payload data.
			Map<String, String> requestParameters = getQueryParams(endpoint);   //Fetch query parameters from payload
			// data.
			Map<String, String> pathParameters = getPathParams(endpoint, endpointHandler.getPathTemplate());
			//Fetch path parameters from payload data.
			String payload = getData(data); //Fetch data as String
			Class<?> clazz = endpointHandler.getClazz();
			Method method = endpointHandler.getMethod();    //Get method object from endpoint handler.
			Parameter[] inputParametersRequired = method.getParameters();   //Get annotated parameter types.
			Object[] inputParams = new Object[inputParametersRequired.length];  //Create a object array with the same
			// length as parameter type.
			for (int i = 0; i < inputParametersRequired.length; i++) {
				Type type = inputParametersRequired[i].getType();   //Find parameter type.
				Class<?> parameterType = ClassLoader.getSystemClassLoader().loadClass(type.getTypeName());
				if (inputParametersRequired[i].isAnnotationPresent(API_REQUEST_BODY)) {
					ApiRequestBody apiRequestBodyInstance =
							(ApiRequestBody) inputParametersRequired[i].getAnnotation(API_REQUEST_BODY);
					if (payload.isEmpty() && apiRequestBodyInstance.required()) {
						throw new ApiException("Payload data is required to process!", HttpStatus.BAD_REQUEST);
					} else if (payload.isEmpty()) {
						inputParams[i] = null;
					} else {
						inputParams[i] = getParser(headers.getOrDefault(CONTENT_TYPE, DEFAULT_CONTENT_TYPE))
								                 .parse(payload, parameterType);
					}
				} else if (inputParametersRequired[i].isAnnotationPresent(API_PATH_VARIABLE)) {
					ApiPathVariable apiPathVariableInstance =
							(ApiPathVariable) inputParametersRequired[i].getAnnotation(API_PATH_VARIABLE);
					if (apiPathVariableInstance.required()
							    && !pathParameters.containsKey(apiPathVariableInstance.value())) {
						throw new ApiException("Requested path parameter is not received!", HttpStatus.BAD_REQUEST);
					}
					inputParams[i] = pathParameters.get(apiPathVariableInstance.value());
				} else if (inputParametersRequired[i].isAnnotationPresent(API_QUERY_PARAMETER)) {
					ApiQueryParameter apiQueryParameterInstance =
							(ApiQueryParameter) inputParametersRequired[i].getAnnotation(API_QUERY_PARAMETER);
					if (apiQueryParameterInstance.required()
							    && !requestParameters.containsKey(apiQueryParameterInstance.value())
							    && apiQueryParameterInstance.defaultValue().equals(DEFAULT_VALUE)) {
						throw new ApiException("Requested query parameter is not received!", HttpStatus.BAD_REQUEST);
					}
					inputParams[i] = requestParameters.getOrDefault(
							apiQueryParameterInstance.value(),
							apiQueryParameterInstance.defaultValue()
					);
				} else if (inputParametersRequired[i].isAnnotationPresent(API_REQUEST_HEADER)) {
					ApiRequestHeader apiRequestHeaderInstance =
							(ApiRequestHeader) inputParametersRequired[i].getAnnotation(API_REQUEST_HEADER);
					if (apiRequestHeaderInstance.required()
							    && !headers.containsKey(apiRequestHeaderInstance.value())
							    && apiRequestHeaderInstance.defaultValue().equals(DEFAULT_VALUE)) {
						throw new ApiException("Requested header is not received!", HttpStatus.BAD_REQUEST);
					}
					inputParams[i] = headers.getOrDefault(
							apiRequestHeaderInstance.value(),
							apiRequestHeaderInstance.defaultValue()
					);
				} else
					inputParams[i] = null;
			}
			return method.invoke(ctx.getBean(clazz), inputParams);
		} catch (InvocationTargetException invocationTargetException) {
			log.error("invocation {}", invocationTargetException.getMessage());
			Class<?> ec = invocationTargetException.getCause().getClass();
			try {
				Constructor<?> constructor = ec.getConstructor(String.class, Throwable.class);
				throw (RuntimeException) constructor.newInstance(invocationTargetException.getCause().getMessage(),
						invocationTargetException.getCause().getCause());
				
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				log.error("{} {}", e.getMessage(), e.getStackTrace());
				throw new ApiException("Unknown Error!", invocationTargetException.getCause());
			}
		} catch (RuntimeException e) {
			log.error("Exception occurred while trying to process the request. Exception: {}, " +
					          "Exception stack trace: {}", e, e.getStackTrace());
			throw e;
		} catch (Exception e) {
			log.error("Unknown exception occurred while trying to process data. Exception: {}, " +
					          "Exception stack trace: {}", e, e.getStackTrace());
			throw new ApiException("Unknown error occurred.", e.getCause());
		}
	}
	
	/**
	 * Fetches query parameters from the provided URI Path.
	 * For example:
	 * For input URI path, {@code "/something/with?queryParam1=Value1&queryParam2=Value2"},
	 * response will contain, {@code Map{queryParam1=Value1, queryParam2=Value2}}
	 *
	 * @param uriPath URI Path.
	 * @return {@link Map}
	 */
	private Map<String, String> getQueryParams(String uriPath) {
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
	 * @return {@link Map}
	 */
	private Map<String, String> getPathParams(String uriPath, String uriTemplate) {
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
	
	/**
	 * Finds the relevant endpoint handlers that suits given path.
	 *
	 * @param wholePath Whole URL Path that is received in the request.
	 * @return {@link List}
	 */
	private List<EndpointHandler> findEndpointHandler(String wholePath) {
		wholePath = wholePath.split(REGEX_QUESTION_MARK)[0];
		if (lookup.containsKey(wholePath))
			return lookup.get(wholePath);
		for (String path : lookup.keySet()) {
			if (matchTemplate(path, wholePath)) {
				return lookup.get(path);
			}
		}
		throw new NotFoundException("Endpoint not found!");
	}
	
	/**
	 * Matches given real-time URL path with the URI Template provided.
	 *
	 * @param uriTemplate URI Template.
	 * @param uriPath     Real-time URL Path.
	 * @return {@code boolean}
	 */
	private boolean matchTemplate(String uriTemplate, String uriPath) {
		if (Objects.isNull(uriPath) || uriPath.isEmpty() || Objects.isNull(uriTemplate) || uriTemplate.isEmpty()) {
			log.error("Empty uriPath or uriTemplate provided.");
			return false;
		}
		String[] uriPathNames = uriPath.split(FORWARD_SLASH);
		String[] uriTemplateNames = uriTemplate.split(FORWARD_SLASH);
		if (uriPathNames.length != uriTemplateNames.length) {
			log.error("uriPath length and uriTemplate length is not matching.");
			return false;
		}
		for (int i = 0; i < uriPathNames.length; i++) {
			if (uriTemplateNames[i].startsWith("{"))
				continue;
			if (!uriTemplateNames[i].equals(uriPathNames[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Fetches {@link ApiRequest.ApiRequestType} from raw payload of the request.
	 *
	 * @param input Raw payload request as String.
	 * @return {@link ApiRequest.ApiRequestType}
	 */
	private ApiRequest.ApiRequestType getRequestMethodType(String input) {
		String command = Objects.requireNonNull(input, "Invalid Command").split(SPACE)[0];
		return ApiRequest.ApiRequestType.requestTypeOf(command).orElseThrow(() -> new InvalidCommandException(
				"Invalid" +
						" Command - " + command));
	}
	
	/**
	 * Fetches endpoint from raw payload of the request.
	 *
	 * @param input Raw payload request as String.
	 * @return {@link String}
	 */
	private String getEndPoint(String input) {
		try {
			return Objects.requireNonNull(input.split(NEW_LINE)[0], "Invalid Command").split(SPACE)[1];
		} catch (Exception e) {
			throw new InvalidCommandException("Invalid Command", e.getCause());
		}
	}
	
	/**
	 * Fetches headers from raw payload of the request.
	 *
	 * @param input Raw payload request as String.
	 * @return {@link String}
	 */
	private Map<String, String> getHeaders(String input) {
		try {
			int newLineIndex = input.indexOf(NEW_LINE);
			if (newLineIndex == -1) {
				return new HashMap<>();
			}
			int doubleNewLineIndex = input.indexOf(DOUBLE_NEW_LINE);
			if (newLineIndex == doubleNewLineIndex) {
				return new HashMap<>();
			}
			String headerAsString = input.substring(newLineIndex + 1, doubleNewLineIndex);
			return getHeadersAsMap(headerAsString.split(NEW_LINE));
		} catch (Exception e) {
			throw new InvalidCommandException("Invalid Command", e.getCause());
		}
	}
	
	/**
	 * Fetches Headers as Map from provided headers String array.
	 *
	 * @param headersArray Array of String containing header key and header value split by " : "
	 * @return {@link Map}
	 */
	private Map<String, String> getHeadersAsMap(String[] headersArray) {
		Map<String, String> headers = new HashMap<>();
		for (String header : headersArray) {
			int colonIndex = header.indexOf(":");
			headers.put(header.substring(0, colonIndex).trim(), header.substring(colonIndex + 1).trim());
		}
		return headers;
	}
	
	/**
	 * Fetches payload data as String from raw payload of the request.
	 *
	 * @param input Raw payload request as String.
	 * @return String
	 */
	private String getData(String input) {
		String[] split = Objects.requireNonNull(input, "Invalid Command").split(DOUBLE_NEW_LINE);
		if (split.length < 2) {
			return "";
		}
		return String.join(DOUBLE_NEW_LINE, Arrays.copyOfRange(split, 1, split.length));
	}
	
	/**
	 * Fetches relevant parser from context by taking in "content-type" value from the header.
	 *
	 * @param mediaType Media Type.
	 * @return {@link Parser}
	 */
	private Parser getParser(String mediaType) {
		try {
			return (Parser) ctx.getBean("parser-" + mediaType);
		} catch (Exception e) {
			throw new InvalidContentTypeException("Invalid Command");
		}
	}
	
	/**
	 * {@code EndpointHandler} is a class which holds information required to dynamically invoke the relevant
	 * instance of a method.
	 */
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	@ToString
	private static class EndpointHandler {
		Class<?> clazz;
		Method method;
		String pathTemplate;
		ApiRequest.ApiRequestType requestMethod;
	}
}
