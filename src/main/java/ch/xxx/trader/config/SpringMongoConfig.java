package ch.xxx.trader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;

@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private String mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDB;
	
	@Override
	public MongoClient mongoClient() {	
		String myHost = System.getenv("MONGODB_HOST");
		return new MongoClient(myHost != null ? myHost : mongoHost + ":" + mongoPort);
	}

	@Override
	protected String getDatabaseName() {
		return mongoDB;
	}

}
