package ch.xxx.trader.usecase.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import ch.xxx.trader.domain.common.Tuple;
import ch.xxx.trader.domain.dtos.Quote;

public class ServiceUtils {
	public static List<Calendar> createDayHours(Calendar begin) {
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
	
	public static BigDecimal avgHourValue(BigDecimal v1, BigDecimal v2, long count) {
		return v1.add(v2 == null ? BigDecimal.ZERO
				: v2.divide(BigDecimal.valueOf(count == 0 ? 1 : count), 10, RoundingMode.HALF_UP));
	}
	
	public static Tuple<Calendar, Calendar> createTimeFrame(ReactiveMongoOperations operations, String colName, Class<? extends Quote> colType, boolean hour) {
		if (!operations.collectionExists(colName).block()) {
			operations.createCollection(colName).block();
		}
		Query query = new Query();
		query.with(Sort.by("createdAt").ascending());
		Quote firstQuote = operations.findOne(query, colType).block();
		query = new Query();
		query.with(Sort.by("createdAt").descending());
		Quote lastHourQuote = operations.findOne(query, colType, colName).block();
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
