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

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import ch.xxx.trader.adapter.config.KafkaConfig;
import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.usecase.mappers.MessageMapper;
import ch.xxx.trader.usecase.services.MyUserServiceMessaging;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Profile("kafka | prod-kafka")
@Service
public class MessageConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);
	private final ReceiverOptions<String, String> receiverOptions;
	private final KafkaReceiver<String, String> userLogoutReceiver;
	private final KafkaReceiver<String, String> newUserReceiver;
	private final MyUserServiceMessaging myUserServiceMessaging;
	private final MessageMapper messageMapper;
	@Value("${spring.kafka.consumer.group-id}")
	private String consumerGroupId;

	public MessageConsumer(MyUserServiceMessaging myUserServiceMessaging, ReceiverOptions<String, String> receiverOptions, MessageMapper messageMapper) {
		this.receiverOptions = receiverOptions;
		this.userLogoutReceiver = KafkaReceiver
				.create(this.receiverOptions(List.of(KafkaConfig.USER_LOGOUT_SINK_TOPIC)));
		this.newUserReceiver = KafkaReceiver.create(this.receiverOptions(List.of(KafkaConfig.NEW_USER_TOPIC)));
		this.myUserServiceMessaging = myUserServiceMessaging;
		this.messageMapper = messageMapper;
	}

	private ReceiverOptions<String, String> receiverOptions(Collection<String> topics) {
		return this.receiverOptions
				.addAssignListener(p -> LOGGER.info("Group {} partitions assigned {}", this.consumerGroupId, p))
				.addRevokeListener(p -> LOGGER.info("Group {} partitions revoked {}", this.consumerGroupId, p))
				.subscription(topics);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doOnStartup() {
		this.newUserReceiver.receiveAtmostOnce().flatMap(myRecord -> this.myUserServiceMessaging
				.postUserSignin(this.messageMapper.mapJsonToObject(myRecord.value(), MyUser.class))).subscribe();
		this.userLogoutReceiver.receiveAtmostOnce().flatMap(myRecord -> this.myUserServiceMessaging
				.postLogout(myRecord.value())).subscribe();
	}	
}
