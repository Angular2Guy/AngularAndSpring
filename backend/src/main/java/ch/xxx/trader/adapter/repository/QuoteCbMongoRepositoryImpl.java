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
package ch.xxx.trader.adapter.repository;

import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoOperations;

import ch.xxx.trader.domain.model.entity.QuoteCb;

public class QuoteCbMongoRepositoryImpl extends AbstractQuoteRepositoryImpl<QuoteCb>
		implements ch.xxx.trader.domain.services.QuoteRepository<QuoteCb> {

	private final QuoteCbMongoRepository mongoRepository;

	public QuoteCbMongoRepositoryImpl(MongoOperations operations, @Lazy QuoteCbMongoRepository mongoRepository) {
		super(operations, QuoteCb.class);
		this.mongoRepository = mongoRepository;
	}

	@Override
	protected Optional<QuoteCb> findLastQuote() {
		return this.mongoRepository.findFirstByOrderByCreatedAtDesc();
	}

	@Override
	protected Optional<QuoteCb> findFirstQuote() {
		return this.mongoRepository.findFirstByOrderByCreatedAtAsc();
	}
}