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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.model.entity.MyMongoRepository;
import ch.xxx.trader.domain.model.entity.QuoteBf;
import ch.xxx.trader.domain.services.MyOrderBookClient;
import ch.xxx.trader.usecase.common.DtoUtils;
import ch.xxx.trader.usecase.mappers.ReportMapper;
import ch.xxx.trader.usecase.services.ServiceUtils.MyTimeFrame;

@Service
public class BitfinexService {
	private static final Logger LOG = LoggerFactory.getLogger(BitfinexService.class);
	public static final String BF_HOUR_COL = "quoteBfHour";
	public static final String BF_DAY_COL = "quoteBfDay";
	public static volatile boolean singleInstanceLock = false;
	private final MyOrderBookClient orderBookClient;
	private final ReportMapper reportMapper;
	private final MyMongoRepository myMongoRepository;
	private final ServiceUtils serviceUtils;
	@Value("${single.instance.deployment:false}")
	private boolean singleInstanceDeployment;

	public BitfinexService(ServiceUtils serviceUtils, MyOrderBookClient orderBookClient, ReportMapper reportMapper,
			MyMongoRepository myMongoRepository) {
		this.orderBookClient = orderBookClient;
		this.reportMapper = reportMapper;
		this.myMongoRepository = myMongoRepository;
		this.serviceUtils = serviceUtils;
	}

	public String getOrderbook(String currpair) {
		return this.orderBookClient.getOrderbookBitfinex(currpair);
	}

	public QuoteBf insertQuote(QuoteBf quote) {
		return this.myMongoRepository.insert(quote);
	}

	public Optional<QuoteBf> currentQuote(String pair) {
		Query query = MongoUtils.buildCurrentQuery(Optional.of(pair));
		return this.myMongoRepository.findOne(query, QuoteBf.class);
	}

	public List<QuoteBf> tfQuotes(String timeFrame, String pair) {
		return this.serviceUtils.tfQuotes(timeFrame, pair, QuoteBf.class, BF_HOUR_COL, BF_DAY_COL);
	}

	public byte[] pdfReport(String timeFrame, String pair) {
		return this.serviceUtils.pdfReport(timeFrame, pair, QuoteBf.class, BF_HOUR_COL, BF_DAY_COL,
				this.reportMapper::convert);
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
			this.myMongoRepository.ensureIndex(BF_HOUR_COL, DtoUtils.CREATEDAT);
		} catch (Exception e) {
			LOG.info("ensureIndex(" + BF_HOUR_COL + ") failed.", e);
		}
		try {
			this.myMongoRepository.ensureIndex(BF_DAY_COL, DtoUtils.CREATEDAT);
		} catch (Exception e) {
			LOG.info("ensureIndex(" + BF_DAY_COL + ") failed.", e);
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
		MyTimeFrame timeFrame = this.serviceUtils.createTimeFrame(BF_HOUR_COL, QuoteBf.class, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(
					Criteria.where(DtoUtils.CREATEDAT).gt(timeFrame.begin().getTime()).lt(timeFrame.end().getTime()));
			// Bitfinex
			Collection<QuoteBf> collectBf = this.collectBfQuotes(query, timeFrame, false);
			if (!collectBf.isEmpty()) {
				try {
					this.myMongoRepository.insertAll(collectBf, BF_HOUR_COL);
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
		MyTimeFrame timeFrame = this.serviceUtils.createTimeFrame(BF_DAY_COL, QuoteBf.class, false);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Calendar now = Calendar.getInstance();
		now.setTime(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		while (timeFrame.end().before(now)) {
			Date start = new Date();
			Query query = new Query();
			query.addCriteria(
					Criteria.where(DtoUtils.CREATEDAT).gt(timeFrame.begin().getTime()).lt(timeFrame.end().getTime()));
			// Bitfinex
			Collection<QuoteBf> collectBf = this.collectBfQuotes(query, timeFrame, true);
			if (!collectBf.isEmpty()) {
				try {
					this.myMongoRepository.insertAll(collectBf, BF_DAY_COL);
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

	private Collection<QuoteBf> collectBfQuotes(Query query, MyTimeFrame timeFrame, boolean day) {
		Map<String, List<QuoteBf>> multimap;
		try {
			multimap = this.myMongoRepository.find(query, QuoteBf.class).stream()
					.collect(Collectors.groupingBy(QuoteBf::getPair));
		} catch (Exception e) {
			LOG.warn(day ? "Bitfinex prepare day data failed" : "Bitfinex prepare hour data failed", e);
			return List.of();
		}
		return multimap.keySet().stream().map(key -> day ? this.makeBfQuoteDay(key, multimap, timeFrame.begin(),
				timeFrame.end()) : this.makeBfQuoteHour(key, multimap, timeFrame.begin(), timeFrame.end()))
				.filter(Predicate.not(Collection::isEmpty)).flatMap(Collection::stream).toList();
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