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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ch.xxx.trader.clients.QuoteCb;
import ch.xxx.trader.clients.QuoteIb;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itbit")
public class ItbitController {
	
	@Autowired
	private ReactiveMongoOperations operations;
	
	@GetMapping
	public Flux<QuoteIb> allQuotes() {
		return this.operations.findAll(QuoteIb.class);
	}
	
	@GetMapping("/btceur/today")
	public Flux<QuoteIb> todayQuotes() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("XBTEUR"));
		return this.operations.find(query, QuoteIb.class).filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/btceur/current")
	public Mono<QuoteIb> currentQuote() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("XBTEUR"));
		return this.operations.findOne(query, QuoteIb.class);
	}
	
	@GetMapping("/btcusd/today")
	public Flux<QuoteIb> todayQuotesUsd() {
		Query query = MongoUtils.buildTodayQuery(Optional.of("XBTUSD"));
		return this.operations.find(query, QuoteIb.class).filter(q -> filterEvenMinutes(q));
	}
	
	@GetMapping("/btcusd/current")
	public Mono<QuoteIb> currentQuoteUsd() {
		Query query = MongoUtils.buildCurrentQuery(Optional.of("XBTUSD"));
		return this.operations.findOne(query, QuoteIb.class);
	}
	
    private boolean filterEvenMinutes(QuoteIb quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}
