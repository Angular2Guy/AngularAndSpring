package ch.xxx.trader.adapter.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.apache.kafka.clients.DefaultHostResolver;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Configuration
@Profile("kafka | prod")
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConfig.class);
	private static final String GZIP = "gzip";
	private static final String ZSTD = "zstd";
	private static final String SPRING_DESERIALIZER = "spring.deserializer.value.delegate.class";
	public static final String NEW_USER_TOPIC = "new-user-topic";
	public static final String NEW_USER_DLT_TOPIC = "new-user-topic-retry";
	public static final String USER_LOGOUT_SOURCE_TOPIC = "user-logout-source-topic";
	public static final String USER_LOGOUT_SINK_TOPIC = "user-logout-sink-topic";
	public static final String USER_LOGOUT_SINK_DLT_TOPIC = "user-logout-sink-topic-retry";

	private SenderOptions<String,String> senderOptions;
	private ReceiverOptions<String, String> receiverOptions;
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;
	@Value("${spring.kafka.producer.compression-type}")
	private String compressionType;
	@Value("${spring.kafka.producer.key-serializer}")
	private String producerKeySerializer;
	@Value("${spring.kafka.producer.value-serializer}")
	private String producerValueSerializer;
	@Value("${spring.kafka.producer.enable.idempotence}")
	private boolean enableIdempotence;
	@Value("${spring.kafka.consumer.group-id}")
	private String consumerGroupId;
	@Value("${spring.kafka.consumer.auto-offset-reset}")
	private String consumerAutoOffsetReset;
	@Value("${spring.kafka.consumer.key-deserializer}")
	private String consumerKeySerializer;
	@Value("${spring.kafka.consumer.value-deserializer}")
	private String consumerValueSerializer;

	@PostConstruct
	public void init() throws ClassNotFoundException {
		String bootstrap = this.bootstrapServers.split(":")[0].trim();
		if (bootstrap.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
			DefaultHostResolver.IP_ADDRESS = bootstrap;
		} else if (!bootstrap.isEmpty()) {
			DefaultHostResolver.KAFKA_SERVICE_NAME = bootstrap;
		}
		LOGGER.info("Kafka Servername: {} Kafka Servicename: {} Ip Address: {}", DefaultHostResolver.KAFKA_SERVER_NAME,
				DefaultHostResolver.KAFKA_SERVICE_NAME, DefaultHostResolver.IP_ADDRESS);
        Map<String,Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, this.compressionType);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Class.forName(this.producerKeySerializer));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Class.forName(this.producerValueSerializer));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Class.forName(this.producerKeySerializer));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Class.forName(this.producerKeySerializer));
        this.senderOptions = SenderOptions.create(props);
//        this.senderOptions.maxInFlight(10);
        props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "all");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, this.consumerAutoOffsetReset);        
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Class.forName(this.consumerKeySerializer));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Class.forName(this.consumerValueSerializer));
        props.put(SPRING_DESERIALIZER, this.consumerKeySerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        this.receiverOptions = ReceiverOptions.create(props);
        this.receiverOptions.pollTimeout(Duration.ofSeconds(1L));
	}

	@Bean
	public ReceiverOptions<?, ?> kafkaReceiverOptions() {
		return this.receiverOptions;
	}
	
	@Bean
	public KafkaSender<?, ?> kafkaSender() {
		return KafkaSender.create(this.senderOptions);
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
