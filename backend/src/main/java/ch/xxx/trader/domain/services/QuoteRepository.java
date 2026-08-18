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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort.Direction;

import ch.xxx.trader.domain.model.entity.Quote;

public interface QuoteRepository<T extends Quote> {
	List<T> findQuotesSince(String collectionName, Date since);

	List<T> findQuotesSince(String collectionName, Date since, String pair, int limit);

	Optional<T> findFirst(String collectionName, Direction direction);

	void insertAll(String collectionName, Collection<? extends T> quotes);

	boolean collectionExists(String collectionName);

	void createCollection(String collectionName);

	void ensureIndex(String collectionName);

	MyTimeFrame createTimeFrame(String collectionName, boolean hour);
}