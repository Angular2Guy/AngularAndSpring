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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.common.Tuple;
import ch.xxx.trader.domain.model.QuoteBf;
import ch.xxx.trader.usecase.mappers.ReportMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BitfinexService {
	private static final Logger log = LoggerFactory.getLogger(BitfinexService.class);
	public static final String BF_HOUR_COL = "quoteBfHour";
	public static final String BF_DAY_COL = "quoteBfDay";
	private final ReportGenerator reportGenerator;
	private final MyOrderBookClient orderBookClient;
	private final ReportMapper reportMapper;
	private final MyMongoRepository myMongoRepository;
	private final ServiceUtils serviceUtils;

	public BitfinexService(ReportGenerator reportGenerator, ServiceUtils serviceUtils, MyOrderBookClient orderBookClient,
			ReportMapper reportMapper, MyMongoRepository myMongoRepository) {
		this.reportGenerator = reportGenerator;
		this.orderBookClient = orderBookClient;
		this.reportMapper = reportMapper;
		this.myMongoRepository = myMongoRepository;
		this.serviceUtils = serviceUtils;
	}

	public Mono<String> getOrderbook(String currpair) {
		return this.orderBookClient.getOrderbookBitfinex(currpair);
	}

	public Mono<QuoteBf> currentQuote(String pair) {
		Query query = MongoUtils.buildCurrentQuery(Optional.of(pair));
		return this.myMongoRepository.findOne(query, QuoteBf.class);
	}

	public Flux<QuoteBf> tfQuotes(String timeFrame, String pair) {
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.myMongoRepository.find(query, QuoteBf.class).filter(q -> filterEvenMinutes(q));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.myMongoRepository.find(query, QuoteBf.class, BF_HOUR_COL);
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.myMongoRepository.find(query, QuoteBf.class, BF_DAY_COL);
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.myMongoRepository.find(query, QuoteBf.class, BF_DAY_COL);
		}

		return Flux.empty();
	}

	public Mono<byte[]> pdfReport(String timeFrame, String pair) {
		if (MongoUtils.TimeFrame.TODAY.getValue().equals(timeFrame)) {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(this.myMongoRepository.find(query, QuoteBf.class)
					.filter(this::filter10Minutes).map(this.reportMapper::convert));
		} else if (MongoUtils.TimeFrame.SEVENDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(
					this.myMongoRepository.find(query, QuoteBf.class, BF_HOUR_COL).map(this.reportMapper::convert));
		} else if (MongoUtils.TimeFrame.THIRTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(
					this.myMongoRepository.find(query, QuoteBf.class, BF_DAY_COL).map(this.reportMapper::convert));
		} else if (MongoUtils.TimeFrame.NINTYDAYS.getValue().equals(timeFrame)) {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			return this.reportGenerator.generateReport(
					this.myMongoRepository.find(query, QuoteBf.class, BF_DAY_COL).map(this.reportMapper::convert));
		}
		return Mono.empty();
	}

	public void createBfHourlyAvg() {
		Tuple<Calendar, Calendar> timeFrame = this.serviceUtils.createTimeFrame(BF_HOUR_COL, QuoteBf.class, true);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Bitfinex
			List<Collection<QuoteBf>> collectBf = this.myMongoRepository.find(query, QuoteBf.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeBfQuoteHour(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectBf.forEach(col -> this.myMongoRepository.insertAll(Mono.just(col), BF_HOUR_COL).blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Bitfinex Hour Data for: " + sdf.format(begin.getTime()));
		}
	}

	public void createBfDailyAvg() {
		Tuple<Calendar, Calendar> timeFrame = this.serviceUtils.createTimeFrame(BF_DAY_COL, QuoteBf.class, false);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Bitfinex
			List<Collection<QuoteBf>> collectBf = this.myMongoRepository.find(query, QuoteBf.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeBfQuoteDay(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectBf.forEach(col -> this.myMongoRepository.insertAll(Mono.just(col), BF_DAY_COL).blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Bitfinex Day Data for: " + sdf.format(begin.getTime()));
		}
	}

	private boolean filterEvenMinutes(QuoteBf quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}

	private boolean filter10Minutes(QuoteBf quote) {
		return MongoUtils.filter10Minutes(quote.getCreatedAt());
	}

	private Collection<QuoteBf> makeBfQuoteHour(String key, Map<String, Collection<QuoteBf>> multimap, Calendar begin,
			Calendar end) {
		List<Calendar> hours = this.serviceUtils.createDayHours(begin);
		List<QuoteBf> hourQuotes = new LinkedList<QuoteBf>();
		for (int i = 0; i < 24; i++) {
			QuoteBf quoteBf = new QuoteBf(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "");
			quoteBf.setCreatedAt(hours.get(i).getTime());
			final int x = i;
			long count = multimap.get(key).stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).count();
			if (count > 2) {
				QuoteBf hourQuote = multimap.get(key).stream().filter(quote -> {
					return quote.getCreatedAt().after(hours.get(x).getTime())
							&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
				}).reduce(quoteBf, (q1, q2) -> avgBfQuote(q1, q2, count));
				hourQuote.setPair(key);
				hourQuotes.add(hourQuote);
			}
		}
		return hourQuotes;
	}

	private Collection<QuoteBf> makeBfQuoteDay(String key, Map<String, Collection<QuoteBf>> multimap, Calendar begin,
			Calendar end) {
		List<QuoteBf> hourQuotes = new LinkedList<QuoteBf>();

		QuoteBf quoteBf = new QuoteBf(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "");
		quoteBf.setCreatedAt(begin.getTime());
		long count = multimap.get(key).stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).count();
		if (count > 2) {
			QuoteBf hourQuote = multimap.get(key).stream().filter(quote -> {
				return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
			}).reduce(quoteBf, (q1, q2) -> avgBfQuote(q1, q2, count));
			hourQuote.setPair(key);
			hourQuotes.add(hourQuote);
		}
		return hourQuotes;
	}

	private QuoteBf avgBfQuote(QuoteBf q1, QuoteBf q2, long count) {
		QuoteBf myQuote = new QuoteBf(this.serviceUtils.avgHourValue(q1.getMid(), q2.getMid(), count),
				this.serviceUtils.avgHourValue(q1.getBid(), q2.getBid(), count),
				this.serviceUtils.avgHourValue(q1.getAsk(), q2.getAsk(), count),
				this.serviceUtils.avgHourValue(q1.getLast_price(), q2.getLast_price(), count),
				this.serviceUtils.avgHourValue(q1.getLow(), q2.getLow(), count),
				this.serviceUtils.avgHourValue(q1.getHigh(), q2.getHigh(), count),
				this.serviceUtils.avgHourValue(q1.getVolume(), q2.getVolume(), count), "");
		myQuote.setCreatedAt(q1.getCreatedAt());
		return myQuote;
	}
}
