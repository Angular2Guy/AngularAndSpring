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

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ch.xxx.trader.domain.model.QuoteBf;
import ch.xxx.trader.domain.model.QuoteBs;
import ch.xxx.trader.domain.model.QuoteIb;
import ch.xxx.trader.domain.model.WrapperCb;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import reactor.core.publisher.Mono;

@Component
public class ScheduledTask {
	private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private static final String URLBS = "https://www.bitstamp.net/api";
	private static final String URLCB = "https://api.coinbase.com/v2";
	private static final String URLIB = "https://api.itbit.com";
	private static final String URLBF = "https://api.bitfinex.com";
	private WebClient bsWebClient;
	private WebClient cbWebClient;
	private WebClient ibWebClient;
	private WebClient bfWebClient;

	private final ReactiveMongoOperations operations;
	
	public ScheduledTask(ReactiveMongoOperations operations) {
		this.operations = operations;
	}

	@PostConstruct
	public void init() {
		this.bsWebClient = this.buildWebClient(URLBS);
		this.cbWebClient = this.buildWebClient(URLCB);
		this.ibWebClient = this.buildWebClient(URLIB);
		this.bfWebClient = this.buildWebClient(URLBF);
	}

	private WebClient buildWebClient(String url) {
		ReactorClientHttpConnector connector = new ReactorClientHttpConnector();
		return WebClient.builder().clientConnector(connector).baseUrl(url).build();
	}

	@Scheduled(fixedRate = 60000, initialDelay = 3000)
	@SchedulerLock(name = "BitstampQuoteBTC_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteBTC() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bsWebClient;
		try {
			operations.insert(wc.get().uri("/v2/ticker/btceur/").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBs.class)).map(res -> {
						res.setPair("btceur");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitstampQuote Btc " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitstamp Btc insert error " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 6000)
	@SchedulerLock(name = "BitstampQuoteETH_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteETH() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bsWebClient;
		try {
			operations.insert(wc.get().uri("/v2/ticker/etheur/").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBs.class)).map(res -> {
						res.setPair("etheur");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitstampQuote Eth " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitstamp Eth insert error " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 9000)
	@SchedulerLock(name = "BitstampQuoteLTC_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteLTC() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bsWebClient;
		try {
			operations.insert(wc.get().uri("/v2/ticker/ltceur/").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBs.class)).map(res -> {
						res.setPair("ltceur");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitstampQuote Ltc " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitstamp Ltc insert error " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 12000)
	@SchedulerLock(name = "BitstampQuoteXRP_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteXRP() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bsWebClient;
		try {
			operations.insert(wc.get().uri("/v2/ticker/xrpeur/").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBs.class)).map(res -> {
						res.setPair("xrpeur");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitstampQuote Xrp " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitstamp Xrp insert error " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 15000)
	@SchedulerLock(name = "CoinbaseQuote_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertCoinbaseQuote() {
		Date start = new Date();
		WebClient wc = this.cbWebClient;
		try {
			operations.insert(wc.get().uri("/exchange-rates?currency=BTC").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(WrapperCb.class))
					.flatMap(resp -> Mono.just(resp.getData())).flatMap(resp2 -> {
						log.info(resp2.getRates().toString());
						return Mono.just(resp2.getRates());
					})).then().block(Duration.ofSeconds(3));
			log.info("CoinbaseQuote " + dateFormat.format(new Date()) + " " + (new Date().getTime() - start.getTime())
					+ "ms");
		} catch (Exception e) {
//			log.error("Coinbase insert error", e);
			log.error("Coinbase insert error " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 18000)
	@SchedulerLock(name = "ItbitQuote_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertItbitQuote() {
		Date start = new Date();
		WebClient wc = this.ibWebClient;
		try {
			operations.insert(wc.get().uri("/v1/markets/XBTEUR/ticker").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteIb.class)).map(res -> {
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("ItbitQuote Btc " + dateFormat.format(new Date()) + " " + (new Date().getTime() - start.getTime())
					+ "ms");
		} catch (Exception e) {
//			log.error("Itbit insert error", e);
			log.error("Itbit Btc insert error " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 21000)
	@SchedulerLock(name = "ItbitUsdQuote_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertItbitUsdQuote() {
		Date start = new Date();
		WebClient wc = this.ibWebClient;
		try {
			operations.insert(wc.get().uri("/v1/markets/XBTUSD/ticker").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteIb.class)).map(res -> {
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("ItbitQuote Btc Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Itbit insert error", e);
			log.error("Itbit Btc insert error Usd " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 24000)
	@SchedulerLock(name = "BitstampQuoteBTCUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteBTCUSD() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bsWebClient;
		try {
			operations.insert(wc.get().uri("/v2/ticker/btcusd/").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBs.class)).map(res -> {
						res.setPair("btcusd");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitstampQuote Btc Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitstamp Btc insert error Usd " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 27000)
	@SchedulerLock(name = "BitstampQuoteETHUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteETHUSD() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bsWebClient;
		try {
			operations.insert(wc.get().uri("/v2/ticker/ethusd/").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBs.class)).map(res -> {
						res.setPair("ethusd");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitstampQuote Eth Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitstamp Eth insert error Usd " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 30000)
	@SchedulerLock(name = "BitstampQuoteLTCUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteLTCUSD() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bsWebClient;
		try {
			operations.insert(wc.get().uri("/v2/ticker/ltcusd/").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBs.class)).map(res -> {
						res.setPair("ltcusd");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitstampQuote Ltc Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitstamp Ltc insert error Usd " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 33000)
	@SchedulerLock(name = "BitstampQuoteXRPUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitstampQuoteXRPUSD() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bsWebClient;
		try {
			operations.insert(wc.get().uri("/v2/ticker/xrpusd/").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBs.class)).map(res -> {
						res.setPair("xrpusd");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitstampQuote Xrp Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitstamp Xrp insert error Usd " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 36000)
	@SchedulerLock(name = "BitfinexQuoteBTCUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitfinexQuoteBTCUSD() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bfWebClient;
		try {
			operations.insert(wc.get().uri("/v1/pubticker/btcusd").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBf.class)).map(res -> {
						res.setPair("btcusd");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitfinexQuote Btc Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitfinex Btc insert error Usd " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 39000)
	@SchedulerLock(name = "BitfinexQuoteETHUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitfinexQuoteETHUSD() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bfWebClient;
		try {
			operations.insert(wc.get().uri("/v1/pubticker/ethusd").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBf.class)).map(res -> {
						res.setPair("ethusd");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitfinexQuote Eth Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitfinex Eth insert error Usd " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 42000)
	@SchedulerLock(name = "BitfinexQuoteLTCUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitfinexQuoteLTCUSD() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bfWebClient;
		try {
			operations.insert(wc.get().uri("/v1/pubticker/ltcusd").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBf.class)).map(res -> {
						res.setPair("ltcusd");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitfinexQuote Ltc Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitfinex Ltc insert error Usd " + dateFormat.format(new Date()));
		}
	}

	@Scheduled(fixedRate = 60000, initialDelay = 45000)
	@SchedulerLock(name = "BitfinexQuoteXRPUSD_scheduledTask", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
	public void insertBitfinexQuoteXRPUSD() throws InterruptedException {
		Date start = new Date();
		WebClient wc = this.bfWebClient;
		try {
			operations.insert(wc.get().uri("/v1/pubticker/xrpusd").accept(MediaType.APPLICATION_JSON)
					.exchangeToMono(response -> response.bodyToMono(QuoteBf.class)).map(res -> {
						res.setPair("xrpusd");
						log.info(res.toString());
						return res;
					})).then().block(Duration.ofSeconds(3));
			log.info("BitfinexQuote Xrp Usd " + dateFormat.format(new Date()) + " "
					+ (new Date().getTime() - start.getTime()) + "ms");
		} catch (Exception e) {
//			log.error("Bitstamp insert error", e);
			log.error("Bitfinex Xrp insert error Usd " + dateFormat.format(new Date()));
		}
	}
}
