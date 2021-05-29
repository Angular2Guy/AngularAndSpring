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

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.trader.domain.dtos.QuoteBf;
import ch.xxx.trader.usecase.services.BitfinexService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bitfinex")
public class BitfinexController {
	private final BitfinexService bitfinexService;
	
	public BitfinexController(BitfinexService bitfinexService) {
		this.bitfinexService = bitfinexService;
	}

	@GetMapping("/{currpair}/orderbook")
	public Mono<String> getOrderbook(@PathVariable String currpair, HttpServletRequest request) {		
		return this.bitfinexService.getOrderbook(currpair, request);
	}

	@GetMapping("/{pair}/current")
	public Mono<QuoteBf> currentQuote(@PathVariable String pair) {
		return this.bitfinexService.currentQuote(pair);
	}

	@GetMapping("/{pair}/{timeFrame}")
	public Flux<QuoteBf> tfQuotes(@PathVariable String timeFrame, @PathVariable String pair) {
		return this.bitfinexService.tfQuotes(timeFrame, pair);		
	}
	
	@GetMapping(path="/{pair}/{timeFrame}/pdf", produces=MediaType.APPLICATION_PDF_VALUE)
	public Mono<byte[]> pdfReport(@PathVariable String timeFrame, @PathVariable String pair) {
		return this.bitfinexService.pdfReport(timeFrame, pair);		
	}	
}
