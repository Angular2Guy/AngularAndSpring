package ch.xxx.trader.clients;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

import reactor.core.publisher.Mono;

@Component
public class PrepareData {
	private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private final Map<Integer, Method> cbMethodCache = new HashMap<Integer, Method>();

	@Autowired
	private ReactiveMongoOperations operations;

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 0 0 ? * ?")
	public void createBsHourlyAvg() {
		if (!this.operations.collectionExists("quoteBsHour").block()) {
			this.operations.createCollection("quoteBsHour").block();
		}
		Query query = new Query();
		query.with(Sort.by("createdAt").ascending());
		QuoteBs firstQuote = this.operations.findOne(query, QuoteBs.class).block();
		query = new Query();
		query.with(Sort.by("createdAt").descending());
		QuoteBs lastHourQuote = this.operations.findOne(query, QuoteBs.class, "quoteBsHour").block();
		Calendar globalBeginn = Calendar.getInstance();
		if (lastHourQuote == null) {
			globalBeginn.setTime(firstQuote.getCreatedAt());
		} else {
			globalBeginn.setTime(lastHourQuote.getCreatedAt());
		}

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(globalBeginn.getTime());
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		end.setTime(begin.getTime());
		end.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Bitstamp
			List<Collection<QuoteBs>> collectBs = this.operations.find(query, QuoteBs.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeBsQuoteHour(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectBs.forEach(col -> this.operations.insertAll(Mono.just(col), "quoteBsHour").blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Bitstamp Data for: " + sdf.format(begin.getTime()));
		}
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "5 0 0 ? * ?")
	public void createBfHourlyAvg() {
		if (!this.operations.collectionExists("quoteBfHour").block()) {
			this.operations.createCollection("quoteBfHour").block();
		}
		Query query = new Query();
		query.with(Sort.by("createdAt").ascending());
		QuoteBs firstQuote = this.operations.findOne(query, QuoteBs.class).block();
		query = new Query();
		query.with(Sort.by("createdAt").descending());
		QuoteBs lastHourQuote = this.operations.findOne(query, QuoteBs.class, "quoteBfHour").block();
		Calendar globalBeginn = Calendar.getInstance();
		if (lastHourQuote == null) {
			globalBeginn.setTime(firstQuote.getCreatedAt());
		} else {
			globalBeginn.setTime(lastHourQuote.getCreatedAt());
		}

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(globalBeginn.getTime());
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		end.setTime(begin.getTime());
		end.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Bitfinex
			List<Collection<QuoteBf>> collectBf = this.operations.find(query, QuoteBf.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeBfQuoteHour(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectBf.forEach(col -> this.operations.insertAll(Mono.just(col), "quoteBfHour").blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Bitfinex Data for: " + sdf.format(begin.getTime()));
		}
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "10 0 0 ? * ?")
	public void createIbHourlyAvg() {
		if (!this.operations.collectionExists("quoteIbHour").block()) {
			this.operations.createCollection("quoteIbHour").block();
		}
		Query query = new Query();
		query.with(Sort.by("createdAt").ascending());
		QuoteBs firstQuote = this.operations.findOne(query, QuoteBs.class).block();
		query = new Query();
		query.with(Sort.by("createdAt").descending());
		QuoteBs lastHourQuote = this.operations.findOne(query, QuoteBs.class, "quoteIbHour").block();
		Calendar globalBeginn = Calendar.getInstance();
		if (lastHourQuote == null) {
			globalBeginn.setTime(firstQuote.getCreatedAt());
		} else {
			globalBeginn.setTime(lastHourQuote.getCreatedAt());
		}

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(globalBeginn.getTime());
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		end.setTime(begin.getTime());
		end.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Itbit
			List<Collection<QuoteIb>> collectIb = this.operations.find(query, QuoteIb.class)
					.collectMultimap(quote -> quote.getPair(), quote -> quote)
					.map(multimap -> multimap.keySet().stream().map(key -> makeIbQuoteHour(key, multimap, begin, end))
							.collect(Collectors.toList()))
					.block();
			collectIb.forEach(col -> this.operations.insertAll(Mono.just(col), "quoteIbHour").blockLast());

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Itbit Data for: " + sdf.format(begin.getTime()));
		}
	}

	@Scheduled(fixedRate = 300000000, initialDelay = 3000)
	// @Scheduled(cron = "20 0 0 ? * ?")
	public void createCbHourlyAvg() {
		if (!this.operations.collectionExists("quoteCbHour").block()) {
			this.operations.createCollection("quoteCbHour").block();
		}
		Query query = new Query();
		query.with(Sort.by("createdAt").ascending());
		QuoteBs firstQuote = this.operations.findOne(query, QuoteBs.class).block();
		query = new Query();
		query.with(Sort.by("createdAt").descending());
		QuoteBs lastHourQuote = this.operations.findOne(query, QuoteBs.class, "quoteCbHour").block();
		Calendar globalBeginn = Calendar.getInstance();
		if (lastHourQuote == null) {
			globalBeginn.setTime(firstQuote.getCreatedAt());
		} else {
			globalBeginn.setTime(lastHourQuote.getCreatedAt());
		}

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(globalBeginn.getTime());
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		end.setTime(begin.getTime());
		end.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		while (end.before(now)) {
			Date start = new Date();
			query = new Query();
			query.addCriteria(Criteria.where("createdAt").gt(begin.getTime()).lt(end.getTime()));
			// Coinbase
			Collection<QuoteCb> collectCb = this.operations.find(query, QuoteCb.class).collectList()
					.map(quotes -> makeCbQuoteHour(quotes, begin, end)).block();
			this.operations.insertAll(Mono.just(collectCb), "quoteCbHour").blockLast();

			begin.add(Calendar.DAY_OF_YEAR, 1);
			end.add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Coinbase Data for: " + sdf.format(begin.getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
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
				quoteCb = QuoteCb.class.getConstructor(types).newInstance(params);
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
			}).reduce(quoteCb, (q1, q2) -> avgCbQuoteHour(q1, q2, count));

			hourQuotes.add(quoteCb);
		}
		return hourQuotes;
	}

	private QuoteCb avgCbQuoteHour(QuoteCb q1, QuoteCb q2, long count) {
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
							Method method = this.cbMethodCache.get(Integer.valueOf(x));
							if (method == null) {
								JsonProperty annotation = (JsonProperty) QuoteCb.class.getConstructor(types)
										.getParameterAnnotations()[x][0];
								String fieldName = annotation.value();
								String firstLetter = fieldName.substring(0, 1);
								String methodName = "get" + firstLetter.toUpperCase()
										+ fieldName.substring(1).toLowerCase();
								if ("getTry".equals(methodName)) {
									methodName = methodName + "1";
								}
								method = QuoteCb.class.getMethod(methodName);
								this.cbMethodCache.put(Integer.valueOf(x), method);
							}
							BigDecimal num1 = (BigDecimal) method.invoke(q1);
							BigDecimal num2 = (BigDecimal) method.invoke(q2);
							bds[x] = avgHourValue(num1, num2, count);
						} catch (NoSuchMethodException | SecurityException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					});
			result = QuoteCb.class.getConstructor(types).newInstance(bds);
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
			}).reduce(quoteIb, (q1, q2) -> avgIbQuoteHour(q1, q2, count));
			// hourQuote.setPair(key);
			hourQuotes.add(hourQuote);
		}
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
			}).reduce(quoteBf, (q1, q2) -> avgBfQuoteHour(q1, q2, count));
			hourQuote.setPair(key);
			hourQuotes.add(hourQuote);
		}
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
			}).reduce(quoteBs, (q1, q2) -> avgBsQuoteHour(q1, q2, count));
			hourQuote.setPair(key);
			hourQuotes.add(hourQuote);
		}
		return hourQuotes;
	}

	private QuoteIb avgIbQuoteHour(QuoteIb q1, QuoteIb q2, long count) {
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

	private QuoteBs avgBsQuoteHour(QuoteBs q1, QuoteBs q2, long count) {
		QuoteBs myQuote = new QuoteBs(avgHourValue(q1.getHigh(), q2.getHigh(), count),
				avgHourValue(q1.getLast(), q2.getLast(), count), q1.getTimestamp(),
				avgHourValue(q1.getBid(), q2.getBid(), count), avgHourValue(q1.getVwap(), q2.getVwap(), count),
				avgHourValue(q1.getVolume(), q2.getVolume(), count), avgHourValue(q1.getLow(), q2.getLow(), count),
				avgHourValue(q1.getAsk(), q2.getAsk(), count), avgHourValue(q1.getOpen(), q2.getOpen(), count));
		myQuote.setCreatedAt(q1.getCreatedAt());
		return myQuote;
	}

	private QuoteBf avgBfQuoteHour(QuoteBf q1, QuoteBf q2, long count) {
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
}
