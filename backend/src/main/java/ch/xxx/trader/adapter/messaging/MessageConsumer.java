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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ch.xxx.trader.adapter.config.KafkaConfig;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Profile("kafka | prod-kafka")
@Component
public class MessageConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);
	private final ReceiverOptions<String,String> receiverOptions;
	private final KafkaReceiver<String,String> userLogoutReceiver;
	private final KafkaReceiver<String,String> newUserReceiver;
	@Value("${spring.kafka.consumer.group-id}")
	private String consumerGroupId;
	
	public MessageConsumer(ReceiverOptions<String,String> receiverOptions) {
		this.receiverOptions = receiverOptions;
		this.userLogoutReceiver = KafkaReceiver.create(this.receiverOptions(List.of(KafkaConfig.USER_LOGOUT_SINK_TOPIC)));
		this.newUserReceiver = KafkaReceiver.create(this.receiverOptions(List.of(KafkaConfig.NEW_USER_TOPIC)));
	}
	
    private ReceiverOptions<String, String> receiverOptions(Collection<String> topics) {
        return this.receiverOptions
                .addAssignListener(p -> LOGGER.info("Group {} partitions assigned {}", this.consumerGroupId, p))
                .addRevokeListener(p -> LOGGER.info("Group {} partitions revoked {}", this.consumerGroupId, p))
                .subscription(topics);
    }
	
	public void sendNewUser() {
		
	}
}
