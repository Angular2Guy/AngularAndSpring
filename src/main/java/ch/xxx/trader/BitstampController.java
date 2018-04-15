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
package ch.xxx.trader;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Path.CrossParameterNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.JodaTimeConverters.DateTimeToDateConverter;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import ch.xxx.trader.clients.PrepareData;
import ch.xxx.trader.clients.QuoteBs;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bitstamp")
public class BitstampController {
	private static final String URLBS = "https://www.bitstamp.net/api";	

	@Autowired
	private ReactiveMongoOperations operations;

	@GetMapping("/{currpair}/orderbook")
	public Mono<String> getOrderbook(@PathVariable String currpair, HttpServletRequest request) {
		if (!WebUtils.checkOBRequest(request, WebUtils.LASTOBCALLBS)) {
			return Mono.just("{\"timestamp\": \"\", \"bids\": [], \"asks\": [] }");
		}
		WebClient wc = WebUtils.buildWebClient(URLBS);
		return wc.get().uri("/v2/order_book/" + currpair + "/").accept(MediaType.APPLICATION_JSON).exchange()
				.flatMap(res -> res.bodyToMono(String.class));
	}

	@GetMapping
	public Flux<QuoteBs> allQuotes() {
		return this.operations.findAll(QuoteBs.class);
	}

	@GetMapping("/{pair}/current")
	public Mono<QuoteBs> currentQuoteBtc(@PathVariable String pair) {				
			Query query = MongoUtils.buildCurrentQuery(Optional.of(pair));
			return this.operations.findOne(query, QuoteBs.class);
	}
	
	@GetMapping("/{pair}/{timeFrame}")
	public Flux<QuoteBs> tfQuotesBtc(@PathVariable String timeFrame, @PathVariable String pair) {				
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBs.class).filter(q -> filterEvenMinutes(q));
		} else if(MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBs.class, PrepareData.BS_HOUR_COL);
		} else if(MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBs.class, PrepareData.BS_DAY_COL);
		} else if(MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteBs.class, PrepareData.BS_DAY_COL);
		}
		
		return Flux.empty();
	}
	
	@GetMapping("/btceur")
	public Flux<QuoteBs> allQuotesBtc() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("btceur"));
		return this.operations.find(query, QuoteBs.class);
	}

	@GetMapping("/etheur")
	public Flux<QuoteBs> allQuotesEth() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("etheur"));
		return this.operations.find(query, QuoteBs.class);
	}

	@GetMapping("/ltceur")
	public Flux<QuoteBs> allQuotesLtc() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("ltceur"));
		return this.operations.find(query, QuoteBs.class);
	}

	@GetMapping("/xrpeur")
	public Flux<QuoteBs> allQuotesXrp() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("xrpeur"));
		return this.operations.find(query, QuoteBs.class);
	}

	@GetMapping("/btcusd")
	public Flux<QuoteBs> allQuotesBtcUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("btcusd"));
		return this.operations.find(query, QuoteBs.class);
	}

	@GetMapping("/ethusd")
	public Flux<QuoteBs> allQuotesEthUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("ethusd"));
		return this.operations.find(query, QuoteBs.class);
	}

	@GetMapping("/ltcusd")
	public Flux<QuoteBs> allQuotesLtcUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("ltcusd"));
		return this.operations.find(query, QuoteBs.class);
	}

	@GetMapping("/xrpusd")
	public Flux<QuoteBs> allQuotesXrpUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("xrpusd"));
		return this.operations.find(query, QuoteBs.class);
	}

	private boolean filterEvenMinutes(QuoteBs quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}
