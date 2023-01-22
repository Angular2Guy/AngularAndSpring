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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import ch.xxx.trader.usecase.services.BitfinexService;
import ch.xxx.trader.usecase.services.BitstampService;
import ch.xxx.trader.usecase.services.CoinbaseService;
import ch.xxx.trader.usecase.services.ItbitService;

@Component
public class TaskStarter {
	private static final Logger log = LoggerFactory.getLogger(TaskStarter.class);
	private final BitstampService bitstampService;
	private final BitfinexService bitfinexService;
	private final ItbitService itbitService;
	private final CoinbaseService coinbaseService;
	@Value("${single.instance.deployment:false}")
	private boolean singleInstanceDeployment;

	public TaskStarter(BitstampService bitstampService, BitfinexService bitfinexService, ItbitService itbitService,
			CoinbaseService coinbaseService) {
		this.bitstampService = bitstampService;
		this.bitfinexService = bitfinexService;
		this.itbitService = itbitService;
		this.coinbaseService = coinbaseService;
	}

	@Async
	@EventListener(ApplicationReadyEvent.class)
	public void initAvgs() {
		if (this.singleInstanceDeployment) {
			log.info("ApplicationReady");
			this.bitstampService.createBsAvg().block();
			this.bitfinexService.createBfAvg().block();
			this.itbitService.createIbAvg().block();
			this.coinbaseService.createCbAvg().block();
		}
	}
}
