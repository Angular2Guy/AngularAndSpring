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
package ch.xxx.trader.adapter.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.xxx.trader.usecase.services.BitfinexService;
import ch.xxx.trader.usecase.services.BitstampService;
import ch.xxx.trader.usecase.services.CoinbaseService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class PrepareDataTask {
	private static final Logger LOG = LoggerFactory.getLogger(PrepareDataTask.class);
	private final BitstampService bitstampService;
	private final BitfinexService bitfinexService;
	private final CoinbaseService coinbaseService;

	public PrepareDataTask(BitstampService bitstampService, BitfinexService bitfinexService,
			CoinbaseService coinbaseService) {
		this.bitstampService = bitstampService;
		this.bitfinexService = bitfinexService;
		this.coinbaseService = coinbaseService;
	}

	@Async
	@Scheduled(cron = "0 5 0,12 ? * ?")
	@SchedulerLock(name = "bitstamp_avg_scheduledTask", lockAtLeastFor = "PT10H", lockAtMostFor = "PT11H")
	public void createBsAvg() {
		try {
			this.bitstampService.createBsAvg();
		} catch (Exception e) {
			LOG.warn("createBsAvg() failed.", e);
		} finally {
			BitstampService.singleInstanceLock = false;
		}
	}

	@Async
	@Scheduled(cron = "0 45 0,12 ? * ?")
	@SchedulerLock(name = "bitfinex_avg_scheduledTask", lockAtLeastFor = "PT10H", lockAtMostFor = "PT11H")
	public void createBfAvg() {
		try {
			this.bitfinexService.createBfAvg();
		} catch (Exception e) {
			LOG.warn("createBfAvg() failed.", e);
		} finally {
			BitfinexService.singleInstanceLock = false;
		}
	}

	@Async
	@Scheduled(cron = "0 10 2,14 ? * ?")
	@SchedulerLock(name = "coinbase_avg_scheduledTask", lockAtLeastFor = "PT10H", lockAtMostFor = "PT11H")
	public void createCbAvg() {
		try {
			this.coinbaseService.createCbAvg();
		} catch (Exception e) {
			LOG.warn("createCbAvg() failed.", e);
		} finally {
			CoinbaseService.singleInstanceLock = false;
		}
	}
}