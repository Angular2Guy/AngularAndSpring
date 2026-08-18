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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.common.MongoUtils.TimeFrame;
import ch.xxx.trader.domain.model.entity.QuoteCb;
import ch.xxx.trader.domain.model.entity.QuoteCbSmall;
import ch.xxx.trader.domain.services.MyTimeFrame;
import ch.xxx.trader.domain.services.QuoteCbRepository;
import ch.xxx.trader.usecase.common.DtoUtils;

@Service
public class CoinbaseService {
	private static final Logger LOG = LoggerFactory.getLogger(CoinbaseService.class);
	private static final Map<String, GetSetMethodFunctions> cbFunctionCache = new ConcurrentHashMap<>();

	private record GetSetMethodFunctions(Function<QuoteCb, BigDecimal> getter, BiConsumer<QuoteCb, BigDecimal> setter,
			String propertyName, PropertyDescriptor propertyDescriptor) {
	}

	public static final String CB_HOUR_COL = "quoteCbHour";
	public static final String CB_DAY_COL = "quoteCbDay";
	public static volatile boolean singleInstanceLock = false;
	private final QuoteCbRepository quoteRepository;
	private final ServiceUtils serviceUtils;
	@Value("${kubernetes.pod.cpu.constraint}")
	private boolean cpuConstraint;
	private final List<String> nonValueFieldNames = List.of("_id", "createdAt", "class");
	private final List<PropertyDescriptor> propertyDescriptors;
	@Value("${single.instance.deployment:false}")
	private boolean singleInstanceDeployment;
	@Value("${single.instance.slow-io:false}")
	private boolean slowIo;

	public CoinbaseService(QuoteCbRepository quoteRepository, ServiceUtils serviceUtils) {
		this.quoteRepository = quoteRepository;
		this.serviceUtils = serviceUtils;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(QuoteCb.class);
			this.propertyDescriptors = Stream.of(beanInfo.getPropertyDescriptors())
					.filter(myDescriptor -> !this.nonValueFieldNames.contains(myDescriptor.getName())).toList();
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	public QuoteCb insertQuote(QuoteCb quote) {
		return this.quoteRepository.insert(quote);
	}

	public List<QuoteCbSmall> todayQuotesBc() {
		return this.quoteRepository.findByCreatedAtAfterOrderByCreatedAtAsc(MongoUtils.buildStartDate(TimeFrame.TODAY))
				.stream().filter(CoinbaseService::filterEvenMinutes)
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()))
				.toList();
	}

	public List<QuoteCbSmall> sevenDaysQuotesBc() {
		return this.quoteRepository.findQuotesSince(CB_HOUR_COL, MongoUtils.buildStartDate(TimeFrame.SEVENDAYS)).stream()
				.filter(CoinbaseService::filterEvenMinutes)
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()))
				.toList();
	}

	public List<QuoteCbSmall> thirtyDaysQuotesBc() {
		return this.quoteRepository.findQuotesSince(CB_DAY_COL, MongoUtils.buildStartDate(TimeFrame.THIRTYDAYS)).stream()
				.filter(CoinbaseService::filterEvenMinutes)
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()))
				.toList();
	}

	public List<QuoteCbSmall> nintyDaysQuotesBc() {
		return this.quoteRepository.findQuotesSince(CB_DAY_COL, MongoUtils.buildStartDate(TimeFrame.NINTYDAYS)).stream()
				.filter(CoinbaseService::filterEvenMinutes)
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()))
				.toList();
	}

	public List<QuoteCbSmall> sixMonthsQuotesBc() {
		return this.quoteRepository.findQuotesSince(CB_DAY_COL, MongoUtils.buildStartDate(TimeFrame.Month6)).stream()
				.filter(CoinbaseService::filterEvenMinutes)
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()))
				.toList();
	}

	public List<QuoteCbSmall> oneYearQuotesBc() {
		return this.quoteRepository.findQuotesSince(CB_DAY_COL, MongoUtils.buildStartDate(TimeFrame.Year1)).stream()
				.filter(CoinbaseService::filterEvenMinutes)
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()))
				.toList();
	}

	public Optional<QuoteCb> currentQuoteBc() {
		return this.quoteRepository.findFirstByCreatedAtAfterOrderByCreatedAtDesc(MongoUtils.buildStartDate(TimeFrame.CURRENT));
	}

	public void createCbAvg() {
		if ((this.singleInstanceDeployment && !CoinbaseService.singleInstanceLock) || !this.singleInstanceDeployment) {
			CoinbaseService.singleInstanceLock = true;
			try {
				this.ensureIndexes();
				this.createHourDayAvg();
			} catch (Exception e) {
				LOG.info("createCbAvg() failed.", e);
			}
		}
	}

	private void ensureIndexes() {
		try {
			this.quoteRepository.ensureIndex(CB_HOUR_COL);
		} catch (Exception e) {
			LOG.info("ensureIndex(" + CB_HOUR_COL + ") failed.", e);
		}
		try {
			this.quoteRepository.ensureIndex(CB_DAY_COL);
		} catch (Exception e) {
			LOG.info("ensureIndex(" + CB_DAY_COL + ") failed.", e);
		}
	}

	private String createHourDayAvg() {
		LOG.info("createHourDayAvg()");
		LocalDateTime start = LocalDateTime.now();
		LOG.info("CpuConstraint property: " + this.cpuConstraint);
		if (this.cpuConstraint) {
			this.createCbIntervalAvg(false);
			this.createCbIntervalAvg(true);
			LOG.info(this.serviceUtils.createAvgLogStatement(start, "Prepared Coinbase Data Time:"));
		} else {
			// This can only be used on machines without cpu constraints.
			final CompletableFuture<String> future7 = CompletableFuture.supplyAsync(() -> {
				this.createCbIntervalAvg(false);
				return "createCbHourlyAvg() Done.";
			}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
			final CompletableFuture<String> future8 = CompletableFuture.supplyAsync(() -> {
				this.createCbIntervalAvg(true);
				return "createCbDailyAvg() Done.";
			}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
			String combined = Stream.of(future7, future8).map(CompletableFuture::join).collect(Collectors.joining(" "));
			LOG.info(combined);
		}
		return "done.";
	}

	private void processTimeFrame(MyTimeFrame timeFrame1, boolean isDay) {
		Date start = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		final var nonZeroProperties = new AtomicInteger(0);
		// Coinbase
		final var logFailed = String.format("Coinbase prepare %s data failed", isDay ? "day" : "hour");
		try {
			List<QuoteCb> quotes;
			try {
				quotes = this.quoteRepository
						.findByCreatedAtGreaterThanAndCreatedAtLessThan(timeFrame1.begin().getTime(),
								timeFrame1.end().getTime())
						.stream().toList();
			} catch (Exception e) {
				LOG.warn(logFailed, e);
				quotes = List.of();
			}
			Collection<QuoteCb> myColl = this.createCbQuoteTimeFrame(timeFrame1, isDay, quotes);
			this.countRelevantProperties(nonZeroProperties, myColl);
			if (!myColl.isEmpty()) {
				try {
					this.quoteRepository.insertAll(isDay ? CB_DAY_COL : CB_HOUR_COL, myColl);
				} catch (Exception e) {
					LOG.warn(logFailed, e);
				}
			}
		} catch (Exception e) {
			LOG.warn(logFailed, e);
		}
		LOG.info(String.format("Prepared Coinbase %s Data for: ", isDay ? "Day" : "Hour")
				+ sdf.format(timeFrame1.begin().getTime()) + " Time: " + (new Date().getTime() - start.getTime()) + "ms"
				+ " 0 < properties: " + nonZeroProperties.get());
	}

	private Collection<QuoteCb> createCbQuoteTimeFrame(final MyTimeFrame timeFrame1, final boolean isDay,
			List<QuoteCb> quotes) {
		Date start = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		var result = isDay ? this.makeCbQuoteDay(quotes, timeFrame1.begin(), timeFrame1.end())
				: this.makeCbQuoteHour(quotes, timeFrame1.begin(), timeFrame1.end());
		LOG.info(String.format("Calculate Coinbase %s Data for: ", isDay ? "Day" : "Hour")
				+ sdf.format(timeFrame1.begin().getTime()) + " Time: " + (new Date().getTime() - start.getTime())
				+ "ms");
		return result;
	}

	private void createCbIntervalAvg(boolean isDay) {
		LOG.info(isDay ? "createCbDailyAvg()" : "createCbHourlyAvg()");
		LocalDateTime startAll = LocalDateTime.now();
		final MyTimeFrame timeFrame = this.quoteRepository.createTimeFrame(isDay ? CB_DAY_COL : CB_HOUR_COL, !isDay);
		final Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		LOG.info("isDay: {}, TimeFrame.Begin: {}, TimeFrame.End: {}, now: {}", isDay,
				sdf.format(timeFrame.begin().getTime()), sdf.format(timeFrame.end().getTime()),
				sdf.format(now.getTime()));
		this.createTimeFrames(timeFrame, now).stream()
				.forEachOrdered(timeFrame1 -> this.processTimeFrame(timeFrame1, isDay));
		var logStmt = String.format("Prepared Coinbase %s Data Time:", isDay ? "Daily" : "Hourly");
		LOG.info(this.serviceUtils.createAvgLogStatement(startAll, logStmt));
	}

	private List<MyTimeFrame> createTimeFrames(final MyTimeFrame timeFrame, final Calendar now) {
		final var timeFrames = new ArrayList<MyTimeFrame>();
		var begin = timeFrame.begin();
		var end = timeFrame.end();
		while (end.before(now)) {
			var myTimeFrame = new MyTimeFrame(begin, end);
			timeFrames.add(myTimeFrame);
			begin = nextDay(begin);
			end = nextDay(end);
		}
		return timeFrames;
	}

	private Calendar nextDay(Calendar begin) {
		var begin1 = GregorianCalendar.getInstance();
		begin1.setTime(begin.getTime());
		begin1.add(Calendar.DAY_OF_YEAR, 1);
		begin = begin1;
		return begin;
	}

	private Collection<QuoteCb> countRelevantProperties(final AtomicInteger nonZeroProperties,
			Collection<QuoteCb> myColl) {
		var relevantProperties = myColl.stream().flatMap(myQuote -> Stream.of(this.propertiesNonZero(myQuote)))
				.mapToInt(v -> v).max().orElse(0);
		nonZeroProperties
				.set(nonZeroProperties.get() < relevantProperties ? relevantProperties : nonZeroProperties.get());
		return myColl;
	}

	private Collection<QuoteCb> makeCbQuoteDay(List<QuoteCb> quotes, Calendar begin, Calendar end) {
		List<QuoteCb> hourQuotes = new LinkedList<QuoteCb>();
		QuoteCb quoteCb = new QuoteCb();
		quoteCb.setCreatedAt(begin.getTime());
		long count = quotes.stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).count();
		if (count > 2) {
			quoteCb = quotes.stream().filter(quote -> {
				return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
			}).reduce(quoteCb, (q1, q2) -> avgCbQuotePeriod(q1, q2, count));
			hourQuotes.add(quoteCb);
		}
		return hourQuotes;
	}

	private Collection<QuoteCb> makeCbQuoteHour(List<QuoteCb> quotes, Calendar begin, Calendar end) {
		List<Calendar> hours = this.serviceUtils.createDayHours(begin);
		List<QuoteCb> hourQuotes = new LinkedList<QuoteCb>();
		for (int i = 0; i < 24; i++) {
			QuoteCb quoteCb = new QuoteCb();
			quoteCb.setCreatedAt(hours.get(i).getTime());
			final int x = i;
			long count = quotes.stream().filter(quote -> {
				return quote.getCreatedAt().after(hours.get(x).getTime())
						&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
			}).count();
			if (count > 2) {
				quoteCb = quotes.stream().filter(quote -> {
					return quote.getCreatedAt().after(hours.get(x).getTime())
							&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
				}).reduce(quoteCb, (q1, q2) -> avgCbQuotePeriod(q1, q2, count));
				hourQuotes.add(quoteCb);
			}
		}
		return hourQuotes;
	}

	private QuoteCb avgCbQuotePeriod(QuoteCb q1, QuoteCb q2, long count) {
		QuoteCb result = avgCbQuotePeriodMF(q1, q2, count);
		return result;
	}

	private Integer propertiesNonZero(QuoteCb quote) {
		var result = new AtomicInteger(0);
		this.propertyDescriptors.forEach(myPropertyDescriptor -> {
			try {
				var gsmf = this.createGetMethodFunction(myPropertyDescriptor);
				BigDecimal num1 = gsmf.getter.apply(quote);
				result.set(num1.compareTo(BigDecimal.ZERO) > 0 ? result.addAndGet(1) : result.get());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return result.get();
	}

	private QuoteCb avgCbQuotePeriodMF(QuoteCb q1, QuoteCb q2, long count) {
		QuoteCb result = new QuoteCb();
		this.propertyDescriptors.forEach(myPropertyDescriptor -> {
			try {
				var gsmf = this.createGetMethodFunction(myPropertyDescriptor);
				BigDecimal num1 = gsmf.getter.apply(q1);
				BigDecimal num2 = gsmf.getter.apply(q2);
				BigDecimal resultValue = this.serviceUtils.avgHourValue(num1, num2, count);
				gsmf.setter.accept(result, resultValue);
				result.setCreatedAt(q1.getCreatedAt());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	private GetSetMethodFunctions createGetMethodFunction(PropertyDescriptor propertyDescriptor) throws Exception {
		GetSetMethodFunctions gsmf = cbFunctionCache.get(propertyDescriptor.getName());
		// log.info(propertyDescriptor.getName());
		if (gsmf == null) {
			synchronized (this) {
				if (cbFunctionCache.size() > 10000) {
					LOG.info("CbFunctionCache size: {}", cbFunctionCache.size());
					cbFunctionCache.clear();
				}
				gsmf = cbFunctionCache.get(propertyDescriptor.getName());
				if (gsmf == null) {
					final MethodHandles.Lookup lookupGetter = MethodHandles.lookup();
					final MethodHandles.Lookup lookupSetter = MethodHandles.lookup();
					record GetSetMethods(Method getterMethod, Method setterMethod) {
					}
					var result = switch (propertyDescriptor.getName().toLowerCase()) {
					case "1inch" -> new GetSetMethods(
							Stream.of(QuoteCb.class.getMethods())
									.filter(myMethod -> myMethod.getName().equalsIgnoreCase("get1Inch")).findFirst()
									.orElseThrow(),
							Stream.of(QuoteCb.class.getMethods())
									.filter(myMethod -> myMethod.getName().equalsIgnoreCase("set1Inch")).findFirst()
									.orElseThrow());
					case "super" -> new GetSetMethods(
							Stream.of(QuoteCb.class.getMethods())
									.filter(myMethod -> myMethod.getName().equalsIgnoreCase("getSuper")).findFirst()
									.orElseThrow(),
							Stream.of(QuoteCb.class.getMethods())
									.filter(myMethod -> myMethod.getName().equalsIgnoreCase("setSuper")).findFirst()
									.orElseThrow());
					case "try" -> new GetSetMethods(
							Stream.of(QuoteCb.class.getMethods())
									.filter(myMethod -> myMethod.getName().equalsIgnoreCase("getTry1")).findFirst()
									.orElseThrow(),
							Stream.of(QuoteCb.class.getMethods())
									.filter(myMethod -> myMethod.getName().equalsIgnoreCase("setTry1")).findFirst()
									.orElseThrow());
					default ->
						new GetSetMethods(propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod());
					};
					@SuppressWarnings("unchecked")
					Function<QuoteCb, BigDecimal> getterFunction = (Function<QuoteCb, BigDecimal>) DtoUtils
							.createGetter(lookupGetter, lookupGetter.unreflect(result.getterMethod()));
					@SuppressWarnings("unchecked")
					BiConsumer<QuoteCb, BigDecimal> setterFunction = DtoUtils.createSetter(lookupSetter,
							lookupSetter.unreflect(result.setterMethod()));
					cbFunctionCache.put(propertyDescriptor.getName(), new GetSetMethodFunctions(getterFunction,
							setterFunction, propertyDescriptor.getName(), propertyDescriptor));
					gsmf = cbFunctionCache.get(propertyDescriptor.getName());
				}
			}
		}
		return gsmf;
	}

	private static boolean filterEvenMinutes(QuoteCb quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}