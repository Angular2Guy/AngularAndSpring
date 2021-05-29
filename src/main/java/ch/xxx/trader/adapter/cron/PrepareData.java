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
package ch.xxx.trader.adapter.cron;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.xxx.trader.domain.common.Tuple;
import ch.xxx.trader.domain.dtos.Quote;
import ch.xxx.trader.domain.dtos.QuoteBf;
import ch.xxx.trader.domain.dtos.QuoteBs;
import ch.xxx.trader.domain.dtos.QuoteCb;
import ch.xxx.trader.domain.dtos.QuoteIb;
import reactor.core.publisher.Mono;

@Component
public class PrepareData {
	private static final Logger log = LoggerFactory.getLogger(PrepareData.class);	
	private static final Map<Integer, MethodHandle> cbMethodCache = new ConcurrentHashMap<>();
	public static final String BS_HOUR_COL = "quoteBsHour";
	public static final String BS_DAY_COL = "quoteBsDay";
	public static final String BF_HOUR_COL = "quoteBfHour";
	public static final String BF_DAY_COL = "quoteBfDay";
	public static final String IB_HOUR_COL = "quoteIbHour";
	public static final String IB_DAY_COL = "quoteIbDay";
	public static final String CB_HOUR_COL = "quoteCbHour";
	public static final String CB_DAY_COL = "quoteCbDay";

	@Autowired
	private ReactiveMongoOperations operations;

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 5 0 ? * ?")
	public void createBsHourlyAvg() {
		Tuple<Calendar, Calendar> timeFrame = createTimeFrame(BS_HOUR_COL, QuoteBs.class, true);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Bitstamp
			List<Collection<QuoteBs>> collectBs = this.operations.find(query, QuoteBs.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeBsQuoteHour(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectBs.forEach(col -> this.operations.insertAll(Mono.just(col), BS_HOUR_COL).blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Bitstamp Hour Data for: " + sdf.format(begin.getTime()));
		}
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 5 1 ? * ?")
	public void createBsDailyAvg() {
		Tuple<Calendar, Calendar> timeFrame = createTimeFrame(BS_DAY_COL, QuoteBs.class, false);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();		
		while (end.before(now)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Bitstamp
			List<Collection<QuoteBs>> collectBs = this.operations.find(query, QuoteBs.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeBsQuoteDay(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectBs.forEach(col -> this.operations.insertAll(Mono.just(col), BS_DAY_COL).blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Bitstamp Day Data for: " + sdf.format(begin.getTime()));
		}
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 10 0 ? * ?")
	public void createBfHourlyAvg() {
		Tuple<Calendar, Calendar> timeFrame = createTimeFrame(BF_HOUR_COL, QuoteBf.class, true);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Bitfinex
			List<Collection<QuoteBf>> collectBf = this.operations.find(query, QuoteBf.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeBfQuoteHour(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectBf.forEach(col -> this.operations.insertAll(Mono.just(col), BF_HOUR_COL).blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Bitfinex Hour Data for: " + sdf.format(begin.getTime()));
		}
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 10 1 ? * ?")
	public void createBfDailyAvg() {
		Tuple<Calendar, Calendar> timeFrame = createTimeFrame(BF_DAY_COL, QuoteBf.class, false);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Bitfinex
			List<Collection<QuoteBf>> collectBf = this.operations.find(query, QuoteBf.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeBfQuoteDay(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectBf.forEach(col -> this.operations.insertAll(Mono.just(col), BF_DAY_COL).blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Bitfinex Day Data for: " + sdf.format(begin.getTime()));
		}
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 15 0 ? * ?")
	public void createIbHourlyAvg() {
		Tuple<Calendar, Calendar> timeFrame = createTimeFrame(IB_HOUR_COL, QuoteIb.class, true);

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

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 15 1 ? * ?")
	public void createIbDailyAvg() {
		Tuple<Calendar, Calendar> timeFrame = createTimeFrame(IB_DAY_COL, QuoteIb.class,false);

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

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 20 0 ? * ?")
	public void createCbHourlyAvg() {
		Tuple<Calendar, Calendar> timeFrame = createTimeFrame(CB_HOUR_COL, QuoteCb.class, true);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Coinbase
			Collection<QuoteCb> collectCb = this.operations.find(query, QuoteCb.class).collectList()
					.map(quotes -> makeCbQuoteHour(quotes, begin, end)).block();
			this.operations.insertAll(Mono.just(collectCb), CB_HOUR_COL).blockLast();

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Coinbase Hour Data for: " + sdf.format(begin.getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
	}

//	@Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 20 1 ? * ?")
	public void createCbDailyAvg() {
		Tuple<Calendar, Calendar> timeFrame = createTimeFrame(CB_DAY_COL, QuoteCb.class,false);

		Calendar begin = timeFrame.getX();
		Calendar end = timeFrame.getY();

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Coinbase
			Collection<QuoteCb> collectCb = this.operations.find(query, QuoteCb.class).collectList()
					.map(quotes -> makeCbQuoteDay(quotes, begin, end)).block();
			this.operations.insertAll(Mono.just(collectCb), CB_DAY_COL).blockLast();

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
		List<Calendar> hours = createDayHours(begin);
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
							MethodHandle mh = PrepareData.cbMethodCache.get(Integer.valueOf(x));
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
								PrepareData.cbMethodCache.put(Integer.valueOf(x), mh);
							}
							BigDecimal num1 = (BigDecimal) mh.invokeExact(q1);
							BigDecimal num2 = (BigDecimal) mh.invokeExact(q2);
							bds[x] = avgHourValue(num1, num2, count);
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

	private Collection<QuoteIb> makeIbQuoteHour(String key, Map<String, Collection<QuoteIb>> multimap, Calendar begin,
			Calendar end) {
		List<Calendar> hours = createDayHours(begin);
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

	private List<Calendar> createDayHours(Calendar begin) {
		List<Calendar> hours = new LinkedList<Calendar>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(begin.getTime());
		while (hours.size() <= 24) {
			Calendar myCal = Calendar.getInstance();
			myCal.setTime(cal.getTime());
			hours.add(myCal);
			cal.add(Calendar.HOUR_OF_DAY, 1);
		}
		return hours;
	}

	private Collection<QuoteBf> makeBfQuoteHour(String key, Map<String, Collection<QuoteBf>> multimap, Calendar begin,
			Calendar end) {
		List<Calendar> hours = createDayHours(begin);
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
			QuoteBf hourQuote = multimap.get(key).stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).reduce(quoteBf, (q1, q2) -> avgBfQuote(q1, q2, count));
			hourQuote.setPair(key);
			hourQuotes.add(hourQuote);
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
		QuoteBf hourQuote = multimap.get(key).stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).reduce(quoteBf, (q1, q2) -> avgBfQuote(q1, q2, count));
		hourQuote.setPair(key);
		hourQuotes.add(hourQuote);

		return hourQuotes;
	}

	private Collection<QuoteBs> makeBsQuoteDay(String key, Map<String, Collection<QuoteBs>> multimap, Calendar begin,
			Calendar end) {
		List<QuoteBs> hourQuotes = new LinkedList<QuoteBs>();

		QuoteBs quoteBs = new QuoteBs(BigDecimal.ZERO, BigDecimal.ZERO, begin.getTime(), BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		quoteBs.setCreatedAt(begin.getTime());
		long count = multimap.get(key).stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).count();
		QuoteBs hourQuote = multimap.get(key).stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).reduce(quoteBs, (q1, q2) -> avgBsQuote(q1, q2, count));
		hourQuote.setPair(key);
		hourQuotes.add(hourQuote);

		return hourQuotes;
	}

	private Collection<QuoteBs> makeBsQuoteHour(String key, Map<String, Collection<QuoteBs>> multimap, Calendar begin,
			Calendar end) {
		List<Calendar> hours = createDayHours(begin);
		List<QuoteBs> hourQuotes = new LinkedList<QuoteBs>();
		for (int i = 0; i < 24; i++) {
			QuoteBs quoteBs = new QuoteBs(BigDecimal.ZERO, BigDecimal.ZERO, hours.get(i).getTime(), BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
			quoteBs.setCreatedAt(hours.get(i).getTime());
			final int x = i;
			long count = multimap.get(key).stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).count();
			QuoteBs hourQuote = multimap.get(key).stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).reduce(quoteBs, (q1, q2) -> avgBsQuote(q1, q2, count));
			hourQuote.setPair(key);
			hourQuotes.add(hourQuote);
		}
		return hourQuotes;
	}

	private QuoteIb avgIbQuote(QuoteIb q1, QuoteIb q2, long count) {
		QuoteIb myQuote = new QuoteIb(q1.getPair(), avgHourValue(q1.getBid(), q2.getBid(), count),
				avgHourValue(q1.getBidAmt(), q2.getBidAmt(), count), avgHourValue(q1.getAsk(), q2.getAsk(), count),
				avgHourValue(q1.getAskAmt(), q2.getAskAmt(), count),
				avgHourValue(q1.getLastPrice(), q2.getLastPrice(), count),
				avgHourValue(q1.getStAmt(), q2.getStAmt(), count),
				avgHourValue(q1.getVolume24h(), q2.getVolume24h(), count),
				avgHourValue(q1.getVolumeToday(), q2.getVolumeToday(), count),
				avgHourValue(q1.getHigh24h(), q2.getHigh24h(), count),
				avgHourValue(q1.getLow24h(), q2.getLow24h(), count),
				avgHourValue(q1.getOpenToday(), q2.getOpenToday(), count),
				avgHourValue(q1.getVwapToday(), q2.getVwapToday(), count),
				avgHourValue(q1.getVwap24h(), q2.getVwap24h(), count), q1.getServerTimeUTC());
		myQuote.setCreatedAt(q1.getCreatedAt());
		return myQuote;
	}

	private QuoteBs avgBsQuote(QuoteBs q1, QuoteBs q2, long count) {
		QuoteBs myQuote = new QuoteBs(avgHourValue(q1.getHigh(), q2.getHigh(), count),
				avgHourValue(q1.getLast(), q2.getLast(), count), q1.getTimestamp(),
				avgHourValue(q1.getBid(), q2.getBid(), count), avgHourValue(q1.getVwap(), q2.getVwap(), count),
				avgHourValue(q1.getVolume(), q2.getVolume(), count), avgHourValue(q1.getLow(), q2.getLow(), count),
				avgHourValue(q1.getAsk(), q2.getAsk(), count), avgHourValue(q1.getOpen(), q2.getOpen(), count));
		myQuote.setCreatedAt(q1.getCreatedAt());
		return myQuote;
	}

	private QuoteBf avgBfQuote(QuoteBf q1, QuoteBf q2, long count) {
		QuoteBf myQuote = new QuoteBf(avgHourValue(q1.getMid(), q2.getMid(), count),
				avgHourValue(q1.getBid(), q2.getBid(), count), avgHourValue(q1.getAsk(), q2.getAsk(), count),
				avgHourValue(q1.getLast_price(), q2.getLast_price(), count),
				avgHourValue(q1.getLow(), q2.getLow(), count), avgHourValue(q1.getHigh(), q2.getHigh(), count),
				avgHourValue(q1.getVolume(), q2.getVolume(), count), "");
		myQuote.setCreatedAt(q1.getCreatedAt());
		return myQuote;
	}

	private BigDecimal avgHourValue(BigDecimal v1, BigDecimal v2, long count) {
		return v1.add(v2 == null ? BigDecimal.ZERO
				: v2.divide(BigDecimal.valueOf(count == 0 ? 1 : count), 10, RoundingMode.HALF_UP));
	}

	private Tuple<Calendar, Calendar> createTimeFrame(String colName, Class<? extends Quote> colType, boolean hour) {
		if (!this.operations.collectionExists(colName).block()) {
			this.operations.createCollection(colName).block();
		}
		Query query = new Query();
		query.with(Sort.by("createdAt").ascending());
		Quote firstQuote = this.operations.findOne(query, colType).block();
		query = new Query();
		query.with(Sort.by("createdAt").descending());
		Quote lastHourQuote = this.operations.findOne(query, colType, colName).block();
		Calendar globalBeginn = Calendar.getInstance();
		if (lastHourQuote == null) {
			globalBeginn.setTime(firstQuote.getCreatedAt());
		} else {
			globalBeginn.setTime(lastHourQuote.getCreatedAt());
			if(hour) {
				globalBeginn.add(Calendar.HOUR_OF_DAY, 1);
			} else {
				globalBeginn.add(Calendar.DAY_OF_YEAR, 1);
			}
		}

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(globalBeginn.getTime());
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		end.setTime(begin.getTime());
		end.add(Calendar.DAY_OF_YEAR, 1);
		return new Tuple<Calendar, Calendar>(begin, end);
	}
}
