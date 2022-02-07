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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.xxx.trader.usecase.services.BitfinexService;
import ch.xxx.trader.usecase.services.BitstampService;
import ch.xxx.trader.usecase.services.CoinbaseService;
import ch.xxx.trader.usecase.services.ItbitService;
import io.micrometer.core.annotation.Timed;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
public class PrepareDataTask {
	private static final Logger log = LoggerFactory.getLogger(PrepareDataTask.class);
	private final BitstampService bitstampService;
	private final BitfinexService bitfinexService;
	private final ItbitService itbitService;
	private final CoinbaseService coinbaseService;

	public PrepareDataTask(BitstampService bitstampService, BitfinexService bitfinexService, ItbitService itbitService,
			CoinbaseService coinbaseService) {
		this.bitstampService = bitstampService;
		this.bitfinexService = bitfinexService;
		this.itbitService = itbitService;
		this.coinbaseService = coinbaseService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initAvgs() {
		log.info("ApplicationReady");
		//this.bitstampService.createBsHourlyAvg();
		//this.bitstampService.createBsDailyAvg();
		//this.bitfinexService.createBfHourlyAvg();
		//this.bitfinexService.createBfDailyAvg();
		//this.itbitService.createIbHourlyAvg();
		//this.itbitService.createIbDailyAvg();
		//this.coinbaseService.createCbHourlyAvg();
		//this.coinbaseService.createCbDailyAvg();
	}

	@Scheduled(cron = "0 5 0 ? * ?")
	@SchedulerLock(name = "bitstamp_hourly_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bs.hourly.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBsHourlyAvg() {
		this.bitstampService.createBsHourlyAvg();
	}

	@Scheduled(cron = "0 10 1 ? * ?")
	@SchedulerLock(name = "bitstamp_daily_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bs.daily.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBsDailyAvg() {
		this.bitstampService.createBsDailyAvg();
	}

	@Scheduled(cron = "0 25 0 ? * ?")
	@SchedulerLock(name = "bitfinex_hourly_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bf.hourly.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBfHourlyAvg() {
		this.bitfinexService.createBfHourlyAvg();
	}

	@Scheduled(cron = "0 30 1 ? * ?")
	@SchedulerLock(name = "bitfinex_daily_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bf.daily.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBfDailyAvg() {
		this.bitfinexService.createBfDailyAvg();
	}

	@Scheduled(cron = "0 50 0 ? * ?")
	@SchedulerLock(name = "itbit_hourly_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.ib.hourly.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createIbHourlyAvg() {
		this.itbitService.createIbHourlyAvg();
	}

	@Scheduled(cron = "0 50 1 ? * ?")
	@SchedulerLock(name = "itbit_daily_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.ib.daily.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createIbDailyAvg() {
		this.itbitService.createIbDailyAvg();
	}

	@Scheduled(cron = "0 10 2 ? * ?")
	@SchedulerLock(name = "coinbase_hourly_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.cb.hourly.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createCbHourlyAvg() {
		this.coinbaseService.createCbHourlyAvg();
	}

	@Scheduled(cron = "0 10 3 ? * ?")
	@SchedulerLock(name = "coinbase_daily_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.cb.daily.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createCbDailyAvg() {
		this.coinbaseService.createCbDailyAvg();
	}
}
