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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ch.xxx.trader.domain.model.entity.Quote;
import ch.xxx.trader.usecase.common.DtoUtils;
import ch.xxx.trader.usecase.services.ServiceUtils.MyTimeFrame;

public abstract class AbstractQuoteRepositoryImpl<T extends Quote> implements QuoteRepository<T> {
	protected final MongoOperations operations;
	protected final Class<T> entityClass;

	protected AbstractQuoteRepositoryImpl(MongoOperations operations, Class<T> entityClass) {
		this.operations = operations;
		this.entityClass = entityClass;
	}

	@Override
	public List<T> findQuotesSince(String collectionName, Date since) {
		return findQuotesSince(collectionName, since, null, 1000);
	}

	@Override
	public List<T> findQuotesSince(String collectionName, Date since, String pair, int limit) {
		Query query = new Query();
		query.allowDiskUse(true);
		query.limit(Math.min(limit, 5000));
		query.addCriteria(Criteria.where(DtoUtils.CREATEDAT).gt(since));
		Optional.ofNullable(pair).ifPresent(myPair -> query.addCriteria(Criteria.where("pair").is(myPair)));
		query.with(Sort.by(Direction.ASC, DtoUtils.CREATEDAT));
		return this.operations.find(query, this.entityClass, collectionName);
	}

	@Override
	public Optional<T> findFirst(String collectionName, Direction direction) {
		Query query = new Query();
		query.limit(1);
		query.with(Sort.by(direction, DtoUtils.CREATEDAT));
		return Optional.ofNullable(this.operations.findOne(query, this.entityClass, collectionName));
	}

	@Override
	public void insertAll(String collectionName, Collection<? extends T> quotes) {
		this.operations.insert(quotes, collectionName);
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
	public void ensureIndex(String collectionName) {
		Index myIndex = new Index(DtoUtils.CREATEDAT, Direction.DESC).named(collectionName + "-" + DtoUtils.CREATEDAT);
		this.operations.indexOps(collectionName).ensureIndex(myIndex);
	}

	@Override
	public MyTimeFrame createTimeFrame(String collectionName, boolean hour) {
		if (!this.operations.collectionExists(collectionName)) {
			this.operations.createCollection(collectionName);
		}
		final Calendar globalBeginn = Calendar.getInstance();
		Optional<T> lastAggregate = this.findFirst(collectionName, Direction.DESC);
		lastAggregate.ifPresentOrElse(myQuote -> this.calcGlobalBegin(hour, globalBeginn, myQuote), () -> {
			Optional<T> firstQuote = this.findFirst(null, Direction.ASC);
			globalBeginn.setTime(firstQuote.map(Quote::getCreatedAt).orElse(
					Date.from(java.time.LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())));
		});

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(globalBeginn.getTime());
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		end.setTime(begin.getTime());
		end.add(Calendar.DAY_OF_YEAR, 1);
		return new MyTimeFrame(begin, end);
	}

	private void calcGlobalBegin(boolean hour, Calendar globalBeginn, Quote myQuote) {
		globalBeginn.setTime(myQuote.getCreatedAt());
		if (hour) {
			globalBeginn.add(Calendar.HOUR_OF_DAY, 1);
		} else {
			globalBeginn.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
}