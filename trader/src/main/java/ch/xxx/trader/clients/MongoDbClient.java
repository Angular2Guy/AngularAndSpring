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
package ch.xxx.trader.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.scheduling.annotation.EnableScheduling;

import ch.xxx.trader.TraderApplication;

@SpringBootApplication
@EnableScheduling
@ComponentScan
public class MongoDbClient {
		
//    @Autowired
//    private ReactiveMongoOperations operations;
//	public static void main(String[] args) {
//		SpringApplication.run(TraderApplication.class, args);		
	
//		mdbc.operations.collectionExists(QuoteBs.class)
//		.flatMap(col -> col ? mdbc.operations.dropCollection(QuoteBs.class) : Mono.just(col))
//		.flatMap(o -> mdbc.operations.createCollection(QuoteBs.class))
//		.then()
//		.block();
		
//	}

}
