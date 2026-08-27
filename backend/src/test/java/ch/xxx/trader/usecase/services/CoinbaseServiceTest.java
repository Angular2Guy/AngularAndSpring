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

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.xxx.trader.domain.services.QuoteCbRepository;

@ExtendWith(MockitoExtension.class)
public class CoinbaseServiceTest {
	@Mock
	private QuoteCbRepository quoteCbRepository;

	@Mock
	private ServiceUtils serviceUtils;

	@Test
	public void sevenDaysQuotesReadHourCollection() {
		Mockito.when(this.quoteCbRepository.findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any())).thenReturn(List.of());
		CoinbaseService service = new CoinbaseService(this.quoteCbRepository, this.serviceUtils);
		Assertions.assertTrue(service.sevenDaysQuotesBc().isEmpty());
		Mockito.verify(this.quoteCbRepository).findByCreatedAtAfterOrderByCreatedAtAsc(
				Mockito.any(), Mockito.any());
	}

	@Test
	public void thirtyDaysQuotesReadDayCollection() {
		Mockito.when(this.quoteCbRepository.findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any())).thenReturn(List.of());
		CoinbaseService service = new CoinbaseService(this.quoteCbRepository, this.serviceUtils);
		Assertions.assertTrue(service.thirtyDaysQuotesBc().isEmpty());
		Mockito.verify(this.quoteCbRepository).findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any());
	}

	@Test
	public void nintyDaysQuotesReadDayCollection() {
		Mockito.when(this.quoteCbRepository.findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any())).thenReturn(List.of());
		CoinbaseService service = new CoinbaseService(this.quoteCbRepository, this.serviceUtils);
		Assertions.assertTrue(service.nintyDaysQuotesBc().isEmpty());
		Mockito.verify(this.quoteCbRepository).findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any());
	}

	@Test
	public void sixMonthsQuotesReadDayCollection() {
		Mockito.when(this.quoteCbRepository.findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any())).thenReturn(List.of());
		CoinbaseService service = new CoinbaseService(this.quoteCbRepository, this.serviceUtils);
		Assertions.assertTrue(service.sixMonthsQuotesBc().isEmpty());
		Mockito.verify(this.quoteCbRepository).findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any());
	}

	@Test
	public void oneYearQuotesReadDayCollection() {
		Mockito.when(this.quoteCbRepository.findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any())).thenReturn(List.of());
		CoinbaseService service = new CoinbaseService(this.quoteCbRepository, this.serviceUtils);
		Assertions.assertTrue(service.oneYearQuotesBc().isEmpty());
		Mockito.verify(this.quoteCbRepository).findByCreatedAtAfterOrderByCreatedAtAsc(
				 Mockito.any(), Mockito.any());
	}
}
