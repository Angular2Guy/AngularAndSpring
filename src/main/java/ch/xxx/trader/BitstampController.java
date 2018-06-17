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

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import ch.xxx.trader.data.PrepareData;
import ch.xxx.trader.dtos.QuoteBs;
import ch.xxx.trader.dtos.QuotePdf;
import ch.xxx.trader.jwt.JwtTokenProvider;
import ch.xxx.trader.reports.ReportGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bitstamp")
public class BitstampController {
	private static final String URLBS = "https://www.bitstamp.net/api";	

	@Autowired
	private ReactiveMongoOperations operations;
	
	@Autowired 
	private ReportGenerator reportGenerator;
	
//	@PreAuthorize("hasRole('USERS')")
	@GetMapping("/{currpair}/orderbook")
	public Mono<String> getOrderbook(@PathVariable String currpair, HttpServletRequest request) {
		if (!WebUtils.checkOBRequest(request, WebUtils.LASTOBCALLBS)) {
			return Mono.just("{\"timestamp\": \"\", \"bids\": [], \"asks\": [] }");
		}		
//		if(!WebUtils.checkToken(request, jwtTokenProvider)) {
//			return Mono.just("{\"timestamp\": \"\", \"bids\": [], \"asks\": [] }");
//		}
		WebClient wc = WebUtils.buildWebClient(URLBS);
		return wc.get().uri("/v2/order_book/" + currpair + "/").accept(MediaType.APPLICATION_JSON).exchange()
				.flatMap(res -> res.bodyToMono(String.class));
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
	
	@GetMapping(path="/{pair}/{timeFrame}/pdf", produces=MediaType.APPLICATION_PDF_VALUE)
	public Mono<byte[]> pdfReport(@PathVariable String timeFrame, @PathVariable String pair) {		
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteBs.class).filter(this::filter10Minutes).map(this::convert));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteBs.class, PrepareData.BS_HOUR_COL).map(this::convert));
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteBs.class, PrepareData.BS_DAY_COL).map(this::convert));
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteBs.class, PrepareData.BS_DAY_COL).map(this::convert));
		}
		
		return Mono.empty();
	}

	private QuotePdf convert(QuoteBs quote) {		
		QuotePdf quotePdf = new QuotePdf(quote.getLast(), quote.getPair(), quote.getVolume(), quote.getCreatedAt(), quote.getBid(), quote.getAsk());		
		return quotePdf;
	}
	
	private boolean filterEvenMinutes(QuoteBs quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
	
	private boolean filter10Minutes(QuoteBs quote) {
		return MongoUtils.filter10Minutes(quote.getCreatedAt());
	}	
}
