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

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.xxx.trader.usecase.services.BitfinexService;
import ch.xxx.trader.usecase.services.BitstampService;
import ch.xxx.trader.usecase.services.CoinbaseService;
import ch.xxx.trader.usecase.services.ItbitService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import reactor.core.Disposable;

@Component
public class PrepareDataTask {
	private static final Logger LOG = LoggerFactory.getLogger(PrepareDataTask.class);
	private final BitstampService bitstampService;
	private final BitfinexService bitfinexService;
	private final ItbitService itbitService;
	private final CoinbaseService coinbaseService;
	private Optional<Disposable> bitstampDisposableOpt = Optional.empty();
	private Optional<Disposable> bitfinexDisposableOpt = Optional.empty();
	private Optional<Disposable> itbitDisposableOpt = Optional.empty();
	private Optional<Disposable> coinbaseDisposableOpt = Optional.empty();

	public PrepareDataTask(BitstampService bitstampService, BitfinexService bitfinexService, ItbitService itbitService,
			CoinbaseService coinbaseService) {
		this.bitstampService = bitstampService;
		this.bitfinexService = bitfinexService;
		this.itbitService = itbitService;
		this.coinbaseService = coinbaseService;
	}

	@Async
	@Scheduled(cron = "0 5 0,12 ? * ?")
	@SchedulerLock(name = "bitstamp_avg_scheduledTask", lockAtLeastFor = "PT10H", lockAtMostFor = "PT11H")
	public void createBsAvg() {
		this.bitstampDisposableOpt.ifPresent(myDisposable -> myDisposable.dispose());
		this.bitstampDisposableOpt = Optional.of(this.bitstampService.createBsAvg()
				.doFinally(value -> BitstampService.singleInstanceLock = false).subscribe(result -> {
				}, error -> LOG.warn("createBsAvg() failed.", error)));
	}

	@Async
	@Scheduled(cron = "0 45 0,12 ? * ?")
	@SchedulerLock(name = "bitfinex_avg_scheduledTask", lockAtLeastFor = "PT10H", lockAtMostFor = "PT11H")
	public void createBfAvg() {
		this.bitfinexDisposableOpt.ifPresent(myDisposable -> myDisposable.dispose());
		this.bitfinexDisposableOpt = Optional.of(this.bitfinexService.createBfAvg()
				.doFinally(value -> BitfinexService.singleInstanceLock = false).subscribe(result -> {
				}, error -> LOG.warn("createBfAvg() failed.", error)));
	}

	@Async
	@Scheduled(cron = "0 25 1,13 ? * ?")
	@SchedulerLock(name = "itbit_avg_scheduledTask", lockAtLeastFor = "PT10H", lockAtMostFor = "PT11H")
	public void createIbAvg() {
		this.itbitDisposableOpt.ifPresent(myDisposable -> myDisposable.dispose());
		this.itbitDisposableOpt = Optional.of(this.itbitService.createIbAvg()
				.doFinally(value -> ItbitService.singleInstanceLock = false).subscribe(result -> {
				}, error -> LOG.warn("createIbAvg() failed.", error)));
	}

	@Async
	@Scheduled(cron = "0 10 2,14 ? * ?")
	@SchedulerLock(name = "coinbase_avg_scheduledTask", lockAtLeastFor = "PT10H", lockAtMostFor = "PT11H")
	public void createCbAvg() {
		this.coinbaseDisposableOpt.ifPresent(myDisposable -> myDisposable.dispose());
		this.coinbaseDisposableOpt = Optional.of(this.coinbaseService.createCbAvg()
				.doFinally(value -> CoinbaseService.singleInstanceLock = false).subscribe(result -> {
				}, error -> LOG.warn("createCbAvg() failed.", error)));
	}
}
