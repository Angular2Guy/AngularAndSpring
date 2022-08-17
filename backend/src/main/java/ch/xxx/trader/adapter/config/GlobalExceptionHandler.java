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

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	public GlobalExceptionHandler(ErrorAttributes errorAttributes, Resources resources,
			ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
		super(errorAttributes, resources, applicationContext);
	    super.setMessageWriters(serverCodecConfigurer.getWriters());
	    super.setMessageReaders(serverCodecConfigurer.getReaders());
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}

	private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		LOGGER.info("Remote Ip: {}", request.remoteAddress());
		LOGGER.info("Request URI: {}", request.uri());
		LOGGER.info("Request Attributes: {}", this.createStringFromMap(request.attributes()));
		LOGGER.info("Request Headers: {}", this.createStringFromMap(request.headers().asHttpHeaders().toSingleValueMap()));
		LOGGER.info("Request Body length: {}", request.body(BodyExtractors.toMono(String.class)).block().length());
		LOGGER.info("Request Body content: {}", request.body(BodyExtractors.toMono(String.class)).block());
		Map<String, Object> errorPropertiesMap = this.getErrorAttributes(request,
				ErrorAttributeOptions.of(Include.values()));
		LOGGER.info("ErrorPropertiesMap: ", this.createStringFromMap(errorPropertiesMap));
		return ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.empty());
	}
	
	private <K,V> String createStringFromMap(Map<K,V> myMap) {
		return myMap.entrySet().stream()
				.map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue() == null ? "" : entry.getValue()))
				.collect(Collectors.joining(" | "));
	}
}