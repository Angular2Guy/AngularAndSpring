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
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.common.MongoUtils.TimeFrame;
import ch.xxx.trader.domain.model.dto.QuotePdf;
import ch.xxx.trader.domain.model.entity.MyMongoRepository;
import ch.xxx.trader.domain.model.entity.Quote;
import ch.xxx.trader.usecase.common.DtoUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ServiceUtils {
	public record MyTimeFrame(Calendar begin, Calendar end) {
	}

	private final MyMongoRepository myMongoRepository;
	private final ReportGenerator reportGenerator;

	public ServiceUtils(MyMongoRepository myMongoRepository, ReportGenerator reportGenerator) {
		this.myMongoRepository = myMongoRepository;
		this.reportGenerator = reportGenerator;
	}

	public List<Calendar> createDayHours(Calendar begin) {
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

	public BigDecimal avgHourValue(BigDecimal v1, BigDecimal v2, long count) {
		return v1.add(v2 == null ? BigDecimal.ZERO
				: v2.divide(BigDecimal.valueOf(count == 0 ? 1 : count), 10, RoundingMode.HALF_UP));
	}

	public List<String> showThreads() {
		List<String> logs = new LinkedList<>();
		Set<Thread> threads = Thread.getAllStackTraces().keySet();
		logs.add(String.format("%-15s \t %-15s \t %-15s \t %s \t %s \t %s", "Name", "State", "Priority", "isDaemon",
				"TheadGroup", "ThreadGroupActive"));
		for (Thread t : threads) {
			logs.add(String.format("%-15s \t %-15s \t %-15d \t %s \t %s \t %d", t.getName(), t.getState(),
					t.getPriority(), t.isDaemon(), t.getThreadGroup().getName(), t.getThreadGroup().activeCount()));
		}
		return logs;
	}

	public String createAvgLogStatement(LocalDateTime start, String statementStart) {
		Duration myDuration = Duration.between(start.atZone(ZoneId.systemDefault()).toInstant(),
				LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
		long millis = (myDuration.toSeconds() < 1 ? myDuration.toMillis()
				: (myDuration.toMillis() - myDuration.toSeconds() * 1000));
		return String.format("%s %d.%d seconds.", statementStart, myDuration.toSeconds(), millis);
	}

	public <T extends Quote> Flux<T> tfQuotes(String timeFrame, String pair, Class<T> quoteClass, String hourCol,
			String dayCol) {
		TimeFrame myTimeFrame = MongoUtils.KEY_TO_TIMEFRAME.get(timeFrame);
		Flux<T> result = switch (myTimeFrame) {
		case MongoUtils.TimeFrame.TODAY -> {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			yield this.myMongoRepository.find(query, quoteClass)
					.filter(q -> MongoUtils.filterEvenMinutes(q.getCreatedAt()));
		}
		case MongoUtils.TimeFrame.SEVENDAYS -> {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			yield this.myMongoRepository.find(query, quoteClass, hourCol);
		}
		case MongoUtils.TimeFrame.THIRTYDAYS -> {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			yield this.myMongoRepository.find(query, quoteClass, dayCol);
		}
		case MongoUtils.TimeFrame.NINTYDAYS -> {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			yield this.myMongoRepository.find(query, quoteClass, dayCol);
		}
		case MongoUtils.TimeFrame.Month6 -> {
			Query query = MongoUtils.buildTimeFrameQuery(Optional.of(pair), myTimeFrame);
			yield this.myMongoRepository.find(query, quoteClass, dayCol);
		}
		case MongoUtils.TimeFrame.Year1 -> {
			Query query = MongoUtils.buildTimeFrameQuery(Optional.of(pair), myTimeFrame);
			yield this.myMongoRepository.find(query, quoteClass, dayCol);
		}
		default -> Flux.empty();
		};
		return result;
	}

	public <T extends Quote> Mono<byte[]> pdfReport(String timeFrame, String pair, Class<T> quoteClass, String hourCol,
			String dayCol, Function<T, QuotePdf> mapping) {
		TimeFrame myTimeFrame = MongoUtils.KEY_TO_TIMEFRAME.get(timeFrame);
		Mono<byte[]> result = switch (myTimeFrame) {
		case MongoUtils.TimeFrame.TODAY -> {
			Query query = MongoUtils.buildTodayQuery(Optional.of(pair));
			yield this.reportGenerator.generateReport(this.myMongoRepository.find(query, quoteClass)
					.filter(myQuote -> MongoUtils.filter10Minutes(myQuote.getCreatedAt())).map(mapping));
		}
		case MongoUtils.TimeFrame.SEVENDAYS -> {
			Query query = MongoUtils.build7DayQuery(Optional.of(pair));
			yield this.reportGenerator
					.generateReport(this.myMongoRepository.find(query, quoteClass, hourCol).map(mapping));
		}
		case MongoUtils.TimeFrame.THIRTYDAYS -> {
			Query query = MongoUtils.build30DayQuery(Optional.of(pair));
			yield this.reportGenerator
					.generateReport(this.myMongoRepository.find(query, quoteClass, dayCol).map(mapping));
		}
		case MongoUtils.TimeFrame.NINTYDAYS -> {
			Query query = MongoUtils.build90DayQuery(Optional.of(pair));
			yield this.reportGenerator
					.generateReport(this.myMongoRepository.find(query, quoteClass, dayCol).map(mapping));
		}
		case MongoUtils.TimeFrame.Month6 -> {
			Query query = MongoUtils.buildTimeFrameQuery(Optional.of(pair), myTimeFrame);
			yield this.reportGenerator
					.generateReport(this.myMongoRepository.find(query, quoteClass, dayCol).map(mapping));
		}
		case MongoUtils.TimeFrame.Year1 -> {
			Query query = MongoUtils.buildTimeFrameQuery(Optional.of(pair), myTimeFrame);
			yield this.reportGenerator
					.generateReport(this.myMongoRepository.find(query, quoteClass, dayCol).map(mapping));
		}
		default -> Mono.empty();
		};
		return result;
	}

	public MyTimeFrame createTimeFrame(String colName, Class<? extends Quote> colType, boolean hour) {
		if (!this.myMongoRepository.collectionExists(colName).block()) {
			this.myMongoRepository.createCollection(colName).block();
		}
		Query query = new Query().with(Sort.by(DtoUtils.CREATEDAT).ascending());
		Optional<? extends Quote> firstQuote = Optional
				.ofNullable(this.myMongoRepository.findOne(query, colType).block());
		query = new Query().with(Sort.by(DtoUtils.CREATEDAT).descending());
		final Calendar globalBeginn = Calendar.getInstance();
		Optional.ofNullable(this.myMongoRepository.findOne(query, colType, colName).block())
				.ifPresentOrElse(myQuote -> {
					this.calcGlobalBegin(hour, globalBeginn, myQuote);
				}, () -> {
					globalBeginn.setTime(firstQuote.stream().map(myQuote -> myQuote.getCreatedAt()).findFirst().orElse(
							Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())));
				});

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(globalBeginn.getTime());
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		end.setTime(begin.getTime());
		end.add(Calendar.DAY_OF_YEAR, 1);
		return new MyTimeFrame(begin, end);
	}

	private void calcGlobalBegin(boolean hour, Calendar globalBeginn, Quote myQuote) {
		globalBeginn.setTime(myQuote.getCreatedAt());
		if (hour) {
			globalBeginn.add(Calendar.HOUR_OF_DAY, 1);
		} else {
			globalBeginn.add(Calendar.DAY_OF_YEAR, 1);
		}
	}
}
