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

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.xxx.trader.usecase.services.BitfinexService;
import ch.xxx.trader.usecase.services.BitstampService;
import ch.xxx.trader.usecase.services.CoinbaseService;
import ch.xxx.trader.usecase.services.ItbitService;

@Component
public class PrepareData {
	private final BitstampService bitstampService;
	private final BitfinexService bitfinexService;	
	private final ItbitService itbitService;
	private final CoinbaseService coinbaseService;
	
	public PrepareData(BitstampService bitstampService, BitfinexService bitfinexService, ItbitService itbitService, CoinbaseService coinbaseService) {
		this.bitstampService = bitstampService;
		this.bitfinexService = bitfinexService;
		this.itbitService = itbitService;
		this. coinbaseService = coinbaseService;
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 5 0 ? * ?")
	public void createBsHourlyAvg() {
		this.bitstampService.createBsHourlyAvg();;		
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 5 1 ? * ?")
	public void createBsDailyAvg() {
		this.bitstampService.createBsDailyAvg();		
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 10 0 ? * ?")
	public void createBfHourlyAvg() {		
		this.bitfinexService.createBfHourlyAvg();
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 10 1 ? * ?")
	public void createBfDailyAvg() {
		this.bitfinexService.createBfDailyAvg();
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 15 0 ? * ?")
	public void createIbHourlyAvg() {
		this.itbitService.createIbHourlyAvg();
	}

	// @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 15 1 ? * ?")
	public void createIbDailyAvg() {
		this.itbitService.createIbDailyAvg();		
	}

//	 @Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 20 0 ? * ?")
	public void createCbHourlyAvg() {
		this.coinbaseService.createCbHourlyAvg();		
	}

//	@Scheduled(fixedRate = 300000000, initialDelay = 3000)
	@Scheduled(cron = "0 20 1 ? * ?")
	public void createCbDailyAvg() {
		this.coinbaseService.createCbDailyAvg();		
	}
}
