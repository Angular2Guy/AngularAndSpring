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
package ch.xxx.trader.domain.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoUtils {

	public enum TimeFrame {
		CURRENT("current"), TODAY("today"), SEVENDAYS("7days"), THIRTYDAYS("30days"), NINTYDAYS("90days"),
		Month1("1month"), Month3("3month"), Month6("6month"), Year1("1year"), Year2("2year"), Year5("5year");

		private TimeFrame(String value) {
			this.value = value;
		}

		private String value;

		public String getValue() {
			return this.value;
		}
	};

	public static final Map<String, TimeFrame> KEY_TO_TIMEFRAME = Collections.unmodifiableMap(new ConcurrentHashMap<>(
			Stream.of(TimeFrame.values()).collect(Collectors.toMap(TimeFrame::getValue, tf -> tf))));

	private static final Query buildQuery(Optional<String> pair, boolean ascending, Optional<Calendar> begin,
			int limit) {
		limit = limit < 5000 ? limit : 5000;
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		Query query = new Query();
		query.allowDiskUse(true);
		query.limit(limit);
		pair.ifPresent(myValue -> query.addCriteria(Criteria.where("pair").is(myValue)));
		begin.ifPresentOrElse(myValue -> query.addCriteria(Criteria.where("createdAt").gt(myValue.getTime())),
				() -> query.addCriteria(Criteria.where("createdAt").gt(cal.getTime())));
		var myValue = ascending ? query.with(Sort.by("createdAt").ascending())
				: query.with(Sort.by("createdAt").descending());
		return query;
	}

	private static final Query buildQuery(Optional<String> pair, boolean ascending, Optional<Calendar> begin) {
		return buildQuery(pair, ascending, begin, 1000);
	}

	public static final Query build90DayQuery(Optional<String> pair) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -90);
		return buildQuery(pair, true, Optional.of(cal));
	}

	public static final Query build30DayQuery(Optional<String> pair) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -30);
		return buildQuery(pair, true, Optional.of(cal));
	}

	public static final Query build7DayQuery(Optional<String> pair) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -7);
		return buildQuery(pair, true, Optional.of(cal));
	}

	public static final Query buildTimeFrameQuery(Optional<String> pair, TimeFrame timeFrame, int limit) {
		Calendar cal = GregorianCalendar.getInstance();
		Query query = switch (timeFrame) {
		case CURRENT -> buildCurrentQuery(pair);
		case TODAY -> buildTodayQuery(pair);
		case SEVENDAYS -> build7DayQuery(pair);
		case THIRTYDAYS -> build30DayQuery(pair);
		case NINTYDAYS -> build90DayQuery(pair);
		case Month1 -> {
			cal.add(Calendar.MONTH, -1);
			yield buildQuery(pair, true, Optional.of(cal));
		}
		case Month3 -> {
			cal.add(Calendar.MONTH, -3);
			yield buildQuery(pair, true, Optional.of(cal));
		}
		case Month6 -> {
			cal.add(Calendar.MONTH, -6);
			yield buildQuery(pair, true, Optional.of(cal));
		}
		case Year1 -> {
			cal.add(Calendar.YEAR, -1);
			yield buildQuery(pair, true, Optional.of(cal));
		}
		case Year2 -> {
			cal.add(Calendar.YEAR, -2);
			yield buildQuery(pair, true, Optional.of(cal), limit);
		}
		case Year5 -> {
			cal.add(Calendar.YEAR, -5);
			yield buildQuery(pair, true, Optional.of(cal), limit);
		}
		default -> new Query();
		};
		return query;
	}

	public static final Query buildTimeFrameQuery(Optional<String> pair, TimeFrame timeFrame) {
		return buildTimeFrameQuery(pair, timeFrame, 1000);
	}

	public static final Query buildTodayQuery(Optional<String> pair) {
		return buildQuery(pair, true, Optional.empty());
	}

	public static final Query buildCurrentQuery(Optional<String> pair) {
		return buildQuery(pair, false, Optional.empty());
	}

	public static final boolean filterEvenMinutes(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getMinute() % 2 == 0;
	}

	public static final boolean filter10Minutes(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getMinute() % 10 == 0;
	}
}
