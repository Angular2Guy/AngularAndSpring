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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.common.Tuple;
import ch.xxx.trader.domain.dtos.QuoteIb;
import ch.xxx.trader.usecase.mappers.ReportMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItbitService {
	private static final Logger log = LoggerFactory.getLogger(ItbitService.class);
	public static final String IB_HOUR_COL = "quoteIbHour";
	public static final String IB_DAY_COL = "quoteIbDay";
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
			return this.operations.find(query, QuoteIb.class, IB_HOUR_COL);
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(newPair));
			return this.operations.find(query, QuoteIb.class, IB_DAY_COL);
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(newPair));
			return this.operations.find(query, QuoteIb.class, IB_DAY_COL);
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
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteIb.class, IB_HOUR_COL).map(this.reportMapper::convert));
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(newPair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteIb.class, IB_DAY_COL).map(this.reportMapper::convert));
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(newPair));
			return this.reportGenerator.generateReport(this.operations.find(query, QuoteIb.class, IB_DAY_COL).map(this.reportMapper::convert));
		}
		
		return Mono.empty();
	}
	
	public void createIbHourlyAvg() {
		Tuple<Calendar, Calendar> timeFrame = ServiceUtils.createTimeFrame(this.operations, IB_HOUR_COL, QuoteIb.class, true);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Itbit
			List<Collection<QuoteIb>> collectIb = this.operations.find(query, QuoteIb.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeIbQuoteHour(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectIb.forEach(col -> this.operations.insertAll(Mono.just(col), IB_HOUR_COL).blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Itbit Hour Data for: " + sdf.format(begin.getTime()));
		}
	}

	public void createIbDailyAvg() {
		Tuple<Calendar, Calendar> timeFrame = ServiceUtils.createTimeFrame(this.operations, IB_DAY_COL, QuoteIb.class,false);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Itbit
			List<Collection<QuoteIb>> collectIb = this.operations.find(query, QuoteIb.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeIbQuoteDay(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectIb.forEach(col -> this.operations.insertAll(Mono.just(col), IB_DAY_COL).blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Itbit Day Data for: " + sdf.format(begin.getTime()));
		}
	}
	
    private boolean filterEvenMinutes(QuoteIb quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
    
	private boolean filter10Minutes(QuoteIb quote) {
		return MongoUtils.filter10Minutes(quote.getCreatedAt());
	}
	
	private Collection<QuoteIb> makeIbQuoteHour(String key, Map<String, Collection<QuoteIb>> multimap, Calendar begin,
			Calendar end) {
		List<Calendar> hours = ServiceUtils.createDayHours(begin);
		List<QuoteIb> hourQuotes = new LinkedList<QuoteIb>();
		for (int i = 0; i < 24; i++) {
			QuoteIb quoteIb = new QuoteIb(key, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new Date());
			quoteIb.setCreatedAt(hours.get(i).getTime());
			final int x = i;
			long count = multimap.get(key).stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).count();
			QuoteIb hourQuote = multimap.get(key).stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).reduce(quoteIb, (q1, q2) -> avgIbQuote(q1, q2, count));
			// hourQuote.setPair(key);
			hourQuotes.add(hourQuote);
		}
		return hourQuotes;
	}

	private Collection<QuoteIb> makeIbQuoteDay(String key, Map<String, Collection<QuoteIb>> multimap, Calendar begin,
			Calendar end) {
		List<QuoteIb> hourQuotes = new LinkedList<QuoteIb>();
		QuoteIb quoteIb = new QuoteIb(key, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new Date());
		quoteIb.setCreatedAt(begin.getTime());
		long count = multimap.get(key).stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).count();
		QuoteIb hourQuote = multimap.get(key).stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).reduce(quoteIb, (q1, q2) -> avgIbQuote(q1, q2, count));
		// hourQuote.setPair(key);
		hourQuotes.add(hourQuote);
		return hourQuotes;
	}
	
	private QuoteIb avgIbQuote(QuoteIb q1, QuoteIb q2, long count) {
		QuoteIb myQuote = new QuoteIb(q1.getPair(), ServiceUtils.avgHourValue(q1.getBid(), q2.getBid(), count),
				ServiceUtils.avgHourValue(q1.getBidAmt(), q2.getBidAmt(), count), ServiceUtils.avgHourValue(q1.getAsk(), q2.getAsk(), count),
				ServiceUtils.avgHourValue(q1.getAskAmt(), q2.getAskAmt(), count),
				ServiceUtils.avgHourValue(q1.getLastPrice(), q2.getLastPrice(), count),
				ServiceUtils.avgHourValue(q1.getStAmt(), q2.getStAmt(), count),
				ServiceUtils.avgHourValue(q1.getVolume24h(), q2.getVolume24h(), count),
				ServiceUtils.avgHourValue(q1.getVolumeToday(), q2.getVolumeToday(), count),
				ServiceUtils.avgHourValue(q1.getHigh24h(), q2.getHigh24h(), count),
				ServiceUtils.avgHourValue(q1.getLow24h(), q2.getLow24h(), count),
				ServiceUtils.avgHourValue(q1.getOpenToday(), q2.getOpenToday(), count),
				ServiceUtils.avgHourValue(q1.getVwapToday(), q2.getVwapToday(), count),
				ServiceUtils.avgHourValue(q1.getVwap24h(), q2.getVwap24h(), count), q1.getServerTimeUTC());
		myQuote.setCreatedAt(q1.getCreatedAt());
		return myQuote;
	}	
}
