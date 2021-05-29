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
package ch.xxx.trader.adapter.clients;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ch.xxx.trader.usecase.services.OrderBookClient;
import reactor.core.publisher.Mono;

@Service
public class RestOrderBookClient implements OrderBookClient {
	private static final String URLBF = "https://api.bitfinex.com";
	private static final String URLBS = "https://www.bitstamp.net/api";

	public Mono<String> getOrderbookBitfinex(String currpair, HttpServletRequest request) {
		WebClient wc = this.buildWebClient(URLBF);
		return wc.get().uri("/v1/book/" + currpair + "/").accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(res -> res.bodyToMono(String.class));
	}

	public Mono<String> getOrderbookBitstamp(String currpair, HttpServletRequest request) {
		WebClient wc = this.buildWebClient(URLBS);
		return wc.get().uri("/v2/order_book/" + currpair + "/").accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(res -> res.bodyToMono(String.class));
	}

	private WebClient buildWebClient(String url) {
		ReactorClientHttpConnector connector = new ReactorClientHttpConnector();
		return WebClient.builder().clientConnector(connector).baseUrl(url).build();
	}
}
