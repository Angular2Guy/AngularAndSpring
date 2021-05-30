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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.xxx.trader.adapter.cron.PrepareData;
import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.common.Tuple;
import ch.xxx.trader.domain.dtos.QuoteCb;
import ch.xxx.trader.domain.dtos.QuoteCbSmall;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CoinbaseService {
	private static final Logger log = LoggerFactory.getLogger(PrepareData.class);	
	private static final Map<Integer, MethodHandle> cbMethodCache = new ConcurrentHashMap<>();
	public static final String CB_HOUR_COL = "quoteCbHour";
	public static final String CB_DAY_COL = "quoteCbDay";
	private final MyMongoRepository myMongoRepository;
	private final ServiceUtils serviceUtils;
	
	public CoinbaseService(MyMongoRepository myMongoRepository, ServiceUtils serviceUtils) {
		this.myMongoRepository = myMongoRepository;
		this.serviceUtils = serviceUtils;
	}
	
	public Flux<QuoteCbSmall> todayQuotesBc() {
		Query query = MongoUtils.buildTodayQuery(Optional.empty());
		return this.myMongoRepository.find(query,QuoteCb.class)
				.filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(), quote.getLtc()));
	}
	
	public Flux<QuoteCbSmall> sevenDaysQuotesBc() {
		Query query = MongoUtils.build7DayQuery(Optional.empty());
		return this.myMongoRepository.find(query,QuoteCb.class, CB_HOUR_COL)
				.filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(), quote.getLtc()));
	}
	
	public Flux<QuoteCbSmall> thirtyDaysQuotesBc() {
		Query query = MongoUtils.build30DayQuery(Optional.empty());
		return this.myMongoRepository.find(query,QuoteCb.class, CB_DAY_COL)
				.filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(), quote.getLtc()));
	}
	
	public Flux<QuoteCbSmall> nintyDaysQuotesBc() {
		Query query = MongoUtils.build90DayQuery(Optional.empty());
		return this.myMongoRepository.find(query,QuoteCb.class, CB_DAY_COL)
				.filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(), quote.getLtc()));
	}
	
	public Mono<QuoteCb> currentQuoteBc() {
		Query query = MongoUtils.buildCurrentQuery(Optional.empty());
		return this.myMongoRepository.findOne(query,QuoteCb.class);
	}

	public void createCbHourlyAvg() {
		Tuple<Calendar, Calendar> timeFrame = this.serviceUtils.createTimeFrame(CB_HOUR_COL, QuoteCb.class, true);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Coinbase
			Collection<QuoteCb> collectCb = this.myMongoRepository.find(query, QuoteCb.class).collectList()
					.map(quotes -> makeCbQuoteHour(quotes, begin, end)).block();
			this.myMongoRepository.insertAll(Mono.just(collectCb), CB_HOUR_COL).blockLast();

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Coinbase Hour Data for: " + sdf.format(begin.getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
	}

	public void createCbDailyAvg() {
		Tuple<Calendar, Calendar> timeFrame = this.serviceUtils.createTimeFrame(CB_DAY_COL, QuoteCb.class,false);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Coinbase
			Collection<QuoteCb> collectCb = this.myMongoRepository.find(query, QuoteCb.class).collectList()
					.map(quotes -> makeCbQuoteDay(quotes, begin, end)).block();
			this.myMongoRepository.insertAll(Mono.just(collectCb), CB_DAY_COL).blockLast();

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Coinbase Day Data for: " + sdf.format(begin.getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
	}
	
	private Collection<QuoteCb> makeCbQuoteDay(List<QuoteCb> quotes, Calendar begin, Calendar end) {
		List<QuoteCb> hourQuotes = new LinkedList<QuoteCb>();
		BigDecimal[] params = new BigDecimal[170];
		Class[] types = new Class[170];
		for (int x = 0; x < 170; x++) {
			params[x] = BigDecimal.ZERO;
			types[x] = BigDecimal.class;
		}
		QuoteCb quoteCb = null;
		try {
			quoteCb = QuoteCb.class.getConstructor(types).newInstance((Object[]) params);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		quoteCb.setCreatedAt(begin.getTime());
		long count = quotes.stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).count();

		quoteCb = quotes.stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).reduce(quoteCb, (q1, q2) -> avgCbQuotePeriod(q1, q2, count));

		hourQuotes.add(quoteCb);
		return hourQuotes;
	}

	private Collection<QuoteCb> makeCbQuoteHour(List<QuoteCb> quotes, Calendar begin, Calendar end) {
		List<Calendar> hours = this.serviceUtils.createDayHours(begin);
		List<QuoteCb> hourQuotes = new LinkedList<QuoteCb>();
		for (int i = 0; i < 24; i++) {
			BigDecimal[] params = new BigDecimal[170];
			Class[] types = new Class[170];
			for (int x = 0; x < 170; x++) {
				params[x] = BigDecimal.ZERO;
				types[x] = BigDecimal.class;
			}
			QuoteCb quoteCb = null;
			try {
				quoteCb = QuoteCb.class.getConstructor(types).newInstance((Object[]) params);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
			quoteCb.setCreatedAt(hours.get(i).getTime());
			final int x = i;
			long count = quotes.stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).count();

			quoteCb = quotes.stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).reduce(quoteCb, (q1, q2) -> avgCbQuotePeriod(q1, q2, count));

			hourQuotes.add(quoteCb);
		}
		return hourQuotes;
	}

	private QuoteCb avgCbQuotePeriod(QuoteCb q1, QuoteCb q2, long count) {
		Class[] types = new Class[170];
		for (int i = 0; i < 170; i++) {
			types[i] = BigDecimal.class;

		}
		QuoteCb result = null;
		try {
			BigDecimal[] bds = new BigDecimal[170];
			IntStream.range(0, QuoteCb.class.getConstructor(types).getParameterAnnotations().length)// .parallel()
					.forEach(x -> {
						try {
							MethodHandle mh = cbMethodCache.get(Integer.valueOf(x));
							if (mh == null) {
								JsonProperty annotation = (JsonProperty) QuoteCb.class.getConstructor(types)
										.getParameterAnnotations()[x][0];
								String fieldName = annotation.value();
								String firstLetter = fieldName.substring(0, 1);
								String methodName = "get" + firstLetter.toUpperCase()
										+ fieldName.substring(1).toLowerCase();
								if ("getTry".equals(methodName)) {
									methodName = methodName + "1";
								}
								MethodType desc = MethodType.methodType(BigDecimal.class);
								mh = MethodHandles.lookup().findVirtual(QuoteCb.class, methodName, desc);
								cbMethodCache.put(Integer.valueOf(x), mh);
							}
							BigDecimal num1 = (BigDecimal) mh.invokeExact(q1);
							BigDecimal num2 = (BigDecimal) mh.invokeExact(q2);
							bds[x] = this.serviceUtils.avgHourValue(num1, num2, count);
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}
					});
			result = QuoteCb.class.getConstructor(types).newInstance((Object[]) bds);
			result.setCreatedAt(q1.getCreatedAt());
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private boolean filterEvenMinutes(QuoteCb quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}
