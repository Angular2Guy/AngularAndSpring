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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ch.xxx.trader.adapter.cron.PrepareData;
import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.dtos.QuoteIb;
import ch.xxx.trader.usecase.mappers.ReportMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItbitService {
	private final Map<String,String> currpairs = new HashMap<String,String>();
	private final ReactiveMongoOperations operations;
	private final ReportGenerator reportGenerator;
	private final OrderBookClient orderBookClient;
	private final ReportMapper reportMapper;
	
	public ItbitService(ReactiveMongoOperations operations, ReportGenerator reportGenerator, OrderBookClient orderBookClient,ReportMapper reportMapper) {
		this.operations = operations;
		this.reportGenerator = reportGenerator;
		this.orderBookClient = orderBookClient;
		this.reportMapper = reportMapper;
		this.currpairs.put("btcusd", "XBTUSD");
		this.currpairs.put("btceur", "XBTEUR");		
	}
	
	public Mono<String> getOrderbook(String currpair) {		
		final String newCurrpair = currpair.equals("btcusd") ? "XBTUSD" : currpair; 
		return this.orderBookClient.getOrderbookItbit(newCurrpair);
	}

	public Mono<QuoteIb> currentQuote(String pair) {
		final String newPair = this.currpairs.get(pair);
		Query query = MongoUtils.buildCurrentQuery(Optional.of(newPair));
		return this.operations.findOne(query, QuoteIb.class);
	}

	public Flux<QuoteIb> tfQuotes(String timeFrame, String pair) {
		final String newPair = this.currpairs.get(pair);
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(newPair));
			return this.operations.find(query, QuoteIb.class).filter(q -> filterEvenMinutes(q));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(newPair));
			return this.operations.find(query, QuoteIb.class, PrepareData.IB_HOUR_COL);
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(newPair));
			return this.operations.find(query, QuoteIb.class, PrepareData.IB_DAY_COL);
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(newPair));
			return this.operations.find(query, QuoteIb.class, PrepareData.IB_DAY_COL);
		}

		return Flux.empty();
	}		
	
	public Mono<byte[]> pdfReport(String timeFrame, String pair) {
		final String newPair = this.currpairs.get(pair);
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(newPair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteIb.class).filter(this::filter10Minutes).map(this.reportMapper::convert));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(newPair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteIb.class, PrepareData.IB_HOUR_COL).map(this.reportMapper::convert));
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(newPair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteIb.class, PrepareData.IB_DAY_COL).map(this.reportMapper::convert));
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(newPair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteIb.class, PrepareData.IB_DAY_COL).map(this.reportMapper::convert));
		}
		
		return Mono.empty();
	}
	
    private boolean filterEvenMinutes(QuoteIb quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
    
	private boolean filter10Minutes(QuoteIb quote) {
		return MongoUtils.filter10Minutes(quote.getCreatedAt());
	}
}
