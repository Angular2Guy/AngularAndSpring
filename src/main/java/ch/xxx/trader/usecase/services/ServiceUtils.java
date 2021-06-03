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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.Tuple;
import ch.xxx.trader.domain.model.Quote;

@Service
public class ServiceUtils {
	private final MyMongoRepository myMongoRepository;
	
	public ServiceUtils(MyMongoRepository myMongoRepository) {
		this.myMongoRepository = myMongoRepository;
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
	
	public Tuple<Calendar, Calendar> createTimeFrame(String colName, Class<? extends Quote> colType, boolean hour) {
		if (!this.myMongoRepository.collectionExists(colName).block()) {
			this.myMongoRepository.createCollection(colName).block();
		}
		Query query = new Query();
		query.with(Sort.by("createdAt").ascending());
		Quote firstQuote = this.myMongoRepository.findOne(query, colType).block();
		query = new Query();
		query.with(Sort.by("createdAt").descending());
		Quote lastHourQuote = this.myMongoRepository.findOne(query, colType, colName).block();
		Calendar globalBeginn = Calendar.getInstance();
		if (lastHourQuote == null) {
			globalBeginn.setTime(firstQuote.getCreatedAt());
		} else {
			globalBeginn.setTime(lastHourQuote.getCreatedAt());
			if(hour) {
				globalBeginn.add(Calendar.HOUR_OF_DAY, 1);
			} else {
				globalBeginn.add(Calendar.DAY_OF_YEAR, 1);
			}
		}

		Calendar begin = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		begin.setTime(globalBeginn.getTime());
		begin.set(Calendar.MINUTE, 0);
		begin.set(Calendar.SECOND, 0);
		end.setTime(begin.getTime());
		end.add(Calendar.DAY_OF_YEAR, 1);
		return new Tuple<Calendar, Calendar>(begin, end);
	}
}
