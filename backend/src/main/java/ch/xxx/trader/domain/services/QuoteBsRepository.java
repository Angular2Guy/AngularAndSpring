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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import ch.xxx.trader.domain.model.entity.QuoteBf;
import org.springframework.data.domain.Limit;

import ch.xxx.trader.domain.model.entity.QuoteBs;

public interface QuoteBsRepository extends QuoteRepository<QuoteBs>, QuoteMongoRepository<QuoteBs> {
	QuoteBs insert(QuoteBs quote);

	<S extends QuoteBs> List<S> insert(Iterable<S> quotes);

	Optional<QuoteBs> findFirstByPairAndCreatedAtAfterOrderByCreatedAtDesc(String pair, Date date);

	List<QuoteBs> findByPairAndCreatedAtAfterOrderByCreatedAtAsc(String pair, Date date);

	List<QuoteBs> findByPairAndCreatedAtAfterOrderByCreatedAtAsc(String pair, Date date, Limit limit);

	List<QuoteBs> findByCreatedAtGreaterThanAndCreatedAtLessThan(Date from, Date to);
}