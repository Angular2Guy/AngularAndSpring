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

import java.lang.reflect.InvocationTargetException;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.xxx.trader.domain.model.entity.*;
import ch.xxx.trader.domain.services.*;
import ch.xxx.trader.usecase.common.DtoUtils;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.common.MongoUtils.TimeFrame;
import ch.xxx.trader.usecase.mappers.ReportMapper;

@Service
public class BitfinexService {
	private static final Logger LOG = LoggerFactory.getLogger(BitfinexService.class);
	public static final String BF_HOUR_COL = "quoteBfHour";
	public static final String BF_DAY_COL = "quoteBfDay";
	public static volatile boolean singleInstanceLock = false;
	private final MyOrderBookClient orderBookClient;
	private final ReportMapper reportMapper;
	private final QuoteBfRepository quoteBfRepository;
	private final QuoteDayBfRepository quoteDayBfRepository;
	private final QuoteHourBfRepository quoteHourBfRepository;
	private final MongoQuoteRepository mongoQuoteRepository;
	private final ServiceUtils serviceUtils;
	@Value("${single.instance.deployment:false}")
	private boolean singleInstanceDeployment;

	public BitfinexService(ServiceUtils serviceUtils, MyOrderBookClient orderBookClient, ReportMapper reportMapper,
						   QuoteBfRepository quoteBfRepository, MongoQuoteRepository mongoQuoteRepository,
						   QuoteDayBfRepository quoteDayBfRepository, QuoteHourBfRepository quoteHourBfRepository) {
		this.orderBookClient = orderBookClient;
		this.reportMapper = reportMapper;
		this.quoteBfRepository = quoteBfRepository;
		this.serviceUtils = serviceUtils;
		this.mongoQuoteRepository = mongoQuoteRepository;
		this.quoteDayBfRepository = quoteDayBfRepository;
		this.quoteHourBfRepository = quoteHourBfRepository;
	}

	public String getOrderbook(String currpair) {
		return this.orderBookClient.getOrderbookBitfinex(currpair);
	}

	public QuoteBf insertQuote(QuoteBf quote) {
		return this.quoteBfRepository.insert(quote);
	}

	public Optional<QuoteBf> currentQuote(String pair) {
		return this.quoteBfRepository.findFirstByPairAndCreatedAtAfterOrderByCreatedAtDesc(pair,
				MongoUtils.buildStartDate(TimeFrame.CURRENT));
	}

	public List<QuoteBf> tfQuotes(String timeFrame, String pair) {
		return DtoUtils.tfQuotes(timeFrame, pair, this.quoteBfRepository, BF_HOUR_COL, BF_DAY_COL);
	}

	public byte[] pdfReport(String timeFrame, String pair) {
		List<QuoteBf> quotes = DtoUtils.tfQuotes(timeFrame, pair, this.quoteBfRepository, BF_HOUR_COL, BF_DAY_COL);
		return this.serviceUtils.generatePdf(quotes, this.reportMapper::convert);
	}

	public void createBfAvg() {
		if ((this.singleInstanceDeployment && !BitfinexService.singleInstanceLock) || !this.singleInstanceDeployment) {
			BitfinexService.singleInstanceLock = true;
			try {
				this.ensureIndexes();
				this.createHourDayAvg();
			} catch (Exception e) {
				LOG.info("createBfAvg() failed.", e);
			}
		}
	}

	private void ensureIndexes() {
		try {
			this.mongoQuoteRepository.ensureIndex(QuoteHourBf.class);
		} catch (Exception e) {
			LOG.info("ensureIndex(" + QuoteHourBf.class.getName() + ") failed.", e);
		}
		try {
			this.mongoQuoteRepository.ensureIndex(QuoteDayBf.class);
		} catch (Exception e) {
			LOG.info("ensureIndex(" + QuoteDayBf.class.getName() + ") failed.", e);
		}
	}

	private String createHourDayAvg() {
		LOG.info("createHourDayAvg()");
		Thread task1 = Thread.ofVirtual().name("createBfHourlyAvg").unstarted(this::createBfHourlyAvg);
		Thread task2 = Thread.ofVirtual().name("createBfDailyAvg").unstarted(this::createBfDailyAvg);
		task1.start();
		task2.start();
		try {
			Thread.sleep(10_000L);
			task1.join();
			Thread.sleep(10_000L);
			task2.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		LOG.info("createBfHourlyAvg() and createBfDailyAvg() done.");
		return "done";
	}

	private void createBfHourlyAvg() {
		LocalDateTime startAll = LocalDateTime.now();
		MyTimeFrame timeFrame = this.mongoQuoteRepository.createTimeFrame(QuoteBf.class,QuoteHourBf.class, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			// Bitfinex
			var collectBf = this.collectBfQuotes(timeFrame, false, QuoteHourBf.class);
			if (!collectBf.isEmpty()) {
				try {
					this.quoteHourBfRepository.insert(collectBf);
				} catch (Exception e) {
					LOG.warn("Bitfinex prepare hour data failed", e);
				}
			}

			timeFrame.begin().add(Calendar.DAY_OF_YEAR, 1);
			timeFrame.end().add(Calendar.DAY_OF_YEAR, 1);
			LOG.info("Prepared Bitfinex Hour Data for: " + sdf.format(timeFrame.begin().getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
		LOG.info(this.serviceUtils.createAvgLogStatement(startAll, "Prepared Bitfinex Hourly Data Time:"));
	}

	private void createBfDailyAvg() {
		LocalDateTime startAll = LocalDateTime.now();
		MyTimeFrame timeFrame = this.mongoQuoteRepository.createTimeFrame(QuoteBf.class,QuoteDayBf.class, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			// Bitfinex
			var collectBf = this.collectBfQuotes(timeFrame, true, QuoteDayBf.class);
			if (!collectBf.isEmpty()) {
				try {
					this.quoteDayBfRepository.insert(collectBf);
				} catch (Exception e) {
					LOG.warn("Bitfinex prepare day data failed", e);
				}
			}

			timeFrame.begin().add(Calendar.DAY_OF_YEAR, 1);
			timeFrame.end().add(Calendar.DAY_OF_YEAR, 1);
			LOG.info("Prepared Bitfinex Day Data for: " + sdf.format(timeFrame.begin().getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
		LOG.info(this.serviceUtils.createAvgLogStatement(startAll, "Prepared Bitfinex Daily Data Time:"));
	}

	private <T> Collection<T> collectBfQuotes(MyTimeFrame timeFrame, boolean day, Class<T> myClass) {
		Map<String, List<QuoteBf>> multimap;
		try {
			multimap = this.quoteBfRepository
					.findByCreatedAtGreaterThanAndCreatedAtLessThan(timeFrame.begin().getTime(), timeFrame.end().getTime())
					.stream().filter(value -> Optional.ofNullable(value.getPair()).stream().anyMatch(Predicate.not(String::isBlank))).collect(Collectors.groupingBy(QuoteBf::getPair));
		} catch (Exception e) {
			LOG.warn(day ? "Bitfinex prepare day data failed" : "Bitfinex prepare hour data failed", e);
			return List.of();
		}
		var quotes = multimap.keySet().stream().map(key -> day ? this.makeBfQuoteDay(key, multimap, timeFrame.begin(),
				timeFrame.end()) : this.makeBfQuoteHour(key, multimap, timeFrame.begin(), timeFrame.end()))
				.filter(Predicate.not(Collection::isEmpty)).flatMap(Collection::stream).toList();
		return quotes.stream().map(value -> mapToDest(myClass, value)).collect(Collectors.toList());
	}

	private <T> @NonNull T mapToDest(Class<T> myClass, QuoteBf value) {
		T dest;
		try {
			dest = myClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
				 NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		BeanUtils.copyProperties(value, dest);
		return dest;
	}

	private Collection<QuoteBf> makeBfQuoteHour(String key, Map<String, List<QuoteBf>> multimap, Calendar begin,
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

	private Collection<QuoteBf> makeBfQuoteDay(String key, Map<String, List<QuoteBf>> multimap, Calendar begin,
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
				this.serviceUtils.avgHourValue(q1.getVolume(), q2.getVolume(), count), q1.getTimestamp());
		myQuote.setCreatedAt(q1.getCreatedAt());
		return myQuote;
	}
}