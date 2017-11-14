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
package ch.xxx.trader;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoUtils {
	private static final Query buildQuery(Optional<String> pair, boolean ascending) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		Query query = new Query();
		if (pair.isPresent()) {
			query.addCriteria(Criteria.where("pair").is(pair.get()));
		}
		query.addCriteria(Criteria.where("createdAt").gt(cal.getTime()));
		if (ascending) {
			query.with(Sort.by("createdAt").ascending());
		} else {
			query.with(Sort.by("createdAt").descending());
		}
		return query;
	}

	public static final Query buildTodayQuery(Optional<String> pair) {
		return buildQuery(pair, true);
	}

	public static final Query buildCurrentQuery(Optional<String> pair) {
		return buildQuery(pair, false);
	}	
	
	public static final boolean filterEvenMinutes(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).getMinute() % 2 == 0;
	}
}
