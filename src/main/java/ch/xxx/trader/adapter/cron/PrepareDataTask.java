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
	}

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 5 0 ? * ?")
	@SchedulerLock(name = "hourly_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bs.hourly.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBsHourlyAvg() {
		this.bitstampService.createBsHourlyAvg();
	}

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 5 1 ? * ?")
	@SchedulerLock(name = "daily_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bs.daily.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBsDailyAvg() {
		this.bitstampService.createBsDailyAvg();
	}

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 10 0 ? * ?")
	@SchedulerLock(name = "hourly_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bf.hourly.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBfHourlyAvg() {
		this.bitfinexService.createBfHourlyAvg();
	}

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 10 1 ? * ?")
	@SchedulerLock(name = "daily_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bf.daily.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBfDailyAvg() {
		this.bitfinexService.createBfDailyAvg();
	}

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 15 0 ? * ?")
	@SchedulerLock(name = "hourly_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.ib.hourly.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createIbHourlyAvg() {
		this.itbitService.createIbHourlyAvg();
	}

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 15 1 ? * ?")
	@SchedulerLock(name = "daily_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.ib.daily.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createIbDailyAvg() {
		this.itbitService.createIbDailyAvg();
	}

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 20 0 ? * ?")
	@SchedulerLock(name = "hourly_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.cb.hourly.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createCbHourlyAvg() {
		this.coinbaseService.createCbHourlyAvg();
	}

//	@Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 20 1 ? * ?")
	@SchedulerLock(name = "daily_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.cb.daily.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createCbDailyAvg() {
		this.coinbaseService.createCbDailyAvg();
	}
}
