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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import ch.xxx.trader.clients.PrepareData;
import dtos.QuoteIb;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itbit")
public class ItbitController {
	private static final String URLIB = "https://api.itbit.com";
	private final Map<String,String> currpairs = new HashMap<String,String>();
	
	@Autowired
	private ReactiveMongoOperations operations;
	
	public ItbitController() {
		this.currpairs.put("btcusd", "XBTUSD");
		this.currpairs.put("btceur", "XBTEUR");
	}
	
	@GetMapping("/{currpair}/orderbook")
	public Mono<String> getOrderbook(@PathVariable String currpair, HttpServletRequest request) {
		if(!WebUtils.checkOBRequest(request, WebUtils.LASTOBCALLIB)) {
			return Mono.just("{\"bids\": [], \"asks\": [] }");
		}
		currpair = currpair.equals("btcusd") ? "XBTUSD" : currpair; 
		WebClient wc = WebUtils.buildWebClient(URLIB);		
		return wc.get().uri("/v1/markets/"+currpair+"/order_book/").accept(MediaType.APPLICATION_JSON).exchange().flatMap(res -> res.bodyToMono(String.class));
	}
	
	@GetMapping
	public Flux<QuoteIb> allQuotes() {
		return this.operations.findAll(QuoteIb.class);
	}
	
	@GetMapping("/{pair}/current")
	public Mono<QuoteIb> currentQuote(@PathVariable String pair) {
		pair = this.currpairs.get(pair);
		Query query = MongoUtils.buildCurrentQuery(Optional.of(pair));
		return this.operations.findOne(query, QuoteIb.class);
	}

	@GetMapping("/{pair}/{timeFrame}")
	public Flux<QuoteIb> tfQuotes(@PathVariable String timeFrame, @PathVariable String pair) {
		pair = this.currpairs.get(pair);
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteIb.class).filter(q -> filterEvenMinutes(q));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteIb.class, PrepareData.IB_HOUR_COL);
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteIb.class, PrepareData.IB_DAY_COL);
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.operations.find(query, QuoteIb.class, PrepareData.IB_DAY_COL);
		}

		return Flux.empty();
	}		
	
    private boolean filterEvenMinutes(QuoteIb quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}
