/**
 *    Copyright 2019 Sven Loesekann
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
package ch.xxx.trader.adapter.messaging;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.trader.adapter.config.KafkaConfig;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;

@Profile("kafka | prod-kafka")
@Component
public class MessageProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);
	private final KafkaSender<String, String> kafkaSender;
	private final ObjectMapper objectMapper;

	public MessageProducer(KafkaSender<String, String> kafkaSender, ObjectMapper objectMapper) {
		this.kafkaSender = kafkaSender;
		this.objectMapper = objectMapper;
	}

	public void sendNewUser(MyUser dto) {
		String dtoJson = mapDtoToString(dto);
		this.kafkaSender.createOutbound()
				.send(Mono.just(new ProducerRecord<>(KafkaConfig.NEW_USER_TOPIC, dto.getSalt(), dtoJson))).then()
				.doOnError(e -> LOGGER.error(
						String.format("Failed to send topic: %s value: %s", KafkaConfig.NEW_USER_TOPIC, dtoJson), e))
				.subscribe();
	}

	private String mapDtoToString(Object dto) {
		String dtoJson;
		try {
			dtoJson = this.objectMapper.writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return dtoJson;
	}

	public void sendUserLogout(RevokedToken dto) {
		String dtoJson = this.mapDtoToString(dto);
		this.kafkaSender.createOutbound()
				.send(Mono.just(new ProducerRecord<>(KafkaConfig.USER_LOGOUT_SOURCE_TOPIC, dto.getUuid(), dtoJson)))
				.then().doOnError(e -> LOGGER.error(String.format("Failed to send topic: %s value: %s",
						KafkaConfig.USER_LOGOUT_SOURCE_TOPIC, dtoJson), e))
				.subscribe();
	}
}
