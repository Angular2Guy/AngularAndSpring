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
package ch.xxx.trader.adapter.events;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ch.xxx.trader.adapter.config.KafkaConfig;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import ch.xxx.trader.domain.services.MyEventProducer;
import ch.xxx.trader.usecase.mappers.EventMapper;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;

@Profile("kafka | prod")
@Service
public class EventProducer implements MyEventProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventProducer.class);
	private final KafkaSender<String, String> kafkaSender;
	private final EventMapper eventMapper;

	public EventProducer(KafkaSender<String, String> kafkaSender, EventMapper eventMapper) {
		this.kafkaSender = kafkaSender;
		this.eventMapper = eventMapper;
	}

	public Mono<MyUser> sendNewUser(MyUser dto) {
		String dtoJson = this.eventMapper.mapDtoToString(dto);
		return this.kafkaSender.createOutbound()
				.send(Mono.just(new ProducerRecord<>(KafkaConfig.NEW_USER_TOPIC, dto.getSalt(), dtoJson)))
				.then()
				.doOnError(e -> LOGGER.error(
						String.format("Failed to send topic: %s value: %s", KafkaConfig.NEW_USER_TOPIC, dtoJson), e))
				.thenReturn(dto);
				
	}

	public Mono<RevokedToken> sendUserLogout(RevokedToken dto) {
		String dtoJson = this.eventMapper.mapDtoToString(dto);
		return this.kafkaSender.createOutbound()
				.send(Mono.just(new ProducerRecord<>(KafkaConfig.USER_LOGOUT_SOURCE_TOPIC, dto.getName(), dtoJson)))
				.then()
				.doOnError(e -> LOGGER.error(String.format("Failed to send topic: %s value: %s",
						KafkaConfig.USER_LOGOUT_SOURCE_TOPIC, dtoJson), e))
				.thenReturn(dto);
	}
}
