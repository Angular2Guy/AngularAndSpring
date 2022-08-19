/**
 *    Copyright 2016 Sven Loesekann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.trader.adapter.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler({ Exception.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public Object handleException(final Exception exception, final HttpServletRequest request) {
		record MyEntry(String key, Object value) {
		}
		LOGGER.info(String.format("Execption: %s", exception.getMessage()), exception);
		LOGGER.info("Remote Ip: {}", request.getRemoteAddr());
		LOGGER.info("Request URL: {}", request.getRequestURL());
		Map<String, Object> attributeMap = Collections.list(request.getAttributeNames()).stream()
				.flatMap(attName -> Stream.of(new MyEntry(attName, request.getAttribute(attName))))
				.collect(Collectors.toMap(myEntry -> myEntry.key, myEntry -> myEntry.value));
		LOGGER.info("Request Attributes: {}", this.createStringFromMap(attributeMap));
		Map<String, Object> headerMap = Collections.list(request.getHeaderNames()).stream()
				.flatMap(headerName -> Stream.of(new MyEntry(headerName, request.getHeader(headerName))))
				.collect(Collectors.toMap(myEntry -> myEntry.key, myEntry -> myEntry.value));
		LOGGER.info("Request Headers: {}", this.createStringFromMap(headerMap));
		LOGGER.info("Request Body length: {}", request.getContentLength());
		try {
			LOGGER.info("Request Body content: {}", new String(request.getInputStream().readAllBytes()));
		} catch (IOException e) {
			LOGGER.warn("Failed to display body.", e);
		}
		return new Object();
	}

	private <K, V> String createStringFromMap(Map<K, V> myMap) {
		return myMap.entrySet().stream()
				.map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue() == null ? "" : entry.getValue()))
				.collect(Collectors.joining(" | "));
	}
}