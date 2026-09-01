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
package ch.xxx.trader.domain.services;

import ch.xxx.trader.domain.model.entity.QuoteBf;
import ch.xxx.trader.domain.model.entity.QuoteBs;
import ch.xxx.trader.domain.model.entity.QuoteDayBf;
import ch.xxx.trader.domain.model.entity.QuoteDayBs;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface QuoteDayBsRepository {
	QuoteDayBs insert(QuoteDayBs quote);

	<S extends QuoteDayBs> List<S> insert(Iterable<S> quotes);

	@Query("{ 'createdAt': { '$gt': ?0, '$lt': ?1 } }")
	List<QuoteDayBs> findByCreatedAtGreaterThanAndCreatedAtLessThan(Date from, Date to);

	Optional<QuoteDayBs> findFirstByCreatedAtAfterOrderByCreatedAtDesc(Date date);
	List<QuoteDayBs> findByCreatedAtAfterOrderByCreatedAtAsc(Date date);
	List<QuoteDayBs> findByCreatedAtAfterOrderByCreatedAtAsc(Date date, Limit limit);
	Optional<QuoteDayBs> findFirstByOrderByCreatedAtDesc();
	Optional<QuoteDayBs> findFirstByOrderByCreatedAtAsc();
	List<QuoteDayBs> findByPairAndCreatedAtAfterOrderByCreatedAtAsc(String pair, Date startDate);
	List<QuoteDayBs> findByPairAndCreatedAtAfterOrderByCreatedAtAsc(String pair, Date startDate, Limit limit);
	Optional<QuoteDayBs> findFirstByPairAndCreatedAtAfterOrderByCreatedAtDesc(String pair, Date date);
}