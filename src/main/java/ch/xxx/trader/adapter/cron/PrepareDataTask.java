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

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
//		CompletableFuture<String> future1  
//		  = CompletableFuture.supplyAsync(() -> {this.bitstampService.createBsHourlyAvg(); return "createBsHourlyAvg() Done.";});
//		CompletableFuture<String> future2  
//		  = CompletableFuture.supplyAsync(() -> {this.bitstampService.createBsDailyAvg(); return "createBsDailyAvg() Done.";});
//		CompletableFuture<String> future3  
//		  = CompletableFuture.supplyAsync(() -> {this.bitfinexService.createBfHourlyAvg(); return "createBfHourlyAvg() Done.";});
//		CompletableFuture<String> future4  
//		  = CompletableFuture.supplyAsync(() -> {this.bitfinexService.createBfDailyAvg(); return "createBfDailyAvg() Done.";});
//		CompletableFuture<String> future5 
//		  = CompletableFuture.supplyAsync(() -> {this.itbitService.createIbHourlyAvg(); return "createIbHourlyAvg() Done.";});
//		CompletableFuture<String> future6 
//		  = CompletableFuture.supplyAsync(() -> {this.itbitService.createIbDailyAvg(); return "createIbDailyAvg() Done.";});
//		CompletableFuture<String> future7 
//		  = CompletableFuture.supplyAsync(() -> {this.coinbaseService.createCbHourlyAvg(); return "createCbHourlyAvg() Done.";});
//		CompletableFuture<String> future8 
//		  = CompletableFuture.supplyAsync(() -> {this.coinbaseService.createCbDailyAvg(); return "createCbDailyAvg() Done.";});
//		String combined = Stream.of(future1, future2, future3, future4, future5, future6, future7, future8)
//				  .map(CompletableFuture::join)
//				  .collect(Collectors.joining(" "));
//		String combined = Stream.of(future7)
//				  .map(CompletableFuture::join)
//				  .collect(Collectors.joining(" "));
//		log.info(combined);
	}

	@Scheduled(cron = "0 5 0 ? * ?")
	@SchedulerLock(name = "bitstamp_avg_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bs.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBsAvg() {
		CompletableFuture<String> future1  
		  = CompletableFuture.supplyAsync(() -> {this.bitstampService.createBsHourlyAvg(); return "createBsHourlyAvg() Done.";});
		CompletableFuture<String> future2  
		  = CompletableFuture.supplyAsync(() -> {this.bitstampService.createBsDailyAvg(); return "createBsDailyAvg() Done.";});
		String combined = Stream.of(future1, future2)
		  .map(CompletableFuture::join)
		  .collect(Collectors.joining(" "));
		log.info(combined);
	}	

	@Scheduled(cron = "0 45 0 ? * ?")
	@SchedulerLock(name = "bitfinex_avg_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.bf.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createBfAvg() {
		CompletableFuture<String> future3  
		  = CompletableFuture.supplyAsync(() -> {this.bitfinexService.createBfHourlyAvg(); return "createBfHourlyAvg() Done.";});
		CompletableFuture<String> future4  
		  = CompletableFuture.supplyAsync(() -> {this.bitfinexService.createBfDailyAvg(); return "createBfDailyAvg() Done.";});
		String combined = Stream.of(future3, future4)
				  .map(CompletableFuture::join)
				  .collect(Collectors.joining(" "));
		log.info(combined);
	}

	@Scheduled(cron = "0 25 1 ? * ?")
	@SchedulerLock(name = "itbit_avg_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.ib.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createIbHourlyAvg() {
		CompletableFuture<String> future5 
		  = CompletableFuture.supplyAsync(() -> {this.itbitService.createIbHourlyAvg(); return "createIbHourlyAvg() Done.";});
		CompletableFuture<String> future6 
		  = CompletableFuture.supplyAsync(() -> {this.itbitService.createIbDailyAvg(); return "createIbDailyAvg() Done.";});
		String combined = Stream.of(future5, future6)
				  .map(CompletableFuture::join)
				  .collect(Collectors.joining(" "));
		log.info(combined);
	}

	@Scheduled(cron = "0 10 2 ? * ?")
	@SchedulerLock(name = "coinbase_avg_scheduledTask", lockAtLeastFor = "PT1M", lockAtMostFor = "PT23H")
	@Timed(value = "create.cb.avg", percentiles = { 0.5, 0.95, 0.99 })
	public void createCbHourlyAvg() {
		CompletableFuture<String> future7 
		  = CompletableFuture.supplyAsync(() -> {this.coinbaseService.createCbHourlyAvg(); return "createCbHourlyAvg() Done.";});
		CompletableFuture<String> future8 
		  = CompletableFuture.supplyAsync(() -> {this.coinbaseService.createCbDailyAvg(); return "createCbDailyAvg() Done.";});
		String combined = Stream.of(future7, future8)
				  .map(CompletableFuture::join)
				  .collect(Collectors.joining(" "));
		log.info(combined);
	}
}
