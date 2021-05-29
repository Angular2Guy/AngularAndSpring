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

import ch.xxx.trader.adapter.cron.PrepareData;
import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.dtos.QuoteBs;
import ch.xxx.trader.domain.dtos.QuotePdf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BitstampService {
	private final ReactiveMongoOperations operations;
	private final OrderBookClient orderBookClient;
	private final ReportGenerator reportGenerator;

	public BitstampService(ReactiveMongoOperations operations, OrderBookClient orderBookClient,
			ReportGenerator reportGenerator) {
		this.operations = operations;
		this.orderBookClient = orderBookClient;
		this.reportGenerator = reportGenerator;
	}

	public Mono<String> getOrderbook(String currpair, HttpServletRequest request) {
		return this.orderBookClient.getOrderbookBitstamp(currpair, request);
	}

	public Mono<QuoteBs> currentQuoteBtc(String pair) {
		Query query = MongoUtils.buildCurrentQuery(Optional.of(pair));
		return this.operations.findOne(query, QuoteBs.class);
	}

	public Flux<QuoteBs> tfQuotesBtc(String timeFrame, String pair) {
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBs.class).filter(q -> filterEvenMinutes(q));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBs.class, PrepareData.BS_HOUR_COL);
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBs.class, PrepareData.BS_DAY_COL);
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBs.class, PrepareData.BS_DAY_COL);
		}

		return Flux.empty();
	}

	public Mono<byte[]> pdfReport(String timeFrame, String pair) {
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(
					this.operations.find(query, QuoteBs.class).filter(this::filter10Minutes).map(this::convert));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(
					this.operations.find(query, QuoteBs.class, PrepareData.BS_HOUR_COL).map(this::convert));
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(
					this.operations.find(query, QuoteBs.class, PrepareData.BS_DAY_COL).map(this::convert));
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(
					this.operations.find(query, QuoteBs.class, PrepareData.BS_DAY_COL).map(this::convert));
		}

		return Mono.empty();
	}

	private QuotePdf convert(QuoteBs quote) {
		QuotePdf quotePdf = new QuotePdf(quote.getLast(), quote.getPair(), quote.getVolume(), quote.getCreatedAt(),
				quote.getBid(), quote.getAsk());
		return quotePdf;
	}

	private boolean filterEvenMinutes(QuoteBs quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}

	private boolean filter10Minutes(QuoteBs quote) {
		return MongoUtils.filter10Minutes(quote.getCreatedAt());
	}
}
