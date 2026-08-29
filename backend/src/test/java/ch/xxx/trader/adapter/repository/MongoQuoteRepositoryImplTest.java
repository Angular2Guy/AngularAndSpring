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

import ch.xxx.trader.domain.model.entity.*;
import ch.xxx.trader.domain.services.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class MongoQuoteRepositoryImplTest {
	private static final String PAIR = "btcusd";

	@Mock
	private MongoOperations operations;
	@Mock
	private QuoteBfRepository quoteBfRepository;
	@Mock
	private QuoteHourBfRepository quoteHourBfRepository;
	@Mock
	private QuoteDayBfRepository quoteDayBfRepository;
	@Mock
	private QuoteBsRepository quoteBsRepository;
	@Mock
	private QuoteHourBsRepository quoteHourBsRepository;
	@Mock
	private QuoteDayBsRepository quoteDayBsRepository;

	private MongoQuoteRepositoryImpl repository;

	@BeforeEach
	void setUp() {
		this.repository = new MongoQuoteRepositoryImpl(this.operations, this.quoteBfRepository,
				this.quoteHourBfRepository, this.quoteDayBfRepository, this.quoteBsRepository,
				this.quoteHourBsRepository, this.quoteDayBsRepository);
	}

	@Test
	void tfQuotesTodayWithQuoteBs() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 4);
		Date evenMinute1 = cal.getTime();
		cal.set(Calendar.MINUTE, 6);
		Date evenMinute2 = cal.getTime();
		QuoteBs q1 = createQuoteBs(evenMinute1);
		QuoteBs q2 = createQuoteBs(evenMinute2);
		Mockito.when(this.quoteBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR), any(Date.class)))
				.thenReturn(List.of(q1, q2));

		List<QuoteBs> result = this.repository.tfQuotes("today", PAIR, QuoteBs.class);

		Assertions.assertEquals(2, result.size());
		Mockito.verify(this.quoteBsRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class));
	}

	@Test
	void tfQuotesTodayFiltersOddMinutes() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 3);
		Date oddMinute = cal.getTime();
		cal.set(Calendar.MINUTE, 4);
		Date evenMinute = cal.getTime();
		QuoteBs oddQuote = createQuoteBs(oddMinute);
		QuoteBs evenQuote = createQuoteBs(evenMinute);
		Mockito.when(this.quoteBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR), any(Date.class)))
				.thenReturn(List.of(oddQuote, evenQuote));

		List<QuoteBs> result = this.repository.tfQuotes("today", PAIR, QuoteBs.class);

		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(evenMinute, result.get(0).getCreatedAt());
	}

	@Test
	void tfQuotesSevenDaysWithQuoteBs() {
		Mockito.when(this.quoteBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteBs> result = this.repository.tfQuotes("7days", PAIR, QuoteBs.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteBsRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesThirtyDaysWithQuoteBs() {
		Mockito.when(this.quoteBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteBs> result = this.repository.tfQuotes("30days", PAIR, QuoteBs.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteBsRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesNintyDaysWithQuoteBs() {
		Mockito.when(this.quoteBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteBs> result = this.repository.tfQuotes("90days", PAIR, QuoteBs.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteBsRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesSixMonthWithQuoteBs() {
		Mockito.when(this.quoteBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteBs> result = this.repository.tfQuotes("6month", PAIR, QuoteBs.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteBsRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesOneYearWithQuoteBs() {
		Mockito.when(this.quoteBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteBs> result = this.repository.tfQuotes("1year", PAIR, QuoteBs.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteBsRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesSevenDaysWithQuoteHourBs() {
		Mockito.when(this.quoteHourBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteHourBs> result = this.repository.tfQuotes("7days", PAIR, QuoteHourBs.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteHourBsRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesSevenDaysWithQuoteDayBs() {
		Mockito.when(this.quoteDayBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteDayBs> result = this.repository.tfQuotes("7days", PAIR, QuoteDayBs.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteDayBsRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesSevenDaysWithQuoteBf() {
		Mockito.when(this.quoteBfRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteBf> result = this.repository.tfQuotes("7days", PAIR, QuoteBf.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteBfRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesSevenDaysWithQuoteHourBf() {
		Mockito.when(this.quoteHourBfRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteHourBf> result = this.repository.tfQuotes("7days", PAIR, QuoteHourBf.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteHourBfRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesSevenDaysWithQuoteDayBf() {
		Mockito.when(this.quoteDayBfRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)))).thenReturn(List.of());

		List<QuoteDayBf> result = this.repository.tfQuotes("7days", PAIR, QuoteDayBf.class);

		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteDayBfRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(eq(PAIR),
				any(Date.class), eq(Limit.of(1000)));
	}

	@Test
	void tfQuotesUnsupportedTimeFrameReturnsEmpty() {
		List<QuoteBs> result = this.repository.tfQuotes("1month", PAIR, QuoteBs.class);

		Assertions.assertTrue(result.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	void ensureIndexCreatesIndex() {
		IndexOperations indexOps = Mockito.mock(IndexOperations.class);
		Mockito.when(this.operations.indexOps(QuoteBs.class)).thenReturn(indexOps);

		this.repository.ensureIndex(QuoteBs.class);

		Mockito.verify(this.operations).indexOps(QuoteBs.class);
		Mockito.verify(indexOps).createIndex(Mockito.any(Index.class));
	}

	@Test
	void createTimeFrameWithNoAggregateAndNoFirstQuote() {
		QuoteBsRepository bsRepository = Mockito.mock(QuoteBsRepository.class);
		QuoteDayBsRepository aggregateRepository = Mockito.mock(QuoteDayBsRepository.class);
		Mockito.when(aggregateRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
		Mockito.when(bsRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.empty());
		Mockito.when(this.operations.collectionExists(QuoteDayBs.class)).thenReturn(true);

		MyTimeFrame result = this.repository.createTimeFrame(QuoteBs.class, QuoteDayBs.class, false,
				bsRepository, aggregateRepository);

		Assertions.assertNotNull(result.begin());
		Assertions.assertNotNull(result.end());
		Assertions.assertTrue(result.end().after(result.begin()));
	}

	@Test
	void createTimeFrameWithNoAggregateAndFirstQuote() {
		QuoteBsRepository bsRepository = Mockito.mock(QuoteBsRepository.class);
		QuoteDayBsRepository aggregateRepository = Mockito.mock(QuoteDayBsRepository.class);
		Mockito.when(aggregateRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
		Date firstDate = new Date();
		QuoteBs firstQuote = createQuoteBs(firstDate);
		Mockito.when(bsRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.of(firstQuote));
		Mockito.when(this.operations.collectionExists(QuoteDayBs.class)).thenReturn(true);

		MyTimeFrame result = this.repository.createTimeFrame(QuoteBs.class, QuoteDayBs.class, false,
				bsRepository, aggregateRepository);

		Assertions.assertNotNull(result.begin());
		Assertions.assertNotNull(result.end());
	}

	@Test
	void createTimeFrameWithAggregateHourTrue() {
		QuoteBsRepository bsRepository = Mockito.mock(QuoteBsRepository.class);
		QuoteDayBsRepository aggregateRepository = Mockito.mock(QuoteDayBsRepository.class);
		Date aggregateDate = new Date();
		QuoteDayBs aggregateQuote = createQuoteDayBs(aggregateDate);
		Mockito.when(aggregateRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.of(aggregateQuote));
		Mockito.when(this.operations.collectionExists(QuoteDayBs.class)).thenReturn(true);

		MyTimeFrame result = this.repository.createTimeFrame(QuoteBs.class, QuoteDayBs.class, true,
				bsRepository, aggregateRepository);

		Assertions.assertNotNull(result.begin());
		Assertions.assertNotNull(result.end());
		Calendar expectedBegin = Calendar.getInstance();
		expectedBegin.setTime(aggregateDate);
		expectedBegin.add(Calendar.HOUR_OF_DAY, 1);
		expectedBegin.set(Calendar.MINUTE, 0);
		expectedBegin.set(Calendar.SECOND, 0);
		Assertions.assertEquals(expectedBegin, result.begin());
	}

	@Test
	void createTimeFrameWithAggregateHourFalse() {
		QuoteBsRepository bsRepository = Mockito.mock(QuoteBsRepository.class);
		QuoteDayBsRepository aggregateRepository = Mockito.mock(QuoteDayBsRepository.class);
		Date aggregateDate = new Date();
		QuoteDayBs aggregateQuote = createQuoteDayBs(aggregateDate);
		Mockito.when(aggregateRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.of(aggregateQuote));
		Mockito.when(this.operations.collectionExists(QuoteDayBs.class)).thenReturn(true);

		MyTimeFrame result = this.repository.createTimeFrame(QuoteBs.class, QuoteDayBs.class, false,
				bsRepository, aggregateRepository);

		Assertions.assertNotNull(result.begin());
		Assertions.assertNotNull(result.end());
		Calendar expectedBegin = Calendar.getInstance();
		expectedBegin.setTime(aggregateDate);
		expectedBegin.add(Calendar.DAY_OF_YEAR, 1);
		expectedBegin.set(Calendar.MINUTE, 0);
		expectedBegin.set(Calendar.SECOND, 0);
		Assertions.assertEquals(expectedBegin, result.begin());
	}

	@Test
	void createTimeFrameCreatesCollectionIfNotExists() {
		QuoteBsRepository bsRepository = Mockito.mock(QuoteBsRepository.class);
		QuoteDayBsRepository aggregateRepository = Mockito.mock(QuoteDayBsRepository.class);
		Mockito.when(aggregateRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.empty());
		Mockito.when(bsRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.empty());
		Mockito.when(this.operations.collectionExists(QuoteDayBs.class)).thenReturn(false);

		this.repository.createTimeFrame(QuoteBs.class, QuoteDayBs.class, false,
				bsRepository, aggregateRepository);

		Mockito.verify(this.operations).createCollection(QuoteDayBs.class);
	}

	private QuoteBs createQuoteBs(Date createdAt) {
		QuoteBs q = new QuoteBs(BigDecimal.TEN, BigDecimal.TEN, new Date(), BigDecimal.TEN, BigDecimal.TEN,
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN);
		q.setCreatedAt(createdAt);
		q.setPair(PAIR);
		return q;
	}

	private QuoteDayBs createQuoteDayBs(Date createdAt) {
		QuoteDayBs q = new QuoteDayBs(BigDecimal.TEN, BigDecimal.TEN, new Date(), BigDecimal.TEN, BigDecimal.TEN,
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN);
		q.setCreatedAt(createdAt);
		q.setPair(PAIR);
		return q;
	}
}
