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

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.model.entity.*;
import ch.xxx.trader.domain.services.*;
import ch.xxx.trader.usecase.common.DtoUtils;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MongoQuoteRepositoryImpl implements MongoQuoteRepository {
    private final MongoOperations operations;
    private final QuoteBfRepository quoteBfRepository;
    private final QuoteHourBfRepository quoteHourBfRepository;
    private final QuoteDayBfRepository quoteDayBfRepository;
    private final QuoteBsRepository quoteBsRepository;
    private final QuoteHourBsRepository quoteHourBsRepository;
    private final QuoteDayBsRepository quoteDayBsRepository;

    public MongoQuoteRepositoryImpl(MongoOperations operations, QuoteBfRepository quoteBfRepository, QuoteHourBfRepository quoteHourBfRepository,
                                    QuoteDayBfRepository quoteDayBfRepository,QuoteBsRepository quoteBsRepository,QuoteHourBsRepository quoteHourBsRepository,
                                    QuoteDayBsRepository quoteDayBsRepository) {
        this.operations = operations;
        this.quoteBfRepository = quoteBfRepository;
        this.quoteHourBfRepository = quoteHourBfRepository;
        this.quoteDayBfRepository = quoteDayBfRepository;
        this.quoteBsRepository = quoteBsRepository;
        this.quoteHourBsRepository = quoteHourBsRepository;
        this.quoteDayBsRepository = quoteDayBsRepository;
    }

    @Override
    public <T extends Quote> void ensureIndex(Class<T> entityClass) {
        Index myIndex = new Index(DtoUtils.CREATEDAT, Sort.Direction.DESC).named(entityClass.getSimpleName() + "-" + DtoUtils.CREATEDAT);
        this.operations.indexOps(entityClass).createIndex(myIndex);
    }

    @Override
    public <A extends Quote, B extends Quote> MyTimeFrame createTimeFrame(Class<A> entityClass, Class<B> aggreateEntityClass,
                      boolean hour, QuoteRepository<A> quoteRepository, QuoteRepository<B> aggreateQuoteRepository) {
        if(!this.operations.collectionExists(aggreateEntityClass)) {
            this.operations.createCollection(aggreateEntityClass);
        }
        final Calendar globalBeginn = Calendar.getInstance();
        Optional<B> lastAggregate = aggreateQuoteRepository.findFirstByOrderByCreatedAtDesc();
        lastAggregate.ifPresentOrElse(myQuote -> this.calcGlobalBegin(hour, globalBeginn, myQuote), () -> {
            Optional<A> firstQuote = quoteRepository.findFirstByOrderByCreatedAtAsc();
            globalBeginn.setTime(firstQuote.map(Quote::getCreatedAt).orElse(
                    Date.from(java.time.LocalDate.now().atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())));
        });

        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        begin.setTime(globalBeginn.getTime());
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        end.setTime(begin.getTime());
        end.add(Calendar.DAY_OF_YEAR, 1);
        return new MyTimeFrame(begin, end);
    }

    private void calcGlobalBegin(boolean hour, Calendar globalBeginn, Quote myQuote) {
        globalBeginn.setTime(myQuote.getCreatedAt());
        if (hour) {
            globalBeginn.add(Calendar.HOUR_OF_DAY, 1);
        } else {
            globalBeginn.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private <T extends Quote> QuotePairRepository<T> findRepository(T instance) {
        return (QuotePairRepository<T>) switch (instance) {
            case QuoteBs q -> this.quoteBsRepository;
            case QuoteDayBs q -> this.quoteDayBsRepository;
            case QuoteHourBs q -> this.quoteHourBsRepository;
            case QuoteBf q  -> this.quoteBfRepository;
            case QuoteDayBf q  -> this.quoteDayBfRepository;
            case QuoteHourBf q  -> this.quoteHourBfRepository;
            default -> throw new IllegalStateException("Unexpected value: " + instance.getClass());
        };
    }

    public <T extends Quote> List<T> tfQuotes(String timeFrame, String pair, T instance) {
        MongoUtils.TimeFrame myTimeFrame = MongoUtils.KEY_TO_TIMEFRAME.get(timeFrame.toLowerCase());
        var myRepository = this.findRepository(instance);
        return switch (myTimeFrame) {
            case TODAY -> myRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                            MongoUtils.buildStartDate(MongoUtils.TimeFrame.TODAY)).stream()
                    .filter(q -> MongoUtils.filterEvenMinutes(q.getCreatedAt())).toList();
            case SEVENDAYS -> myRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                    MongoUtils.buildStartDate(MongoUtils.TimeFrame.SEVENDAYS), Limit.of(1000));
            case THIRTYDAYS -> myRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                    MongoUtils.buildStartDate(MongoUtils.TimeFrame.THIRTYDAYS), Limit.of(1000));
            case NINTYDAYS -> myRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                    MongoUtils.buildStartDate(MongoUtils.TimeFrame.NINTYDAYS), Limit.of(1000));
            case Month6 -> myRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                    MongoUtils.buildStartDate(MongoUtils.TimeFrame.Month6), Limit.of(1000));
            case Year1 -> myRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                    MongoUtils.buildStartDate(MongoUtils.TimeFrame.Year1), Limit.of(1000));
            default -> List.of();
        };
    }
}
