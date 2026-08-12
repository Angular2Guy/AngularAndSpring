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
package ch.xxx.trader.adapter.repository;

import java.util.Collection;
import java.util.Optional;

import org.bson.Document;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.model.entity.MyMongoRepository;

@Service
public class ClientMongoRepository implements MyMongoRepository {
	private final MongoOperations operations;

	public ClientMongoRepository(MongoOperations operations) {
		this.operations = operations;
	}

	@Override
	public <T> T save(T objectToSave) {
		return this.operations.save(objectToSave);
	}

	@Override
	public <T> Optional<T> findOne(Query query, Class<T> entityClass) {
		return Optional.ofNullable(this.operations.findOne(query, entityClass));
	}

	@Override
	public <T> Optional<T> findOne(Query query, Class<T> entityClass, String name) {
		return Optional.ofNullable(this.operations.findOne(query, entityClass, name));
	}

	@Override
	public <T> Collection<T> find(Query query, Class<T> entityClass) {
		return this.operations.find(query, entityClass);
	}

	@Override
	public <T> Collection<T> find(Query query, Class<T> entityClass, String collectionName) {
		return this.operations.find(query, entityClass, collectionName);
	}

	@Override
	public <T> Collection<T> insertAll(Collection<? extends T> batchToSave, String collectionName) {
		return this.operations.insert(batchToSave, collectionName);
	}

	@Override
	public <T> T insert(T quote) {
		return this.operations.insert(quote);
	}

	@Override
	public <T> Optional<T> insertOptional(Optional<T> quote) {
		return quote.isPresent() ? Optional.of(this.operations.insert(quote.get())) : Optional.empty();
	}

	@Override
	public boolean collectionExists(String collectionName) {
		return this.operations.collectionExists(collectionName);
	}

	@Override
	public void createCollection(String collectionName) {
		this.operations.createCollection(collectionName);
	}

	@Override
	public void remove(Object quote) {
		this.operations.remove(quote);
	}

	@Override
	public String ensureIndex(String collectionName, String propertyName) {
		Index myIndex = new Index(propertyName, Direction.DESC);
		myIndex.named(collectionName + "-" + propertyName);
		return this.operations.indexOps(collectionName).ensureIndex(myIndex);
	}
}