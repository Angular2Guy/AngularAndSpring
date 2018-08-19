package ch.xxx.trader.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import ch.xxx.trader.data.ScheduledTask;

@Configuration
public class SpringMongoConfig extends AbstractReactiveMongoConfiguration {
	private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
	
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private String mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDB;

	@Override
	protected String getDatabaseName() {
		return mongoDB;
	}

	@Override
	public MongoClient reactiveMongoClient() {
		String myHost = System.getenv("MONGODB_HOST");		
		log.info("MONGODB_HOST="+myHost);			
		return MongoClients.create("mongodb://"+(myHost==null ? mongoHost : myHost)+":"+mongoPort);
	}	

}
