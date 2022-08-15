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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ch.xxx.trader.domain.model.dto.WrapperCb;
import ch.xxx.trader.domain.model.entity.QuoteBf;
import ch.xxx.trader.domain.model.entity.QuoteBs;
import ch.xxx.trader.domain.model.entity.QuoteCb;
import ch.xxx.trader.domain.model.entity.QuoteIb;
import ch.xxx.trader.usecase.mappers.EventMapper;
import ch.xxx.trader.usecase.services.BitfinexService;
import ch.xxx.trader.usecase.services.BitstampService;
import ch.xxx.trader.usecase.services.CoinbaseService;
import ch.xxx.trader.usecase.services.ItbitService;
import ch.xxx.trader.usecase.services.MyUserService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Component
public class ScheduledTask {
	private static final Logger LOG = LoggerFactory.getLogger(ScheduledTask.class);

	private static final String URLBS = "https://www.bitstamp.net/api";
	private static final String URLCB = "https://api.coinbase.com/v2";
	private static final String URLIB = "https://api.itbit.com";
	private static final String URLBF = "https://api.bitfinex.com";

	private final WebClient webClient;
	private final BitstampService bitstampService;
	private final BitfinexService bitfinexService;
	private final ItbitService itbitService;
	private final CoinbaseService coinbaseService;
	private final MyUserService myUserService;
	private final Map<String, Optional<Disposable>> disposables = new ConcurrentHashMap<>();
	private final Scheduler mongoImportScheduler = Schedulers.newBoundedElastic(40, 40, "mongoImport", 10);

	public ScheduledTask(BitstampService bitstampService, MyUserService myUserService, EventMapper messageMapper,
			BitfinexService bitfinexService, ItbitService itbitService, CoinbaseService coinbaseService,
			WebClient webClient) {
		this.webClient = webClient;
		this.bitstampService = bitstampService;
		this.bitfinexService = bitfinexService;
		this.itbitService = itbitService;
		this.coinbaseService = coinbaseService;
		this.myUserService = myUserService;
	}

//	@PostConstruct
//	public void init() {
//		
//	}

	@Scheduled(fixedRate = 90000)
	@SchedulerLock(name = "UpdateLoggedOutUsers_scheduledTask", lockAtLeastFor = "PT80S", lockAtMostFor = "PT85S")
	@Order(1)
	public void updateLoggedOutUsers() {
		this.myUserService.updateLoggedOutUsers();
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 3000)
	@SchedulerLock(name = "BitstampQuoteBTC_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteBTC() throws InterruptedException {
		String currPair = "btceur";
		insertBsQuote(currPair);
	}

	private void insertBsQuote(String currPair) {
		// LOG.info(currPair);
		this.disposeClient(currPair);
		LocalTime start = LocalTime.now();
		Mono<QuoteBs> request = this.webClient.get()
				.uri(String.format("%s/v2/ticker/%s/", ScheduledTask.URLBS, currPair))
				.accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> response.bodyToMono(QuoteBs.class))
				.map(res -> {
					res.setPair(currPair);
//				log.info(res.toString());
					return res;
				}).timeout(Duration.ofSeconds(5L)).doOnError(ex -> LOG.warn("Bitstamp data request failed", ex))
				.onErrorResume(ex -> Mono.empty()).subscribeOn(this.mongoImportScheduler);
		Disposable subscribe = null;
			subscribe = request
					.flatMap(myQuote -> this.bitstampService.insertQuote(Mono.just(myQuote))
							.timeout(Duration.ofSeconds(6L)).subscribeOn(this.mongoImportScheduler)
							.doOnError(ex -> LOG.warn("Bitstamp data store failed", ex))
							.onErrorResume(ex -> Mono.empty()))
					.subscribeOn(this.mongoImportScheduler).subscribe(x -> this.logDuration(currPair, start));
			this.disposables.put(currPair, Optional.of(subscribe));
	}

	private void logDuration(String currPair, LocalTime start) {
		long durationInMs = Duration.between(start, LocalTime.now()).toMillis();
		if (durationInMs > 1000) {
			LOG.info("Duration of {}: {}ms", currPair, durationInMs);
		}
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 6000)
	@SchedulerLock(name = "BitstampQuoteETH_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteETH() throws InterruptedException {
		String currPair = "etheur";
		this.insertBsQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 9000)
	@SchedulerLock(name = "BitstampQuoteLTC_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteLTC() throws InterruptedException {
		String currPair = "ltceur";
		this.insertBsQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 12000)
	@SchedulerLock(name = "BitstampQuoteXRP_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteXRP() throws InterruptedException {
		String currPair = "xrpeur";
		this.insertBsQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 15000)
	@SchedulerLock(name = "CoinbaseQuote_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertCoinbaseQuote() {
		final String currPair = "ALLUSD";
		// LOG.info(currPair);
		this.disposeClient(currPair);
		LocalTime start = LocalTime.now();
		Mono<QuoteCb> request = this.webClient.get().uri(ScheduledTask.URLCB + "/exchange-rates?currency=BTC")
				.accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> {
					return response.bodyToMono(WrapperCb.class);
//					return response.bodyToMono(String.class);
//				}).flatMap(value -> {
//					// log.info(value);
//					return Mono.just(this.messageMapper.mapJsonToObject(value, WrapperCb.class));
				}).flatMap(resp -> Mono.just(resp.getData())).flatMap(resp2 -> {
//				log.info(resp2.getRates().toString());
					return Mono.just(resp2.getRates());
				}).timeout(Duration.ofSeconds(5L), Mono.empty())
				.doOnError(ex -> LOG.warn("Coinbase data request failed", ex)).onErrorResume(ex -> Mono.empty())
				.subscribeOn(this.mongoImportScheduler);
		Disposable subscribe = request
				.flatMap(myQuote -> this.coinbaseService.insertQuote(Mono.just(myQuote))
						.timeout(Duration.ofSeconds(6L), Mono.empty()).subscribeOn(this.mongoImportScheduler)
						.doOnError(ex -> LOG.warn("Coinbase data store failed", ex))
						.onErrorResume(ex -> Mono.empty()))
				.subscribeOn(this.mongoImportScheduler).subscribe(x -> this.logDuration(currPair, start));
		this.disposables.put(currPair, Optional.of(subscribe));
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 21000)
	@SchedulerLock(name = "ItbitUsdQuote_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertItbitUsdQuote() {
		final String currPair = "XBTUSD";
		// LOG.info(currPair);
		this.disposeClient(currPair);
		LocalTime start = LocalTime.now();
		Mono<QuoteIb> request = this.webClient.get()
				.uri(String.format("%s/v1/markets/%s/ticker", ScheduledTask.URLIB, currPair))
				.accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> response.bodyToMono(QuoteIb.class))
				.map(res -> {
//				log.info(res.toString());
					return res;
				}).timeout(Duration.ofSeconds(5L)).doOnError(ex -> LOG.warn("Ibit data request failed", ex))
				.onErrorResume(ex -> Mono.empty()).subscribeOn(this.mongoImportScheduler);
		Disposable subscribe = request
				.flatMap(myQuote -> this.itbitService.insertQuote(Mono.just(myQuote))
						.timeout(Duration.ofSeconds(6L)).subscribeOn(this.mongoImportScheduler)
						.doOnError(ex -> LOG.warn("Itbit data store failed", ex))
						.onErrorResume(ex -> Mono.empty()))
				.subscribeOn(this.mongoImportScheduler).subscribe(x -> this.logDuration(currPair, start));
		this.disposables.put(currPair, Optional.of(subscribe));
	}

	private void disposeClient(final String currPair) {
		Optional<Disposable> optional = this.disposables.getOrDefault(currPair, Optional.empty());
		optional.ifPresent(disposable -> disposable.dispose());
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 24000)
	@SchedulerLock(name = "BitstampQuoteBTCUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteBTCUSD() throws InterruptedException {
		String currPair = "btcusd";
		this.insertBsQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 27000)
	@SchedulerLock(name = "BitstampQuoteETHUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteETHUSD() throws InterruptedException {
		String currPair = "ethusd";
		this.insertBsQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 30000)
	@SchedulerLock(name = "BitstampQuoteLTCUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteLTCUSD() throws InterruptedException {
		String currPair = "ltcusd";
		this.insertBsQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 33000)
	@SchedulerLock(name = "BitstampQuoteXRPUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteXRPUSD() throws InterruptedException {
		String currPair = "xrpusd";
		this.insertBsQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 36000)
	@SchedulerLock(name = "BitfinexQuoteBTCUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitfinexQuoteBTCUSD() throws InterruptedException {
		String currPair = "btcusd";
		insertBfQuote(currPair);
	}

	private void insertBfQuote(String currPair) {
		// LOG.info(currPair);
		this.disposeClient(currPair);
		LocalTime start = LocalTime.now();
		Mono<QuoteBf> request = this.webClient.get()
				.uri(String.format("%s/v1/pubticker/%s", ScheduledTask.URLBF, currPair))
				.accept(MediaType.APPLICATION_JSON).exchangeToMono(response -> response.bodyToMono(QuoteBf.class))
				.map(res -> {
					res.setPair(currPair);
					QuoteBf result = checkBfTimestamp(res);
//				log.info(res.toString());
					return result;
				}).timeout(Duration.ofSeconds(5L)).doOnError(ex -> LOG.warn("Bitfinex data request failed", ex))
				.onErrorResume(ex -> Mono.empty()).subscribeOn(this.mongoImportScheduler);
		Disposable subscribe = request
				.flatMap(myQuote -> this.bitfinexService.insertQuote(Mono.just(myQuote))
						.timeout(Duration.ofSeconds(6L), Mono.empty()).subscribeOn(this.mongoImportScheduler)
						.doOnError(ex -> LOG.warn("Bitfinex data store failed", ex))
						.onErrorResume(ex -> Mono.empty()))
				.subscribeOn(this.mongoImportScheduler).subscribe(x -> this.logDuration(currPair, start));
		this.disposables.put(currPair, Optional.of(subscribe));
	}

	private QuoteBf checkBfTimestamp(QuoteBf res) {
		QuoteBf result = res;
		try {
			BigDecimal timestamp = new BigDecimal(res.getTimestamp());
			LOG.debug(timestamp.toString());
		} catch (Exception e) {
			LOG.warn(String.format("Failed to parse the timestamp: %s", res.getTimestamp()), e);
			result = new QuoteBf(res.getMid(), res.getBid(), res.getAsk(), res.getLast_price(), res.getLow(),
					res.getHigh(), res.getVolume(), "0.0");
		}
		return result;
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 39000)
	@SchedulerLock(name = "BitfinexQuoteETHUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitfinexQuoteETHUSD() throws InterruptedException {
		String currPair = "ethusd";
		this.insertBfQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 42000)
	@SchedulerLock(name = "BitfinexQuoteLTCUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitfinexQuoteLTCUSD() throws InterruptedException {
		String currPair = "ltcusd";
		this.insertBfQuote(currPair);
	}

	@Async("clientTaskExecutor")
	@Scheduled(fixedRate = 60000, initialDelay = 45000)
	@SchedulerLock(name = "BitfinexQuoteXRPUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitfinexQuoteXRPUSD() throws InterruptedException {
		String currPair = "xrpusd";
		this.insertBfQuote(currPair);
	}
}
