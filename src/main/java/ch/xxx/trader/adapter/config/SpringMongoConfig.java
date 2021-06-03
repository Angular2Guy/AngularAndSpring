/**
 *    Copyright 2016 Sven Loesekann

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
package ch.xxx.trader.adapter.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.reactivestreams.ReactiveStreamsMongoLockProvider;

@Configuration
public class SpringMongoConfig  {
	private static final Logger log = LoggerFactory.getLogger(SpringMongoConfig.class);
	private static final String SCHED_LOCK_DB = "schedLock";
	
    @Value("${spring.data.mongodb.host}")
    private String mongoHost;        	

    private final MongoMappingContext mongoMappingContext;
    
    public SpringMongoConfig(MongoMappingContext mongoMappingContext) {
    	this.mongoMappingContext = mongoMappingContext;
    }

    @PostConstruct
    public void init() {
    	this.mongoMappingContext.setAutoIndexCreation(true);
    	log.info("Mongo AutoIndexCreation: {}", this.mongoMappingContext.isAutoIndexCreation());
    }
    
    @Bean
    public MongoClient reactiveMongoClient()  {
    	String myHost = System.getenv("MONGODB_HOST");		
		log.info("MONGODB_HOST="+myHost);
        if(myHost==null) {
        	return MongoClients.create();
        }
		return MongoClients.create("mongodb://"+ myHost);
    }	

    @Bean
	public ServerCodecConfigurer serverCodecConfigurer() {
		return new DefaultServerCodecConfigurer();
	}
    
    @Bean
    public LockProvider lockProvider(MongoClient mongo) {
        return new ReactiveStreamsMongoLockProvider(mongo.getDatabase(SCHED_LOCK_DB));
    }
}
