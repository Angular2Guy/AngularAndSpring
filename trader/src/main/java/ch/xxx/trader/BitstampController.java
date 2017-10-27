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
import javax.validation.Path.CrossParameterNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.JodaTimeConverters.DateTimeToDateConverter;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.trader.clients.QuoteBs;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bitstamp")
public class BitstampController {
	@Autowired
	private ReactiveMongoOperations operations;	
	
	@GetMapping
	public Flux<QuoteBs> allQuotes() {
		return this.operations.findAll(QuoteBs.class);
	}
	
	@GetMapping("/btceur/today")
	public Flux<QuoteBs> todayQuotesBtc() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("btceur"));
		return this.operations.find(query,QuoteBs.class)
				.filter(q -> filterEvenMinutes(q)); 
	}
	
	@GetMapping("/etheur/today")
	public Flux<QuoteBs> todayQuotesEth() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("etheur"));
		return this.operations.find(query,QuoteBs.class)
				.filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/ltceur/today")
	public Flux<QuoteBs> todayQuotesLtc() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("ltceur"));
		return this.operations.find(query,QuoteBs.class)
				.filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/xrpeur/today")
	public Flux<QuoteBs> todayQuotesXrp() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("xrpeur"));
		return this.operations.find(query,QuoteBs.class)
				.filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/btcusd/today")
	public Flux<QuoteBs> todayQuotesBtcUsd() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("btcusd"));
		return this.operations.find(query,QuoteBs.class)
				.filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/ethusd/today")
	public Flux<QuoteBs> todayQuotesEthUsd() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("ethusd"));
		return this.operations.find(query,QuoteBs.class)
				.filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/ltcusd/today")
	public Flux<QuoteBs> todayQuotesLtcUsd() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("ltcusd"));
		return this.operations.find(query,QuoteBs.class)
				.filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/xrpusd/today")
	public Flux<QuoteBs> todayQuotesXrpUsd() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("xrpusd"));
		return this.operations.find(query,QuoteBs.class)
				.filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/btceur/current")
	public Mono<QuoteBs> currentQuoteBtc() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("btceur"));
		return this.operations.findOne(query,QuoteBs.class);
	}
	
	@GetMapping("/etheur/current")
	public Mono<QuoteBs> currentQuoteEth() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("etheur"));
		return this.operations.findOne(query,QuoteBs.class);
	}
	
	@GetMapping("/ltceur/current")
	public Mono<QuoteBs> currentQuoteLtc() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("ltceur"));
		return this.operations.findOne(query,QuoteBs.class);
	}

	@GetMapping("/xrpeur/current")
	public Mono<QuoteBs> currentQuoteXrp() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("xrpeur"));
		return this.operations.findOne(query,QuoteBs.class);
	}
	
	@GetMapping("/btcusd/current")
	public Mono<QuoteBs> currentQuoteBtcUsd() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("btcusd"));
		return this.operations.findOne(query,QuoteBs.class);
	}
	
	@GetMapping("/ethusd/current")
	public Mono<QuoteBs> currentQuoteEthUsd() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("ethusd"));
		return this.operations.findOne(query,QuoteBs.class);
	}
	
	@GetMapping("/ltcusd/current")
	public Mono<QuoteBs> currentQuoteLtcUsd() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("ltcusd"));
		return this.operations.findOne(query,QuoteBs.class);
	}

	@GetMapping("/xrpusd/current")
	public Mono<QuoteBs> currentQuoteXrpUsd() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("xrpusd"));
		return this.operations.findOne(query,QuoteBs.class);
	}
	
	@GetMapping("/btceur")
	public Flux<QuoteBs> allQuotesBtc() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("btceur"));
		return this.operations.find(query,QuoteBs.class);
	}
		
	@GetMapping("/etheur")
	public Flux<QuoteBs> allQuotesEth() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("etheur"));
		return this.operations.find(query,QuoteBs.class);
	}
	
	@GetMapping("/ltceur")
	public Flux<QuoteBs> allQuotesLtc() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("ltceur"));
		return this.operations.find(query,QuoteBs.class);
	}

	@GetMapping("/xrpeur")
	public Flux<QuoteBs> allQuotesXrp() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("xrpeur"));
		return this.operations.find(query,QuoteBs.class);
	}
	
	@GetMapping("/btcusd")
	public Flux<QuoteBs> allQuotesBtcUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("btcusd"));
		return this.operations.find(query,QuoteBs.class);
	}
		
	@GetMapping("/ethusd")
	public Flux<QuoteBs> allQuotesEthUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("ethusd"));
		return this.operations.find(query,QuoteBs.class);
	}
	
	@GetMapping("/ltcusd")
	public Flux<QuoteBs> allQuotesLtcUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("ltcusd"));
		return this.operations.find(query,QuoteBs.class);
	}

	@GetMapping("/xrpusd")
	public Flux<QuoteBs> allQuotesXrpUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("xrpusd"));
		return this.operations.find(query,QuoteBs.class);
	}
	
	private boolean filterEvenMinutes(QuoteBs quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}
