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

import ch.xxx.trader.domain.model.entity.QuoteIb;
import ch.xxx.trader.usecase.services.ItbitService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itbit")
public class ItbitController {
	private final ItbitService itbitService;
	
	public ItbitController(ItbitService itbitService) {
		this.itbitService = itbitService;
	}
	
	@GetMapping("/{currpair}/orderbook")
	public Mono<String> getOrderbook(@PathVariable String currpair, HttpServletRequest request) {
		return this.itbitService.getOrderbook(currpair);
	}

	@GetMapping("/{pair}/current")
	public Mono<QuoteIb> currentQuote(@PathVariable String pair) {
		return this.itbitService.currentQuote(pair);
	}

	@GetMapping("/{pair}/{timeFrame}")
	public Flux<QuoteIb> tfQuotes(@PathVariable String timeFrame, @PathVariable String pair) {
		return this.itbitService.tfQuotes(timeFrame, pair);
	}		
	
	@GetMapping(path="/{pair}/{timeFrame}/pdf", produces=MediaType.APPLICATION_PDF_VALUE)
	public Mono<byte[]> pdfReport(@PathVariable String timeFrame, @PathVariable String pair) {
		return this.itbitService.pdfReport(timeFrame, pair);
	}	
}
