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
package ch.xxx.trader.adapter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.trader.domain.model.dto.CommonStatisticsDto;
import ch.xxx.trader.domain.model.dto.StatisticsCommon.CoinExchange;
import ch.xxx.trader.domain.model.dto.StatisticsCommon.StatisticsCurrPair;
import ch.xxx.trader.usecase.services.StatisticService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);
	private final StatisticService statisticService;
	
	public StatisticsController(StatisticService statisticService) {
		this.statisticService = statisticService;
	}

	@GetMapping("/overview/{coinExchange}/{currPair}")
	public Mono<CommonStatisticsDto> getOverview(@PathVariable StatisticsCurrPair currPair, @PathVariable CoinExchange coinExchange) {		
		return this.statisticService.getCommonStatistics(currPair, coinExchange);
	}
}
