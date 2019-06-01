package ch.xxx.trader.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
public class SpringMongoConfig  {
	private static final Logger log = LoggerFactory.getLogger(SpringMongoConfig.class);
	
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;        	

    public @Bean MongoClient reactiveMongoClient()  {
    	String myHost = System.getenv("MONGODB_HOST");		
		log.info("MONGODB_HOST="+myHost);
        if(myHost==null) {
        	return MongoClients.create();
        }
		return MongoClients.create("mongodb://"+ myHost);
    }	

    public @Bean com.mongodb.MongoClient mongoClient() {
    	String myHost = System.getenv("MONGODB_HOST");		
		log.info("MONGODB_HOST="+myHost);
		if(myHost==null) {
			return new com.mongodb.MongoClient();
		}
        return new com.mongodb.MongoClient(myHost);
    }    		
}
