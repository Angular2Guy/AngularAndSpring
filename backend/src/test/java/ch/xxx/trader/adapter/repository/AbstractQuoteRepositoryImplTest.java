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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import ch.xxx.trader.domain.model.entity.QuoteBs;

@ExtendWith(MockitoExtension.class)
public class AbstractQuoteRepositoryImplTest {
	private static final String HOUR_COL = "quoteBsHour";
	private static final String DAY_COL = "quoteBsDay";

	@Mock
	private MongoOperations mongoOperations;

	private AbstractQuoteRepositoryImpl<QuoteBs> createRepository() {
		return new AbstractQuoteRepositoryImpl<>(this.mongoOperations, QuoteBs.class) {
			@Override
			protected Optional<QuoteBs> findLastQuote() {
				return Optional.empty();
			}

			@Override
			protected Optional<QuoteBs> findFirstQuote() {
				return Optional.empty();
			}

			@Override
			public java.util.List<QuoteBs> findByCreatedAtAfterOrderByCreatedAtAsc(Date date) {
				return java.util.List.of();
			}

			@Override
			public java.util.List<QuoteBs> findByCreatedAtAfterOrderByCreatedAtAsc(Date date, Limit limit) {
				return java.util.List.of();
			}

			@Override
			public java.util.List<QuoteBs> findByPairAndCreatedAtAfterOrderByCreatedAtAsc(String pair, Date date,
					Limit limit) {
				return java.util.List.of();
			}

			@Override
			public Optional<QuoteBs> findFirstByCreatedAtAfterOrderByCreatedAtDesc(Date date) {
				return Optional.empty();
			}

			@Override
			public Optional<QuoteBs> findFirstByPairAndCreatedAtAfterOrderByCreatedAtDesc(String pair, Date date) {
				return Optional.empty();
			}
		};
	}

	@Test
	public void insertTargetsNamedCollection() {
		List<QuoteBs> quotes = List.of(new QuoteBs(null, BigDecimal.TEN, null, null, null, BigDecimal.TEN, null, null,
				null));
		this.createRepository().insert(DAY_COL, quotes);
		Mockito.verify(this.mongoOperations).insert(Mockito.eq(quotes), Mockito.eq(DAY_COL));
	}

	@Test
	public void findByPairReadsNamedCollectionWithCriteria() {
		Date since = new Date();
		Mockito.when(this.mongoOperations.find(Mockito.any(Query.class), Mockito.eq(QuoteBs.class),
				Mockito.eq(HOUR_COL))).thenReturn(List.of());
		List<QuoteBs> result = this.createRepository()
				.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(HOUR_COL, "btcusd", since, Limit.of(1000));
		Assertions.assertTrue(result.isEmpty());
		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		Mockito.verify(this.mongoOperations).find(queryCaptor.capture(), Mockito.eq(QuoteBs.class),
				Mockito.eq(HOUR_COL));
		Query query = queryCaptor.getValue();
		Assertions.assertEquals(1000, query.getLimit());
		Assertions.assertEquals(1, query.getSortObject().getInteger("createdAt"));
		Document criteria = query.getQueryObject();
		Assertions.assertEquals("btcusd", criteria.getString("pair"));
		Assertions.assertNotNull(((Document) criteria.get("createdAt")).get("$gt"));
	}

	@Test
	public void findByPairWithoutCollectionReadsDefaultCollection() {
		Date since = new Date();
		Mockito.when(this.mongoOperations.find(Mockito.any(Query.class), Mockito.eq(QuoteBs.class)))
				.thenReturn(List.of());
		List<QuoteBs> result = this.createRepository().findByPairAndCreatedAtAfterOrderByCreatedAtAsc("btcusd", since);
		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.mongoOperations).find(Mockito.any(Query.class), Mockito.eq(QuoteBs.class));
	}
}
