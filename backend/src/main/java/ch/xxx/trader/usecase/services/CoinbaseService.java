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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.common.MongoUtils.TimeFrame;
import ch.xxx.trader.domain.model.entity.QuoteCb;
import ch.xxx.trader.domain.model.entity.QuoteCbSmall;
import ch.xxx.trader.usecase.common.DtoUtils;
import ch.xxx.trader.usecase.services.ServiceUtils.MyTimeFrame;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

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
	private final MyMongoRepository myMongoRepository;
	private final ServiceUtils serviceUtils;
	@Value("${kubernetes.pod.cpu.constraint}")
	private boolean cpuConstraint;
	private final List<String> nonValueFieldNames = List.of("_id", "createdAt", "class");
	private final List<PropertyDescriptor> propertyDescriptors;
	private final Scheduler mongoScheduler = Schedulers.newBoundedElastic(10, 10, "mongoImport", 10);
	@Value("${single.instance.deployment:false}")
	private boolean singleInstanceDeployment;

	public CoinbaseService(MyMongoRepository myMongoRepository, ServiceUtils serviceUtils) {
		this.myMongoRepository = myMongoRepository;
		this.serviceUtils = serviceUtils;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(QuoteCb.class);
			this.propertyDescriptors = Stream.of(beanInfo.getPropertyDescriptors())
					.filter(myDescriptor -> !this.nonValueFieldNames.contains(myDescriptor.getName())).toList();
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	public Mono<QuoteCb> insertQuote(Mono<QuoteCb> quote) {
		return this.myMongoRepository.insert(quote);
	}

	public Flux<QuoteCbSmall> todayQuotesBc() {
		Query query = MongoUtils.buildTodayQuery(Optional.empty());
		return this.myMongoRepository.find(query, QuoteCb.class).filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()));
	}

	public Flux<QuoteCbSmall> sevenDaysQuotesBc() {
		Query query = MongoUtils.build7DayQuery(Optional.empty());
		return this.myMongoRepository.find(query, QuoteCb.class, CB_HOUR_COL).filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()));
	}

	public Flux<QuoteCbSmall> thirtyDaysQuotesBc() {
		Query query = MongoUtils.build30DayQuery(Optional.empty());
		return this.myMongoRepository.find(query, QuoteCb.class, CB_DAY_COL).filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()));
	}

	public Flux<QuoteCbSmall> nintyDaysQuotesBc() {
		Query query = MongoUtils.build90DayQuery(Optional.empty());
		return this.myMongoRepository.find(query, QuoteCb.class, CB_DAY_COL).filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()));
	}

	public Flux<QuoteCbSmall> sixMonthsQuotesBc() {
		Query query = MongoUtils.buildTimeFrameQuery(Optional.empty(), TimeFrame.Month6);
		return this.myMongoRepository.find(query, QuoteCb.class, CB_DAY_COL).filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()));
	}

	public Flux<QuoteCbSmall> oneYearQuotesBc() {
		Query query = MongoUtils.buildTimeFrameQuery(Optional.empty(), TimeFrame.Year1);
		return this.myMongoRepository.find(query, QuoteCb.class, CB_DAY_COL).filter(q -> filterEvenMinutes(q))
				.map(quote -> new QuoteCbSmall(quote.getCreatedAt(), quote.getUsd(), quote.getEur(), quote.getEth(),
						quote.getLtc()));
	}

	public Mono<QuoteCb> currentQuoteBc() {
		Query query = MongoUtils.buildCurrentQuery(Optional.empty());
		return this.myMongoRepository.findOne(query, QuoteCb.class);
	}

	public Mono<String> createCbAvg() {
		Mono<String> result = Mono.empty();
		if ((this.singleInstanceDeployment && !CoinbaseService.singleInstanceLock) || !this.singleInstanceDeployment) {
			CoinbaseService.singleInstanceLock = true;
			result = this.myMongoRepository.ensureIndex(CB_HOUR_COL, DtoUtils.CREATEDAT)
					.subscribeOn(this.mongoScheduler).timeout(Duration.ofMinutes(5L))
					.doOnError(ex -> LOG.info("ensureIndex(" + CB_HOUR_COL + ") failed.", ex))
					.then(this.myMongoRepository.ensureIndex(CB_DAY_COL, DtoUtils.CREATEDAT)
							.subscribeOn(this.mongoScheduler).timeout(Duration.ofMinutes(5L))
							.doOnError(ex -> LOG.info("ensureIndex(" + CB_DAY_COL + ") failed.", ex)))
					.map(value -> this.createHourDayAvg()).timeout(Duration.ofHours(2L))
					.doOnError(ex -> LOG.info("createCbAvg() failed.", ex))
					.subscribeOn(this.mongoScheduler);
		}
		return result;
	}

	private String createHourDayAvg() {
		LOG.info("createHourDayAvg()");
		LocalDateTime start = LocalDateTime.now();
		LOG.info("CpuConstraint property: " + this.cpuConstraint);
		if (this.cpuConstraint) {
			this.createCbHourlyAvg();
			this.createCbDailyAvg();
			LOG.info(this.serviceUtils.createAvgLogStatement(start, "Prepared Coinbase Data Time:"));
		} else {
			// This can only be used on machines without cpu constraints.
			CompletableFuture<String> future7 = CompletableFuture.supplyAsync(() -> {
				this.createCbHourlyAvg();
				return "createCbHourlyAvg() Done.";
			}, CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS));
			CompletableFuture<String> future8 = CompletableFuture.supplyAsync(() -> {
				this.createCbDailyAvg();
				return "createCbDailyAvg() Done.";
			}, CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS));
			String combined = Stream.of(future7, future8).map(CompletableFuture::join).collect(Collectors.joining(" "));
			LOG.info(combined);
		}
		return "done.";
	}

	private void createCbHourlyAvg() {
		LOG.info("createCbHourlyAvg()");
		LocalDateTime startAll = LocalDateTime.now();
		MyTimeFrame timeFrame = this.serviceUtils.createTimeFrame(CB_HOUR_COL, QuoteCb.class, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(
					Criteria.where(DtoUtils.CREATEDAT).gt(timeFrame.begin().getTime()).lt(timeFrame.end().getTime()));
			// Coinbase
			Mono<Collection<QuoteCb>> collectCb = this.myMongoRepository.find(query, QuoteCb.class)
					.timeout(Duration.ofSeconds(5L)).doOnError(ex -> LOG.warn("Coinbase prepare hour data failed", ex))
					.onErrorResume(ex -> Mono.empty()).subscribeOn(this.mongoScheduler).collectList()
					.map(quotes -> makeCbQuoteHour(quotes, timeFrame.begin(), timeFrame.end()));
			collectCb.filter(myColl -> !myColl.isEmpty())
					.flatMap(myColl -> this.myMongoRepository.insertAll(Mono.just(myColl), CB_HOUR_COL)
							.timeout(Duration.ofSeconds(5L))
							.doOnError(ex -> LOG.warn("Coinbase prepare hour data failed", ex))
							.onErrorResume(ex -> Mono.empty()).subscribeOn(this.mongoScheduler).collectList())
					.subscribeOn(this.mongoScheduler).block();

			timeFrame.begin().add(Calendar.DAY_OF_YEAR, 1);
			timeFrame.end().add(Calendar.DAY_OF_YEAR, 1);
			LOG.info("Prepared Coinbase Hour Data for: " + sdf.format(timeFrame.begin().getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
		LOG.info(this.serviceUtils.createAvgLogStatement(startAll, "Prepared Coinbase Hourly Data Time:"));
	}

	private void createCbDailyAvg() {
		LOG.info("createCbDailyAvg()");
		LocalDateTime startAll = LocalDateTime.now();
		MyTimeFrame timeFrame = this.serviceUtils.createTimeFrame(CB_DAY_COL, QuoteCb.class, false);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(
					Criteria.where(DtoUtils.CREATEDAT).gt(timeFrame.begin().getTime()).lt(timeFrame.end().getTime()));
			// Coinbase
			Mono<Collection<QuoteCb>> collectCb = this.myMongoRepository.find(query, QuoteCb.class)
					.timeout(Duration.ofSeconds(5L)).doOnError(ex -> LOG.warn("Coinbase prepare day data failed", ex))
					.onErrorResume(ex -> Mono.empty()).subscribeOn(this.mongoScheduler).collectList()
					.map(quotes -> makeCbQuoteDay(quotes, timeFrame.begin(), timeFrame.end()));
			collectCb.filter(myColl -> !myColl.isEmpty())
					.flatMap(myColl -> this.myMongoRepository.insertAll(Mono.just(myColl), CB_DAY_COL)
							.timeout(Duration.ofSeconds(5L))
							.doOnError(ex -> LOG.warn("Coinbase prepare day data failed", ex))
							.onErrorResume(ex -> Mono.empty()).subscribeOn(this.mongoScheduler).collectList())
					.subscribeOn(this.mongoScheduler).block();

			timeFrame.begin().add(Calendar.DAY_OF_YEAR, 1);
			timeFrame.end().add(Calendar.DAY_OF_YEAR, 1);
			LOG.info("Prepared Coinbase Day Data for: " + sdf.format(timeFrame.begin().getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
		LOG.info(this.serviceUtils.createAvgLogStatement(startAll, "Prepared Coinbase Daily Data Time:"));
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

	private QuoteCb avgCbQuotePeriodMF(QuoteCb q1, QuoteCb q2, long count) {
		QuoteCb result = new QuoteCb();
		this.propertyDescriptors.forEach(myPropertyDescriptor -> {
			GetSetMethodFunctions gsmf;
			try {
				gsmf = this.createGetMethodFunction(myPropertyDescriptor);
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
				gsmf = cbFunctionCache.get(propertyDescriptor.getName());
				if (gsmf == null) {
					final MethodHandles.Lookup lookupGetter = MethodHandles.lookup();
					final MethodHandles.Lookup lookupSetter = MethodHandles.lookup();
					Method getterMethod = null;
					Method setterMethod = null;
					if ("1Inch".equalsIgnoreCase(propertyDescriptor.getName())) {
						getterMethod = Stream.of(QuoteCb.class.getMethods())
								.filter(myMethod -> myMethod.getName().equalsIgnoreCase("get1Inch")).findFirst()
								.orElseThrow();
						setterMethod = Stream.of(QuoteCb.class.getMethods())
								.filter(myMethod -> myMethod.getName().equalsIgnoreCase("set1Inch")).findFirst()
								.orElseThrow();
					} else if ("super".equalsIgnoreCase(propertyDescriptor.getName())) {
						getterMethod = Stream.of(QuoteCb.class.getMethods())
								.filter(myMethod -> myMethod.getName().equalsIgnoreCase("getSuper")).findFirst()
								.orElseThrow();
						setterMethod = Stream.of(QuoteCb.class.getMethods())
								.filter(myMethod -> myMethod.getName().equalsIgnoreCase("setSuper")).findFirst()
								.orElseThrow();
					} else if ("try".equalsIgnoreCase(propertyDescriptor.getName())) {
						getterMethod = Stream.of(QuoteCb.class.getMethods())
								.filter(myMethod -> myMethod.getName().equalsIgnoreCase("getTry1")).findFirst()
								.orElseThrow();
						setterMethod = Stream.of(QuoteCb.class.getMethods())
								.filter(myMethod -> myMethod.getName().equalsIgnoreCase("setTry1")).findFirst()
								.orElseThrow();
					} else {
						getterMethod = propertyDescriptor.getReadMethod();
						setterMethod = propertyDescriptor.getWriteMethod();
					}
					@SuppressWarnings("unchecked")
					Function<QuoteCb, BigDecimal> getterFunction = (Function<QuoteCb, BigDecimal>) DtoUtils
							.createGetter(lookupGetter, lookupGetter.unreflect(getterMethod));
					@SuppressWarnings("unchecked")
					BiConsumer<QuoteCb, BigDecimal> setterFunction = DtoUtils.createSetter(lookupSetter,
							lookupSetter.unreflect(setterMethod));
					cbFunctionCache.put(propertyDescriptor.getName(), new GetSetMethodFunctions(getterFunction,
							setterFunction, propertyDescriptor.getName(), propertyDescriptor));
					gsmf = cbFunctionCache.get(propertyDescriptor.getName());
				}
			}
		}
		return gsmf;
	}

	private boolean filterEvenMinutes(QuoteCb quote) {
		return MongoUtils.filterEvenMinutes(quote.getCreatedAt());
	}
}
