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

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import ch.xxx.trader.data.PrepareData;
import dtos.QuoteBf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bitfinex")
public class BitfinexController {
	private static final String URLBF = "https://api.bitfinex.com";

	@Autowired
	private ReactiveMongoOperations operations;

	@GetMapping("/{currpair}/orderbook")
	public Mono<String> getOrderbook(@PathVariable String currpair, HttpServletRequest request) {
		if (!WebUtils.checkOBRequest(request, WebUtils.LASTOBCALLBF)) {
			return Mono.just("{\n" + "  \"bids\":[],\n" + "  \"asks\":[]\n" + "}");
		}
		WebClient wc = WebUtils.buildWebClient(URLBF);
		return wc.get().uri("/v1/book/" + currpair + "/").accept(MediaType.APPLICATION_JSON).exchange()
				.flatMap(res -> res.bodyToMono(String.class));
	}

	@GetMapping
	public Flux<QuoteBf> allQuotes() {
		return this.operations.findAll(QuoteBf.class);
	}

	@GetMapping("/{pair}/current")
	public Mono<QuoteBf> currentQuote(@PathVariable String pair) {
		Query query = MongoUtils.buildCurrentQuery(Optional.of(pair));
		return this.operations.findOne(query, QuoteBf.class);
	}

	@GetMapping("/{pair}/{timeFrame}")
	public Flux<QuoteBf> tfQuotes(@PathVariable String timeFrame, @PathVariable String pair) {
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

	@GetMapping("/btcusd")
	public Flux<QuoteBf> allQuotesBtcUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("btcusd"));
		return this.operations.find(query, QuoteBf.class);
	}

	@GetMapping("/ethusd")
	public Flux<QuoteBf> allQuotesEthUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("ethusd"));
		return this.operations.find(query, QuoteBf.class);
	}

	@GetMapping("/ltcusd")
	public Flux<QuoteBf> allQuotesLtcUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("ltcusd"));
		return this.operations.find(query, QuoteBf.class);
	}

	@GetMapping("/xrpusd")
	public Flux<QuoteBf> allQuotesXrpUsd() {
		Query query = new Query();
		query.addCriteria(Criteria.where("pair").is("xrpusd"));
		return this.operations.find(query, QuoteBf.class);
	}

	private boolean filterEvenMinutes(QuoteBf quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}
