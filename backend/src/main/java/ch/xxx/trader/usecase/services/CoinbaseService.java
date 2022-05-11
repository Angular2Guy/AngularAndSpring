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
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import ch.xxx.trader.domain.model.entity.QuoteCb;
import ch.xxx.trader.domain.model.entity.QuoteCbSmall;
import ch.xxx.trader.usecase.common.DtoUtils;
import ch.xxx.trader.usecase.services.ServiceUtils.MyTimeFrame;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CoinbaseService {
	private static final Logger log = LoggerFactory.getLogger(CoinbaseService.class);
	private static final Map<String, GetSetMethodHandles> cbMethodCache = new ConcurrentHashMap<>();
	private static final Map<String, GetSetMethodFunctions> cbFunctionCache = new ConcurrentHashMap<>();

	private record GetSetMethodHandles(MethodHandle getter, MethodHandle setter, String fieldName) {
	}

	private record GetSetMethodFunctions(Function<QuoteCb, BigDecimal> getter, BiConsumer<QuoteCb, BigDecimal> setter,
			String propertyName, PropertyDescriptor propertyDescriptor) {
	}

	public static final String CB_HOUR_COL = "quoteCbHour";
	public static final String CB_DAY_COL = "quoteCbDay";
	private final MyMongoRepository myMongoRepository;
	private final ServiceUtils serviceUtils;
	@Value("${kubernetes.pod.cpu.constraint}")
	private boolean cpuConstraint;
	private final List<Field> valueFields;
	private final List<String> nonValueFieldNames = List.of("_id", "createdAt", "class");
	private final List<PropertyDescriptor> propertyDescriptors;

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
//		this.propertyDescriptors = List.of();
		valueFields = List.of(QuoteCb.class.getDeclaredFields()).stream()
				.filter(myField -> !this.nonValueFieldNames.contains(myField.getName())).toList();
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

	public Mono<QuoteCb> currentQuoteBc() {
		Query query = MongoUtils.buildCurrentQuery(Optional.empty());
		return this.myMongoRepository.findOne(query, QuoteCb.class);
	}

	public void createCbAvg() {
		this.myMongoRepository.ensureIndex(CB_HOUR_COL, DtoUtils.CREATEDAT)
		.then(this.myMongoRepository.ensureIndex(CB_DAY_COL, DtoUtils.CREATEDAT))
		.doAfterTerminate(() -> this.createHourDayAvg()).subscribe();		
	}

	private void createHourDayAvg() {
		LocalDateTime start = LocalDateTime.now();
		log.info("CpuConstraint property: " + this.cpuConstraint);
		if (this.cpuConstraint) {
			this.createCbHourlyAvg();
			this.createCbDailyAvg();
			log.info(this.serviceUtils.createAvgLogStatement(start, "Prepared Coinbase Data Time:"));
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
			log.info(combined);
		}
	}

	private void createCbHourlyAvg() {
		LocalDateTime startAll = LocalDateTime.now();
		MyTimeFrame timeFrame = this.serviceUtils.createTimeFrame(CB_HOUR_COL, QuoteCb.class, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(
					Criteria.where("createdAt").gt(timeFrame.begin().getTime()).lt(timeFrame.end().getTime()));
			// Coinbase
			Mono<Collection<QuoteCb>> collectCb = this.myMongoRepository.find(query, QuoteCb.class).collectList()
					.map(quotes -> makeCbQuoteHour(quotes, timeFrame.begin(), timeFrame.end()));
			this.myMongoRepository.insertAll(collectCb, CB_HOUR_COL).blockLast();

			timeFrame.begin().add(Calendar.DAY_OF_YEAR, 1);
			timeFrame.end().add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Coinbase Hour Data for: " + sdf.format(timeFrame.begin().getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
		log.info(this.serviceUtils.createAvgLogStatement(startAll, "Prepared Coinbase Hourly Data Time:"));
	}

	private void createCbDailyAvg() {
		LocalDateTime startAll = LocalDateTime.now();
		MyTimeFrame timeFrame = this.serviceUtils.createTimeFrame(CB_DAY_COL, QuoteCb.class, false);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(
					Criteria.where("createdAt").gt(timeFrame.begin().getTime()).lt(timeFrame.end().getTime()));
			// Coinbase
			Mono<Collection<QuoteCb>> collectCb = this.myMongoRepository.find(query, QuoteCb.class).collectList()
					.map(quotes -> makeCbQuoteDay(quotes, timeFrame.begin(), timeFrame.end()));
			this.myMongoRepository.insertAll(collectCb, CB_DAY_COL).blockLast();

			timeFrame.begin().add(Calendar.DAY_OF_YEAR, 1);
			timeFrame.end().add(Calendar.DAY_OF_YEAR, 1);
			log.info("Prepared Coinbase Day Data for: " + sdf.format(timeFrame.begin().getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
		log.info(this.serviceUtils.createAvgLogStatement(startAll, "Prepared Coinbase Daily Data Time:"));
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
		// QuoteCb result = avgCbQuotePeriodMH(q1, q2, count);
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
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	private QuoteCb avgCbQuotePeriodMH(QuoteCb q1, QuoteCb q2, long count) {
		QuoteCb result = new QuoteCb();
		this.valueFields.forEach(myField -> {
			try {
				GetSetMethodHandles gsmh = this.createGetMethodHandle(myField);
				BigDecimal num1 = (BigDecimal) gsmh.getter.invokeExact(q1);
				BigDecimal num2 = (BigDecimal) gsmh.getter.invokeExact(q2);
				BigDecimal resultValue = this.serviceUtils.avgHourValue(num1, num2, count);
				gsmh.setter.invokeExact(result, resultValue);
				result.setCreatedAt(q1.getCreatedAt());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	private GetSetMethodHandles createGetMethodHandle(Field field)
			throws NoSuchMethodException, IllegalAccessException {
		GetSetMethodHandles gsmh = cbMethodCache.get(field.getName());
		if (gsmh == null) {
			synchronized (this) {
				gsmh = cbMethodCache.get(field.getName());
				if (gsmh == null) {
					String getterName = String.format("get%s%s", field.getName().substring(0, 1).toUpperCase(),
							field.getName().substring(1).toLowerCase());
					String setterName = String.format("s%s", getterName.substring(1),
							field.getName().substring(1).toLowerCase());
					if ("getTry".equals(getterName)) {
						getterName = getterName + "1";
						setterName = setterName + "1";
					} else if ("getSuper1".equals(getterName)) {
						getterName = getterName.substring(0, (getterName.length() - 1));
						setterName = setterName.substring(0, (setterName.length() - 1));
					} else if ("getInch1".equals(getterName)) {
						final String methodNameEnd = "set1Inch".substring(1);
						getterName = String.format("g%s", methodNameEnd);
						setterName = String.format("s%s", methodNameEnd);
					}
					MethodType desc = MethodType.methodType(BigDecimal.class);
					MethodHandle getterHandle = MethodHandles.lookup().findVirtual(QuoteCb.class, getterName, desc);
					MethodType descSet = MethodType.methodType(void.class, BigDecimal.class);
					MethodHandle setterHandle = MethodHandles.lookup().findVirtual(QuoteCb.class, setterName, descSet);
					cbMethodCache.put(field.getName(),
							new GetSetMethodHandles(getterHandle, setterHandle, field.getName()));
					gsmh = cbMethodCache.get(field.getName());
				}
			}
		}
		return gsmh;
	}

	private GetSetMethodFunctions createGetMethodFunction(PropertyDescriptor propertyDescriptor) throws Exception {
		GetSetMethodFunctions gsmf = cbFunctionCache.get(propertyDescriptor.getName());
		//log.info(propertyDescriptor.getName());
		if (gsmf == null) {
			synchronized (this) {
				gsmf = cbFunctionCache.get(propertyDescriptor.getName());
				if (gsmf == null) {
					final MethodHandles.Lookup lookupGetter = MethodHandles.lookup();
					final MethodHandles.Lookup lookupSetter = MethodHandles.lookup();
					Method getterMethod = null;
					Method setterMethod = null;
					if("1Inch".equalsIgnoreCase(propertyDescriptor.getName())) {
						getterMethod = Stream.of(QuoteCb.class.getMethods()).filter(myMethod -> myMethod.getName().equalsIgnoreCase("get1Inch")).findFirst().orElseThrow();
						setterMethod = Stream.of(QuoteCb.class.getMethods()).filter(myMethod -> myMethod.getName().equalsIgnoreCase("set1Inch")).findFirst().orElseThrow();						
					} else if("super".equalsIgnoreCase(propertyDescriptor.getName())) {
						getterMethod = Stream.of(QuoteCb.class.getMethods()).filter(myMethod -> myMethod.getName().equalsIgnoreCase("getSuper")).findFirst().orElseThrow();
						setterMethod = Stream.of(QuoteCb.class.getMethods()).filter(myMethod -> myMethod.getName().equalsIgnoreCase("setSuper")).findFirst().orElseThrow();
					} else if("try".equalsIgnoreCase(propertyDescriptor.getName())) {
						getterMethod = Stream.of(QuoteCb.class.getMethods()).filter(myMethod -> myMethod.getName().equalsIgnoreCase("getTry1")).findFirst().orElseThrow();
						setterMethod = Stream.of(QuoteCb.class.getMethods()).filter(myMethod -> myMethod.getName().equalsIgnoreCase("setTry1")).findFirst().orElseThrow();
					} else {
						getterMethod = propertyDescriptor.getReadMethod();
						setterMethod = propertyDescriptor.getWriteMethod();
					}
					@SuppressWarnings("unchecked")
					Function<QuoteCb, BigDecimal> getterFunction = (Function<QuoteCb, BigDecimal>) DtoUtils.createGetter(lookupGetter,
							lookupGetter.unreflect(getterMethod));
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
