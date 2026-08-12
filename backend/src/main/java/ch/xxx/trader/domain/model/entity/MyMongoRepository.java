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
package ch.xxx.trader.domain.model.entity;

import java.util.Collection;
import java.util.Optional;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;

public interface MyMongoRepository {
	
	<T> Optional<T> findOne(Query query, Class<T> entityClass);
	
	<T> Optional<T> findOne(Query query, Class<T> entityClass, String name);
	
	<T> Collection<T> find(Query query, Class<T> entityClass);
	
	<T> Collection<T> find(Query query, Class<T> entityClass, String collectionName);
	
	<T> Collection<T> insertAll(Collection<? extends T> batchToSave, String collectionName);
	
	<T> T insert(T quote);
	
	<T> Optional<T> insertOptional(Optional<T> quote);
	
	boolean collectionExists(String collectionName);
	
	void createCollection(String collectionName);
	
	<T> T save(T objectToSave);
	
	void remove(Object quote);
	
	String ensureIndex(String collectionName, String propertyName);
}