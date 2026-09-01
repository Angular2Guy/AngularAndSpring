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
    private final QuoteCbRepository quoteCbRepository;
    private final QuoteDayCbRepository quoteDayCbRepository;
    private final QuoteHourCbRepository quoteHourCbRepository;

    public MongoQuoteRepositoryImpl(MongoOperations operations, QuoteBfRepository quoteBfRepository, QuoteHourBfRepository quoteHourBfRepository,
                                    QuoteDayBfRepository quoteDayBfRepository,QuoteBsRepository quoteBsRepository, QuoteCbRepository quoteCbRepository,
                                    QuoteHourBsRepository quoteHourBsRepository, QuoteDayBsRepository quoteDayBsRepository,
                                    QuoteDayCbRepository quoteDayCbRepository, QuoteHourCbRepository quoteHourCbRepository) {
        this.operations = operations;
        this.quoteBfRepository = quoteBfRepository;
        this.quoteHourBfRepository = quoteHourBfRepository;
        this.quoteDayBfRepository = quoteDayBfRepository;
        this.quoteBsRepository = quoteBsRepository;
        this.quoteHourBsRepository = quoteHourBsRepository;
        this.quoteDayBsRepository = quoteDayBsRepository;
        this.quoteDayCbRepository = quoteDayCbRepository;
        this.quoteHourCbRepository = quoteHourCbRepository;
        this.quoteCbRepository = quoteCbRepository;
    }

    @Override
    public <T extends Quote> void ensureIndex(Class<T> entityClass) {
        Index myIndex = new Index(DtoUtils.CREATEDAT, Sort.Direction.DESC).named(entityClass.getSimpleName() + "-" + DtoUtils.CREATEDAT);
        this.operations.indexOps(entityClass).createIndex(myIndex);
    }

    @Override
    public <A extends Quote, B extends Quote> MyTimeFrame createTimeFrame(Class<A> entityClass, Class<B> aggreateEntityClass,
                      boolean hour) {
        if(!this.operations.collectionExists(aggreateEntityClass)) {
            this.operations.createCollection(aggreateEntityClass);
        }
        final Calendar globalBeginn = Calendar.getInstance();
        Optional<B> lastAggregate = getLastAggregate(aggreateEntityClass);
        lastAggregate.ifPresentOrElse(myQuote -> this.calcGlobalBegin(hour, globalBeginn, myQuote), () -> {
            Optional<A> firstQuote = getFirstQuote(entityClass, aggreateEntityClass);
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

    @SuppressWarnings("unchecked")
    private <A extends Quote, B extends Quote> Optional<A> getFirstQuote(Class<A> entityClass, Class<B> aggreateEntityClass) {
        var result = (Optional<A>) switch (entityClass) {
            case Class<?> c when c == QuoteBf.class -> this.quoteBfRepository;
            case Class<?> c when c == QuoteBs.class -> this.quoteBsRepository;
            case Class<?> c when c == QuoteCb.class -> this.quoteCbRepository;
            default -> throw new IllegalStateException("Unexpected value: " + aggreateEntityClass.getSimpleName());
        };
        return result;
    }

    @SuppressWarnings("unchecked")
    private <B extends Quote> Optional<B> getLastAggregate(Class<B> aggreateEntityClass) {
        Optional<B> lastAggregate = (Optional<B>) switch (aggreateEntityClass) {
            case Class<?> c when c == QuoteDayBf.class -> quoteDayBfRepository.findFirstByOrderByCreatedAtDesc();
            case Class<?> c when c == QuoteHourBf.class -> quoteHourBfRepository.findFirstByOrderByCreatedAtDesc();
            case Class<?> c when c == QuoteDayBs.class -> quoteDayBsRepository.findFirstByOrderByCreatedAtDesc();
            case Class<?> c when c == QuoteHourBs.class -> quoteHourBsRepository.findFirstByOrderByCreatedAtDesc();
            case Class<?> c when c == QuoteHourCb.class -> quoteHourCbRepository.findFirstByOrderByCreatedAtDesc();
            case Class<?> c when c == QuoteDayCb.class -> quoteDayCbRepository.findFirstByOrderByCreatedAtDesc();
            default -> throw new IllegalStateException("Unexpected value: " + aggreateEntityClass.getSimpleName());
        };
        return lastAggregate;
    }

    private void calcGlobalBegin(boolean hour, Calendar globalBeginn, Quote myQuote) {
        globalBeginn.setTime(myQuote.getCreatedAt());
        if (hour) {
            globalBeginn.add(Calendar.HOUR_OF_DAY, 1);
        } else {
            globalBeginn.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> tfQuotes(MongoUtils.TimeFrame myTimeFrame, String pair, Class<T> myClass) {
        var result = (List<T>) switch (myTimeFrame) {
            case TODAY,CURRENT -> switch(myClass) {
                case Class<?> c when c == QuoteBf.class -> this.quoteBfRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                                MongoUtils.buildStartDate(myTimeFrame)).stream()
                        .filter(q -> MongoUtils.filterEvenMinutes(q.getCreatedAt())).toList();
                case Class<?> c when c == QuoteBs.class -> this.quoteBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                                MongoUtils.buildStartDate(myTimeFrame)).stream()
                        .filter(q -> MongoUtils.filterEvenMinutes(q.getCreatedAt())).toList();
                default -> List.of();
             };
            case SEVENDAYS -> switch (myClass) {
                case Class<?> c when c == QuoteHourBf.class -> this.quoteHourBfRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                        MongoUtils.buildStartDate(myTimeFrame), Limit.of(1000));
                case Class<?> c when c == QuoteHourBs.class -> this.quoteHourBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                        MongoUtils.buildStartDate(myTimeFrame), Limit.of(1000));
                default -> List.of();
            };
            case THIRTYDAYS -> getDayList(pair, myClass, myTimeFrame);
            case NINTYDAYS -> getDayList(pair, myClass, myTimeFrame);
            case Month6 -> getDayList(pair, myClass, myTimeFrame);
            case Year1 -> getDayList(pair, myClass, myTimeFrame);
            default -> List.of();
        };
        return result;
    }

    private <T> List<?> getDayList(String pair, Class<T> myClass, MongoUtils.TimeFrame myTimeFrame) {
        return switch (myClass) {
            case Class<?> c when c == QuoteDayBf.class ->
                    this.quoteDayBfRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                            MongoUtils.buildStartDate(myTimeFrame), Limit.of(1000));
            case Class<?> c when c == QuoteDayBs.class ->
                    this.quoteDayBsRepository.findByPairAndCreatedAtAfterOrderByCreatedAtAsc(pair.toLowerCase(),
                            MongoUtils.buildStartDate(myTimeFrame), Limit.of(1000));
            default -> List.of();
        };
    }
}
