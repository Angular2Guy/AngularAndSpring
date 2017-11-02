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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.trader.clients.QuoteCb;
import ch.xxx.trader.clients.QuoteCbSmall;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/coinbase")
public class CoinbaseController {
	
	@Autowired
	private ReactiveMongoOperations operations;
	
	@GetMapping
	public Flux<QuoteCb> allQuotes() {
		return this.operations.findAll(QuoteCb.class);
	}
	
	@GetMapping("/today")
	public Flux<QuoteCbSmall> todayQuotesBc() {
		Query query = MongoUtils.buildTodayQuery(Optional.empty());
		return this.operations.find(query,QuoteCb.class)
				.filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(), quote.getLtc()));
	}
	
	@GetMapping("/current")
	public Mono<QuoteCb> currentQuoteBc() {
		Query query = MongoUtils.buildCurrentQuery(Optional.empty());
		return this.operations.findOne(query,QuoteCb.class);
	}
	
	private boolean filterEvenMinutes(QuoteCb quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}
