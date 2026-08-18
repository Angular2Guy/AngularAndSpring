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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.model.dto.QuotePdf;
import ch.xxx.trader.domain.model.entity.Quote;

@Service
public class ServiceUtils {
	private final ReportGenerator reportGenerator;

	public ServiceUtils(ReportGenerator reportGenerator) {
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

	public <T extends Quote> byte[] generatePdf(List<T> quotes, Function<T, QuotePdf> mapping) {
		return this.reportGenerator.generateReport(quotes.stream().map(mapping).toList());
	}
}