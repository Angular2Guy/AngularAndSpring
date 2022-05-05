package ch.xxx.trader.adapter.config;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.DefaultHostResolver;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("kafka | prod-kafka")
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);
	private static final String GZIP = "gzip";
	private static final String ZSTD = "zstd";
	public static final String NEW_USER_TOPIC = "new-user-topic";
	public static final String NEW_USER_DLT_TOPIC = "new-user-topic-retry";
	public static final String USER_LOGOUT_SOURCE_TOPIC = "user-logout-source-topic";
	public static final String USER_LOGOUT_SINK_TOPIC = "user-logout-sink-topic";
	public static final String USER_LOGOUT_SINK_DLT_TOPIC = "user-logout-sink-topic-retry";

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;
	@Value("${spring.kafka.producer.compression-type}")
	private String compressionType;
	
	@PostConstruct
	public void init() {
		String bootstrap = this.bootstrapServers.split(":")[0].trim();
		if (bootstrap.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
			DefaultHostResolver.IP_ADDRESS = bootstrap;
		} else if(!bootstrap.isEmpty()) {
			DefaultHostResolver.KAFKA_SERVICE_NAME = bootstrap;
		}
		LOGGER.info("Kafka Servername: {} Kafka Servicename: {} Ip Address: {}", DefaultHostResolver.KAFKA_SERVER_NAME,
				DefaultHostResolver.KAFKA_SERVICE_NAME, DefaultHostResolver.IP_ADDRESS);
	}
	
	@Bean
	public NewTopic newUserTopic() {
		return TopicBuilder.name(KafkaConfig.NEW_USER_TOPIC)
				.config(TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType).compact().build();
	}

	@Bean
	public NewTopic newUserDltTopic() {
		return TopicBuilder.name(KafkaConfig.NEW_USER_TOPIC)
				.config(TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType).compact().build();
	}
	
	@Bean
	public NewTopic userLogoutSourceTopic() {
		return TopicBuilder.name(KafkaConfig.USER_LOGOUT_SOURCE_TOPIC)
				.config(TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType).compact().build();
	}
	
	@Bean
	public NewTopic userLogoutSinkTopic() {
		return TopicBuilder.name(KafkaConfig.USER_LOGOUT_SINK_TOPIC)
				.config(TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType).compact().build();
	}
	
	@Bean
	public NewTopic userLogoutSinkDltTopic() {
		return TopicBuilder.name(KafkaConfig.USER_LOGOUT_SINK_DLT_TOPIC)
				.config(TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType).compact().build();
	}
}
