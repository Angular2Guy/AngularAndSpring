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
package ch.xxx.trader.usecase.services;

import java.util.Collection;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.reactivestreams.client.MongoCollection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MyMongoRepository {
	
	<T> Mono<T> findOne(Query query, Class<T> entityClass);
	
	<T> Mono<T> findOne(Query query, Class<T> entityClass, String name);
	
	<T> Flux<T> find(Query query, Class<T> entityClass);
	
	<T> Flux<T> find(Query query, Class<T> entityClass, String collectionName);
	
	<T> Flux<T> insertAll(Mono<? extends Collection<? extends T>> batchToSave, String collectionName);
	
	Mono<Boolean> collectionExists(String collectionName);
	
	Mono<MongoCollection<Document>> createCollection(String collectionName);
	
	<T> Mono<T> save(T objectToSave);
}
