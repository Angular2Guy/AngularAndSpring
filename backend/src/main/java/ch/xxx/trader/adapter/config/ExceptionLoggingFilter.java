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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionLoggingFilter extends GenericFilterBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLoggingFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		try {
			chain.doFilter(req, res);
		} catch (RequestRejectedException exception) {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			record MyEntry(String key, Object value) {
			}
			LOGGER.info(String.format("Exception: %s", exception.getMessage()));
			LOGGER.info("Remote Ip: {}", request.getRemoteAddr());
			LOGGER.info("Request URL: {}", request.getRequestURL());
			Map<String, Object> attributeMap = Collections.list(request.getAttributeNames()).stream()
					.flatMap(attName -> Stream.of(new MyEntry(attName, request.getAttribute(attName))))
					.collect(Collectors.toMap(myEntry -> myEntry.key, myEntry -> myEntry.value));
			LOGGER.debug("Request Attributes: {}", this.createStringFromMap(attributeMap));
			Map<String, Object> headerMap = Collections.list(request.getHeaderNames()).stream()
					.flatMap(headerName -> Stream.of(new MyEntry(headerName, request.getHeader(headerName))))
					.collect(Collectors.toMap(myEntry -> myEntry.key, myEntry -> myEntry.value));
			LOGGER.info("Request Headers: {}", this.createStringFromMap(headerMap));
			LOGGER.info("Request Body length: {}", request.getContentLength());
			try {
				LOGGER.debug("Request Body content: {}", new String(request.getInputStream().readAllBytes()));
			} catch (IOException e) {
				LOGGER.warn("Failed to display body.", e);
			}

			LOGGER.warn("request_rejected: remote={}, user_agent={}, request_url={}", request.getRemoteHost(),
					request.getHeader(HttpHeaders.USER_AGENT), request.getRequestURL(), exception);

			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private <K, V> String createStringFromMap(Map<K, V> myMap) {
		return myMap.entrySet().stream()
				.map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue() == null ? "" : entry.getValue()))
				.collect(Collectors.joining(" | "));
	}
}
