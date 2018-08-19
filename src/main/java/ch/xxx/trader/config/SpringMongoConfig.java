package ch.xxx.trader.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;

import ch.xxx.trader.data.ScheduledTask;

@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {
	private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
	
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private String mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDB;
	
	@Override
	public MongoClient mongoClient() {	
		String myHost = System.getenv("MONGODB_HOST");
		log.info("MONGODB_HOST="+myHost);		
		return new MongoClient(myHost != null ? myHost : mongoHost + ":" + mongoPort);
	}

	@Override
	protected String getDatabaseName() {
		return mongoDB;
	}

}
