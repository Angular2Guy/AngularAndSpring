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

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.SlidingWindows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.trader.adapter.config.KafkaConfig;
import ch.xxx.trader.domain.model.dto.RevokedTokensDto;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import ch.xxx.trader.usecase.common.LastlogoutTimestampExtractor;

@Profile("kafka | prod-kafka")
@Component
public class KafkaStreams {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreams.class);
	private static final long LOGOUT_TIMEOUT = 90L;
	private static final long GRACE_TIMEOUT = 5L;
	private ObjectMapper objectMapper;

	public KafkaStreams(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Bean("UserLogoutTopology")
	public Topology userLogout(final StreamsBuilder builder) {
		builder.stream(KafkaConfig.USER_LOGOUT_SOURCE_TOPIC, Consumed.with(Serdes.String(), Serdes.String()))
				.groupByKey()
				.windowedBy(SlidingWindows.ofTimeDifferenceAndGrace(Duration.ofSeconds(KafkaStreams.LOGOUT_TIMEOUT),
						Duration.ofSeconds(KafkaStreams.GRACE_TIMEOUT)))
				.aggregate(LinkedList<String>::new, (key, value, myList) -> {
//					LOGGER.info("Logout Stream value: {}", value);
//					LOGGER.info("Logout Stream key: {}", key);
					myList.add(value);
//					LOGGER.info("Logout Stream myList: {}", myList.stream().collect(Collectors.joining(",")));
					return myList;
				}, Materialized.with(Serdes.String(), Serdes.ListSerde(LinkedList.class, Serdes.String())))				
				.toStream()
				.mapValues(value -> convertToRevokedTokens((List<String>) value))
				.to(KafkaConfig.USER_LOGOUT_SINK_TOPIC);
		Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
				LastlogoutTimestampExtractor.class.getName());
		streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, -1L);
		streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000L);
		return builder.build(streamsConfiguration);
	}

	private String convertToRevokedTokens(List<String> value) {
		try {
			List<RevokedToken> revokedTokenList = value.stream().map(myValue -> {
				try {
					return this.objectMapper.readValue(myValue, RevokedToken.class);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}).toList();
			return this.objectMapper.writeValueAsString(new RevokedTokensDto(revokedTokenList));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
