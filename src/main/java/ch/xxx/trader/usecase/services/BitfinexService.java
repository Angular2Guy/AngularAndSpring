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

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import ch.xxx.trader.adapter.cron.PrepareData;
import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.dtos.QuoteBf;
import ch.xxx.trader.domain.dtos.QuotePdf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BitfinexService {
	private final ReactiveMongoOperations operations;	
	private final ReportGenerator reportGenerator;
	private final OrderBookClient orderBookClient;
	
	public BitfinexService(ReactiveMongoOperations operations, ReportGenerator reportGenerator, OrderBookClient orderBookClient) {
		this.operations = operations;
		this.reportGenerator = reportGenerator;
		this.orderBookClient = orderBookClient;
	}
	
	public Mono<String> getOrderbook(@PathVariable String currpair, HttpServletRequest request) {		
		return this.orderBookClient.getOrderbookBitfinex(currpair, request);				
	}

	public Mono<QuoteBf> currentQuote(String pair) {
		Query query = MongoUtils.buildCurrentQuery(Optional.of(pair));
		return this.operations.findOne(query, QuoteBf.class);
	}

	public Flux<QuoteBf> tfQuotes(String timeFrame, String pair) {		
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBf.class).filter(q -> filterEvenMinutes(q));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBf.class, PrepareData.BF_HOUR_COL);
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBf.class, PrepareData.BF_DAY_COL);
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBf.class, PrepareData.BF_DAY_COL);
		}

		return Flux.empty();
	}
	
	public Mono<byte[]> pdfReport(String timeFrame, String pair) {
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteBf.class).filter(this::filter10Minutes).map(this::convert));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteBf.class, PrepareData.BF_HOUR_COL).map(this::convert));
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteBf.class, PrepareData.BF_DAY_COL).map(this::convert));
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteBf.class, PrepareData.BF_DAY_COL).map(this::convert));
		}
		
		return Mono.empty();
	}

	private QuotePdf convert(QuoteBf quote) {
		QuotePdf quotePdf = new QuotePdf(quote.getLast_price(), quote.getPair(), quote.getVolume(), quote.getCreatedAt(), quote.getBid(), quote.getAsk());		
		return quotePdf;
	}
	
	private boolean filterEvenMinutes(QuoteBf quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
	
	private boolean filter10Minutes(QuoteBf quote) {
		return MongoUtils.filter10Minutes(quote.getCreatedAt());
	}
}
