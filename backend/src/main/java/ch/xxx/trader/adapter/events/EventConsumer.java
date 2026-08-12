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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import ch.xxx.trader.adapter.config.KafkaConfig;
import ch.xxx.trader.domain.model.dto.RevokedTokensDto;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.usecase.mappers.EventMapper;
import ch.xxx.trader.usecase.services.MyUserServiceEvents;

@Profile("kafka | prod")
@Service
public class EventConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
	private final MyUserServiceEvents myUserServiceEvents;
	private final EventMapper eventMapper;

	public EventConsumer(MyUserServiceEvents myUserServiceEvents, EventMapper eventMapper) {
		this.myUserServiceEvents = myUserServiceEvents;
		this.eventMapper = eventMapper;
	}

	@KafkaListener(topics = KafkaConfig.NEW_USER_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
	public void onNewUser(String message) {
		LOGGER.info("New user event received.");
		this.myUserServiceEvents.userSigninEvent(this.eventMapper.mapJsonToObject(message, MyUser.class));
	}

	@KafkaListener(topics = KafkaConfig.USER_LOGOUT_SINK_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
	public void onUserLogout(String message) {
		LOGGER.info("Logout event received.");
		this.myUserServiceEvents.logoutEvent(this.eventMapper.mapJsonToObject(message, RevokedTokensDto.class));
	}
}