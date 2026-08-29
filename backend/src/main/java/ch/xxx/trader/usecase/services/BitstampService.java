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

import ch.xxx.trader.domain.model.entity.QuoteDayBf;
import ch.xxx.trader.domain.model.entity.QuoteDayBs;
import ch.xxx.trader.domain.model.entity.QuoteHourBs;
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
import ch.xxx.trader.domain.model.entity.QuoteBs;
import ch.xxx.trader.usecase.mappers.ReportMapper;

@Service
public class BitstampService {
	private static final Logger LOG = LoggerFactory.getLogger(BitstampService.class);
	public static volatile boolean singleInstanceLock = false;
	private final MyOrderBookClient orderBookClient;
	private final ReportMapper reportMapper;
	private final QuoteBsRepository quoteBsRepository;
	private final MongoQuoteRepository mongoQuoteRepository;
	private final QuoteDayBsRepository quoteDayBsRepository;
	private final QuoteHourBsRepository quoteHourBsRepository;
	private final ServiceUtils serviceUtils;
	@Value("${single.instance.deployment:false}")
	private boolean singleInstanceDeployment;

	public BitstampService(MyOrderBookClient orderBookClient, QuoteBsRepository quoteBsRepository, MongoQuoteRepository mongoQuoteRepository,
						   ServiceUtils serviceUtils, ReportMapper reportMapper,
						   QuoteDayBsRepository quoteDayBsRepository, QuoteHourBsRepository quoteHourBsRepository) {
		this.orderBookClient = orderBookClient;
		this.reportMapper = reportMapper;
		this.quoteBsRepository = quoteBsRepository;
		this.serviceUtils = serviceUtils;
		this.mongoQuoteRepository = mongoQuoteRepository;
		this.quoteDayBsRepository = quoteDayBsRepository;
		this.quoteHourBsRepository = quoteHourBsRepository;
	}

	public QuoteBs insertQuote(QuoteBs quote) {
		return this.quoteBsRepository.insert(quote);
	}

	public String getOrderbook(String currpair) {
		return this.orderBookClient.getOrderbookBitstamp(currpair);
	}

	public Optional<QuoteBs> currentQuoteBtc(String pair) {
		return this.quoteBsRepository.findFirstByPairAndCreatedAtAfterOrderByCreatedAtDesc(pair.toLowerCase(),
				MongoUtils.buildStartDate(TimeFrame.CURRENT));
	}

	public List<QuoteBs> tfQuotes(String timeFrame, String pair) {
		return this.mongoQuoteRepository.tfQuotes(timeFrame, pair, QuoteBs.class);
	}

	public byte[] pdfReport(String timeFrame, String pair) {
		List<QuoteBs> quotes = this.mongoQuoteRepository.tfQuotes(timeFrame, pair, QuoteBs.class);
		return this.serviceUtils.generatePdf(quotes, this.reportMapper::convert);
	}

	public void createBsAvg() {
		if ((this.singleInstanceDeployment && !BitstampService.singleInstanceLock) || !this.singleInstanceDeployment) {
			BitstampService.singleInstanceLock = true;
			try {
				this.ensureIndexes();
				this.createHourDayAvg();
			} catch (Exception e) {
				LOG.info("createBsAvg() failed.", e);
			}
		}
	}

	private void ensureIndexes() {
		try {
			this.mongoQuoteRepository.ensureIndex(QuoteHourBs.class);
		} catch (Exception e) {
			LOG.info("ensureIndex(" + QuoteHourBs.class.getSimpleName() + ") failed.", e);
		}
		try {
			this.mongoQuoteRepository.ensureIndex(QuoteDayBf.class);
		} catch (Exception e) {
			LOG.info("ensureIndex(" + QuoteDayBs.class.getSimpleName() + ") failed.", e);
		}
	}

	private String createHourDayAvg() {
		LOG.info("createHourDayAvg()");
		Thread task1 = Thread.ofVirtual().name("createBsHourlyAvg").unstarted(this::createBsHourlyAvg);
		Thread task2 = Thread.ofVirtual().name("createBsDailyAvg").unstarted(this::createBsDailyAvg);
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
		LOG.info("createBsHourlyAvg() and createBsDailyAvg() done.");
		return "done";
	}

	private void createBsHourlyAvg() {
		LocalDateTime startAll = LocalDateTime.now();
		MyTimeFrame timeFrame = this.mongoQuoteRepository.createTimeFrame(QuoteBs.class, QuoteHourBs.class, true, this.quoteBsRepository, this.quoteHourBsRepository);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			// Bitstamp
			var collectBs = this.collectBsQuotes(timeFrame, false, QuoteHourBs.class);
			if (!collectBs.isEmpty()) {
				try {
					this.quoteHourBsRepository.insert(collectBs);
				} catch (Exception e) {
					LOG.warn("Bitstamp prepare hour data failed", e);
				}
			}

			timeFrame.begin().add(Calendar.DAY_OF_YEAR, 1);
			timeFrame.end().add(Calendar.DAY_OF_YEAR, 1);
			LOG.info("Prepared Bitstamp Hour Data for: " + sdf.format(timeFrame.begin().getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
		LOG.info(this.serviceUtils.createAvgLogStatement(startAll, "Prepared Bitstamp Hourly Data Time:"));
	}

	private void createBsDailyAvg() {
		LocalDateTime startAll = LocalDateTime.now();
		MyTimeFrame timeFrame = this.mongoQuoteRepository.createTimeFrame(QuoteBs.class, QuoteDayBs.class, false, this.quoteBsRepository, this.quoteDayBsRepository);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			// Bitstamp
			var collectBs = this.collectBsQuotes(timeFrame, true, QuoteDayBs.class);
			if (!collectBs.isEmpty()) {
				try {
					this.quoteDayBsRepository.insert(collectBs);
				} catch (Exception e) {
					LOG.warn("Bitstamp prepare day data failed", e);
				}
			}

			timeFrame.begin().add(Calendar.DAY_OF_YEAR, 1);
			timeFrame.end().add(Calendar.DAY_OF_YEAR, 1);
			LOG.info("Prepared Bitstamp Day Data for: " + sdf.format(timeFrame.begin().getTime()) + " Time: "
					+ (new Date().getTime() - start.getTime()) + "ms");
		}
		LOG.info(this.serviceUtils.createAvgLogStatement(startAll, "Prepared Bitstamp Daily Data Time:"));
	}

	private <T> Collection<T> collectBsQuotes(MyTimeFrame timeFrame, boolean day, Class<T> myClass) {
		Map<String, List<QuoteBs>> multimap;
		try {
			multimap = this.quoteBsRepository
					.findByCreatedAtGreaterThanAndCreatedAtLessThan(timeFrame.begin().getTime(), timeFrame.end().getTime())
					.stream().collect(Collectors.groupingBy(QuoteBs::getPair));
		} catch (Exception e) {
			LOG.warn(day ? "Bitstamp prepare day data failed" : "Bitstamp prepare hour data failed", e);
			return List.of();
		}
		var quoteList = multimap.keySet().stream().map(key -> day ? this.makeBsQuoteDay(key, multimap, timeFrame.begin(),
				timeFrame.end()) : this.makeBsQuoteHour(key, multimap, timeFrame.begin(), timeFrame.end()))
				.filter(Predicate.not(Collection::isEmpty)).flatMap(Collection::stream).toList();
		return quoteList.stream().map(value -> mapToDest(myClass, value)).collect(Collectors.toList());
	}

	private <T> @NonNull T mapToDest(Class<T> myClass, QuoteBs value) {
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

	private Collection<QuoteBs> makeBsQuoteDay(String key, Map<String, List<QuoteBs>> multimap, Calendar begin,
			Calendar end) {
		List<QuoteBs> hourQuotes = new LinkedList<QuoteBs>();

		QuoteBs quoteBs = new QuoteBs(BigDecimal.ZERO, BigDecimal.ZERO, begin.getTime(), BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		quoteBs.setCreatedAt(begin.getTime());
		long count = multimap.get(key).stream().filter(quote -> {
			return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
		}).count();
		if (count > 2) {
			QuoteBs hourQuote = multimap.get(key).stream().filter(quote -> {
				return quote.getCreatedAt().after(begin.getTime()) && quote.getCreatedAt().before(end.getTime());
			}).reduce(quoteBs, (q1, q2) -> avgBsQuote(q1, q2, count));
			hourQuote.setPair(key);
			hourQuotes.add(hourQuote);
		}
		return hourQuotes;
	}

	private Collection<QuoteBs> makeBsQuoteHour(String key, Map<String, List<QuoteBs>> multimap, Calendar begin,
			Calendar end) {
		List<Calendar> hours = this.serviceUtils.createDayHours(begin);
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
			if (count > 2) {
				QuoteBs hourQuote = multimap.get(key).stream().filter(quote -> {
					return quote.getCreatedAt().after(hours.get(x).getTime())
							&& quote.getCreatedAt().before(hours.get(x + 1).getTime());
				}).reduce(quoteBs, (q1, q2) -> avgBsQuote(q1, q2, count));
				hourQuote.setPair(key);
				hourQuotes.add(hourQuote);
			}
		}
		return hourQuotes;
	}

	private QuoteBs avgBsQuote(QuoteBs q1, QuoteBs q2, long count) {
		QuoteBs myQuote = new QuoteBs(this.serviceUtils.avgHourValue(q1.getHigh(), q2.getHigh(), count),
				this.serviceUtils.avgHourValue(q1.getLast(), q2.getLast(), count), q1.getTimestamp(),
				this.serviceUtils.avgHourValue(q1.getBid(), q2.getBid(), count),
				this.serviceUtils.avgHourValue(q1.getVwap(), q2.getVwap(), count),
				this.serviceUtils.avgHourValue(q1.getVolume(), q2.getVolume(), count),
				this.serviceUtils.avgHourValue(q1.getLow(), q2.getLow(), count),
				this.serviceUtils.avgHourValue(q1.getAsk(), q2.getAsk(), count),
				this.serviceUtils.avgHourValue(q1.getOpen(), q2.getOpen(), count));
		myQuote.setCreatedAt(q1.getCreatedAt());
		return myQuote;
	}
}