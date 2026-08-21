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
package ch.xxx.trader.usecase.common;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.xxx.trader.domain.model.entity.QuoteBs;
import ch.xxx.trader.domain.services.QuoteRepository;

@ExtendWith(MockitoExtension.class)
public class DtoUtilsTest {
	private static final String PAIR = "btcusd";
	private static final String HOUR_COL = "quoteBsHour";
	private static final String DAY_COL = "quoteBsDay";

	@Mock
	private QuoteRepository<QuoteBs> quoteRepository;

	@Test
	public void tfQuotesTodayReadsDefaultCollection() {
		Mockito.when(this.quoteRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(PAIR),
				Mockito.any())).thenReturn(List.of());
		List<QuoteBs> result = DtoUtils.tfQuotes("today", PAIR, this.quoteRepository, HOUR_COL, DAY_COL);
		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(PAIR),
				Mockito.any());
	}

	@Test
	public void tfQuotesSevenDaysReadsHourCollection() {
		Mockito.when(this.quoteRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(HOUR_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any())).thenReturn(List.of());
		List<QuoteBs> result = DtoUtils.tfQuotes("7days", PAIR, this.quoteRepository, HOUR_COL, DAY_COL);
		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(HOUR_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any());
	}

	@Test
	public void tfQuotesThirtyDaysReadsDayCollection() {
		Mockito.when(this.quoteRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(DAY_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any())).thenReturn(List.of());
		List<QuoteBs> result = DtoUtils.tfQuotes("30days", PAIR, this.quoteRepository, HOUR_COL, DAY_COL);
		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(DAY_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any());
	}

	@Test
	public void tfQuotesNintyDaysReadsDayCollection() {
		Mockito.when(this.quoteRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(DAY_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any())).thenReturn(List.of());
		List<QuoteBs> result = DtoUtils.tfQuotes("90days", PAIR, this.quoteRepository, HOUR_COL, DAY_COL);
		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(DAY_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any());
	}

	@Test
	public void tfQuotesSixMonthsReadsDayCollection() {
		Mockito.when(this.quoteRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(DAY_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any())).thenReturn(List.of());
		List<QuoteBs> result = DtoUtils.tfQuotes("6month", PAIR, this.quoteRepository, HOUR_COL, DAY_COL);
		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(DAY_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any());
	}

	@Test
	public void tfQuotesOneYearReadsDayCollection() {
		Mockito.when(this.quoteRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(DAY_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any())).thenReturn(List.of());
		List<QuoteBs> result = DtoUtils.tfQuotes("1year", PAIR, this.quoteRepository, HOUR_COL, DAY_COL);
		Assertions.assertTrue(result.isEmpty());
		Mockito.verify(this.quoteRepository).findByPairAndCreatedAtAfterOrderByCreatedAtAsc(Mockito.eq(DAY_COL),
				Mockito.eq(PAIR), Mockito.any(), Mockito.any());
	}
}
