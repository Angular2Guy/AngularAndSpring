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
import java.math.MathContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.common.MongoUtils;
import ch.xxx.trader.domain.common.MongoUtils.TimeFrame;
import ch.xxx.trader.domain.model.dto.CommonStatisticsDto;
import ch.xxx.trader.domain.model.dto.RangeDto;
import ch.xxx.trader.domain.model.dto.StatisticsCommon.CoinExchange;
import ch.xxx.trader.domain.model.dto.StatisticsCommon.StatisticsCurrPair;
import ch.xxx.trader.domain.model.entity.Quote;
import ch.xxx.trader.domain.model.entity.QuoteBf;
import ch.xxx.trader.domain.model.entity.QuoteBs;
import reactor.core.publisher.Mono;

@Service
public class StatisticService {
	private final MyMongoRepository myMongoRepository;

	public StatisticService(MyMongoRepository myMongoRepository) {
		this.myMongoRepository = myMongoRepository;
	}

	public Mono<CommonStatisticsDto> getCommonStatistics(StatisticsCurrPair currPair, CoinExchange coinExchange) {
		Mono<CommonStatisticsDto> result = CoinExchange.Bitfinex.equals(coinExchange)
				? this.createBitfinexStatistics(currPair)
				: this.createBitStampStatistics(currPair);
		return result;
	}

	private Mono<CommonStatisticsDto> createBitStampStatistics(StatisticsCurrPair currPair) {
		Mono<CommonStatisticsDto> result = this.myMongoRepository
				.find(MongoUtils.buildTimeFrameQuery(Optional.of(currPair.getBitStampKey()), TimeFrame.Year5),
						QuoteBs.class, BitstampService.BS_DAY_COL)
				.collectList().flatMap(myList -> this.calcStatistics(myList));
		return result;
	}

	private <T extends Quote> Mono<CommonStatisticsDto> calcStatistics(List<T> quotes) {
		CommonStatisticsDto commonStatisticsDto = new CommonStatisticsDto();
		calcStatistics1Month(quotes, commonStatisticsDto);		
		calcStatistics3Month(quotes, commonStatisticsDto);			
		calcStatistics6Months(quotes, commonStatisticsDto);
		calcStatistics1Year(quotes, commonStatisticsDto);
		calcStatistics2Years(quotes, commonStatisticsDto);
		calcStatistics5Years(quotes, commonStatisticsDto);
		return Mono.just(commonStatisticsDto);
	}

	<T extends Quote> void calcStatistics5Years(List<T> quotes, CommonStatisticsDto commonStatisticsDto) {
		List<T> quotes5Year = quotes.stream()
				.filter(myQuote -> myQuote.getCreatedAt().after(this.createBeforeDate(0, 5))).toList();
		commonStatisticsDto.setRange5Year(
				new RangeDto(this.getMinMaxValue(quotes5Year, false), this.getMinMaxValue(quotes5Year, true)));
		commonStatisticsDto.setPerformance5Year(this.calcPerformance(quotes5Year));
		commonStatisticsDto.setAvgVolume5Year(this.calcAvgVolume(quotes5Year));
		commonStatisticsDto.setVolatility5Year(this.calcVolatility(quotes5Year));
	}

	<T extends Quote> void calcStatistics2Years(List<T> quotes, CommonStatisticsDto commonStatisticsDto) {
		List<T> quotes2Year = quotes.stream()
				.filter(myQuote -> myQuote.getCreatedAt().after(this.createBeforeDate(0, 2))).toList();
		commonStatisticsDto.setRange2Year(
				new RangeDto(this.getMinMaxValue(quotes2Year, false), this.getMinMaxValue(quotes2Year, true)));
		commonStatisticsDto.setPerformance2Year(this.calcPerformance(quotes2Year));
		commonStatisticsDto.setAvgVolume2Year(this.calcAvgVolume(quotes2Year));
		commonStatisticsDto.setVolatility2Year(this.calcVolatility(quotes2Year));
	}

	<T extends Quote> void calcStatistics1Year(List<T> quotes, CommonStatisticsDto commonStatisticsDto) {
		List<T> quotes1Year = quotes.stream()
				.filter(myQuote -> myQuote.getCreatedAt().after(this.createBeforeDate(0, 1))).toList();
		commonStatisticsDto.setRange1Year(
				new RangeDto(this.getMinMaxValue(quotes1Year, false), this.getMinMaxValue(quotes1Year, true)));
		commonStatisticsDto.setPerformance1Year(this.calcPerformance(quotes1Year));
		commonStatisticsDto.setAvgVolume1Year(this.calcAvgVolume(quotes1Year));
		commonStatisticsDto.setVolatility1Year(this.calcVolatility(quotes1Year));
	}

	<T extends Quote> void calcStatistics6Months(List<T> quotes, CommonStatisticsDto commonStatisticsDto) {
		List<T> quotes6Month = quotes.stream()
				.filter(myQuote -> myQuote.getCreatedAt().after(this.createBeforeDate(6, 0))).toList();
		commonStatisticsDto.setPerformance6Month(this.calcPerformance(quotes6Month));
		commonStatisticsDto.setAvgVolume6Month(this.calcAvgVolume(quotes6Month));
		commonStatisticsDto.setVolatility6Month(this.calcVolatility(quotes6Month));
		commonStatisticsDto.setRange6Month(
				new RangeDto(this.getMinMaxValue(quotes6Month, false), this.getMinMaxValue(quotes6Month, true)));
	}

	<T extends Quote> void calcStatistics3Month(List<T> quotes, CommonStatisticsDto commonStatisticsDto) {
		List<T> quotes3Month = quotes.stream()
				.filter(myQuote -> myQuote.getCreatedAt().after(this.createBeforeDate(3, 0))).toList();
		commonStatisticsDto.setRange3Month(
				new RangeDto(this.getMinMaxValue(quotes3Month, false), this.getMinMaxValue(quotes3Month, true)));
		commonStatisticsDto.setPerformance3Month(this.calcPerformance(quotes3Month));
		commonStatisticsDto.setAvgVolume3Month(this.calcAvgVolume(quotes3Month));
		commonStatisticsDto.setVolatility3Month(this.calcVolatility(quotes3Month));
	}

	<T extends Quote> void calcStatistics1Month(List<T> quotes, CommonStatisticsDto commonStatisticsDto) {
		List<T> quotes1Month = quotes.stream()
				.filter(myQuote -> myQuote.getCreatedAt().after(this.createBeforeDate(1, 0))).toList();
		commonStatisticsDto.setRange1Month(
				new RangeDto(this.getMinMaxValue(quotes1Month, false), this.getMinMaxValue(quotes1Month, true)));
		commonStatisticsDto.setAvgVolume1Month(this.calcAvgVolume(quotes1Month));
		commonStatisticsDto.setPerformance1Month(this.calcPerformance(quotes1Month));
		commonStatisticsDto.setVolatility1Month(this.calcVolatility(quotes1Month));
	}

	private <T extends Quote> BigDecimal calcVolatility(List<T> quotes) {
		final BigDecimal average = quotes.size() < 2 ? BigDecimal.ZERO
				: quotes.stream().map(myQuote -> this.getLastValue(myQuote))
						.reduce(BigDecimal.ZERO, (acc, value) -> acc.add(value))
						.divide(BigDecimal.valueOf(quotes.size()), MathContext.DECIMAL128);
		BigDecimal variance = quotes.size() < 2 ? BigDecimal.ZERO : quotes.stream().map(myQuote -> this.getLastValue(myQuote)).map(lastValue -> lastValue.subtract(average))
				.map(avgDifference -> avgDifference.multiply(avgDifference))
				.reduce(BigDecimal.ZERO, (acc, value) -> acc.add(value)).divide(BigDecimal.valueOf(quotes.size()), MathContext.DECIMAL128);
		BigDecimal volatility = variance.sqrt(MathContext.DECIMAL64);
		return volatility;
	}

	private <T extends Quote> BigDecimal calcAvgVolume(List<T> quotes) {
		return quotes.size() < 2 ? BigDecimal.ZERO
				: quotes.stream().map(myQuote -> this.getVolume(myQuote))
						.reduce(BigDecimal.ZERO, (acc, value) -> acc.add(value))
						.divide(BigDecimal.valueOf(quotes.size()), MathContext.DECIMAL128);
	}

	private <T extends Quote> BigDecimal getVolume(T myQuote) {
		return myQuote instanceof QuoteBs ? ((QuoteBs) myQuote).getLast() : ((QuoteBf) myQuote).getLast_price();
	}

	private <T extends Quote> Double calcPerformance(List<T> quotes) {
		return quotes.size() < 2 ? 0.0
				: (this.getLastValue(quotes.get(0)).doubleValue()
						/ this.getLastValue(quotes.get(quotes.size()-1)).doubleValue() - 1) * 100;
	}

	private <T extends Quote> BigDecimal getMinMaxValue(List<T> quotes, boolean max) {
		Stream<BigDecimal> valueStream = quotes.stream().map(myQuote -> this.getLastValue(myQuote));
		return max ? valueStream.max(BigDecimal::compareTo).orElse(BigDecimal.ZERO)
				: valueStream.min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
	}

	private <T extends Quote> BigDecimal getLastValue(T myQuote) {
		return myQuote instanceof QuoteBs ? ((QuoteBs) myQuote).getLast() : ((QuoteBf) myQuote).getLast_price();
	}

	private Date createBeforeDate(int months, int years) {
		return Date.from(LocalDate.now().minusMonths(months).minusYears(years).atStartOfDay()
				.atZone(ZoneId.systemDefault()).toInstant());
	}

	private Mono<CommonStatisticsDto> createBitfinexStatistics(StatisticsCurrPair currPair) {
		Mono<CommonStatisticsDto> result = this.myMongoRepository
				.find(MongoUtils.buildTimeFrameQuery(Optional.of(currPair.getBitfinexKey()), TimeFrame.Year5),
						QuoteBf.class, BitfinexService.BF_DAY_COL)
				.collectList().flatMap(myList -> this.calcStatistics(myList));
		return result;
	}
}
