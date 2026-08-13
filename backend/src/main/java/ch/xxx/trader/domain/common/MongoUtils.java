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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public static final Date buildStartDate(TimeFrame timeFrame) {
		Calendar cal = GregorianCalendar.getInstance();
		return switch (timeFrame) {
		case CURRENT, TODAY -> {
			cal.add(Calendar.DAY_OF_YEAR, -1);
			yield cal.getTime();
		}
		case SEVENDAYS -> {
			cal.add(Calendar.DAY_OF_YEAR, -7);
			yield cal.getTime();
		}
		case THIRTYDAYS -> {
			cal.add(Calendar.DAY_OF_YEAR, -30);
			yield cal.getTime();
		}
		case NINTYDAYS -> {
			cal.add(Calendar.DAY_OF_YEAR, -90);
			yield cal.getTime();
		}
		case Month1 -> {
			cal.add(Calendar.MONTH, -1);
			yield cal.getTime();
		}
		case Month3 -> {
			cal.add(Calendar.MONTH, -3);
			yield cal.getTime();
		}
		case Month6 -> {
			cal.add(Calendar.MONTH, -6);
			yield cal.getTime();
		}
		case Year1 -> {
			cal.add(Calendar.YEAR, -1);
			yield cal.getTime();
		}
		case Year2 -> {
			cal.add(Calendar.YEAR, -2);
			yield cal.getTime();
		}
		case Year5 -> {
			cal.add(Calendar.YEAR, -5);
			yield cal.getTime();
		}
		default -> throw new IllegalArgumentException("Unsupported time frame: " + timeFrame);
		};
	}

	public static final boolean filterEvenMinutes(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getMinute() % 2 == 0;
	}

	public static final boolean filter10Minutes(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getMinute() % 10 == 0;
	}
}
