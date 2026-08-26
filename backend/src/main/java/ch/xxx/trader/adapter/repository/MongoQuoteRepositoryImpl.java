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

import ch.xxx.trader.domain.model.entity.Quote;
import ch.xxx.trader.domain.services.MongoQuoteRepository;
import ch.xxx.trader.domain.services.MyTimeFrame;
import ch.xxx.trader.usecase.common.DtoUtils;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MongoQuoteRepositoryImpl implements MongoQuoteRepository {
	protected final MongoOperations operations;

	protected MongoQuoteRepositoryImpl(MongoOperations operations) {
		this.operations = operations;
	}

	@Override
	public <T extends Quote> void ensureIndex(Class<T> entityClass) {
		Index myIndex = new Index(DtoUtils.CREATEDAT, Direction.DESC).named(entityClass.getSimpleName() + "-" + DtoUtils.CREATEDAT);
		this.operations.indexOps(entityClass).createIndex(myIndex);
	}

	private <T extends Quote> Optional<T> findFirstLastQuote(Class<T> entityClass, boolean first) {
		Query query = new Query();
		query.limit(Limit.of(1));
		query.with(Sort.by(first ? Direction.ASC : Direction.DESC, DtoUtils.CREATEDAT));
		return Optional.ofNullable(this.operations.findOne(query, entityClass));
	}

	@Override
	public <A extends Quote, B extends Quote> MyTimeFrame createTimeFrame(Class<A> entityClass, Class<B> aggreateEntityClass, boolean hour) {
		if (!this.operations.collectionExists(entityClass)) {
			this.operations.createCollection(entityClass);
		}
		final Calendar globalBeginn = Calendar.getInstance();
		Optional<T> lastAggregate = this.findFirstLastQuote(aggreateEntityClass, false);
		lastAggregate.ifPresentOrElse(myQuote -> this.calcGlobalBegin(hour, globalBeginn, myQuote), () -> {
			Optional<T> firstQuote = this.findFirstLastQuote(entityClass, true);
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