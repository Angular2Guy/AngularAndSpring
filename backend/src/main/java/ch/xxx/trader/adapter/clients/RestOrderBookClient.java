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

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import ch.xxx.trader.domain.services.MyOrderBookClient;

@Service
public class RestOrderBookClient implements MyOrderBookClient {
	private static final String URLBF = "https://api.bitfinex.com";
	private static final String URLBS = "https://www.bitstamp.net/api";
	private static final String URLIB = "https://api.itbit.com";
	private final RestClient.Builder restClientBuilder;

	public RestOrderBookClient(RestClient.Builder restClientBuilder) {
		this.restClientBuilder = restClientBuilder;
	}

	public String getOrderbookBitfinex(String currpair) {
		RestClient rc = this.buildRestClient(URLBF);
		return rc.get().uri("/v1/book/" + currpair + "/").accept(MediaType.APPLICATION_JSON).retrieve()
				.body(String.class);
	}

	public String getOrderbookBitstamp(String currpair) {
		RestClient rc = this.buildRestClient(URLBS);
		return rc.get().uri("/v2/order_book/" + currpair + "/").accept(MediaType.APPLICATION_JSON).retrieve()
				.body(String.class);
	}

	public String getOrderbookItbit(String currpair) {
		RestClient rc = this.buildRestClient(URLIB);
		return rc.get().uri("/v1/markets/" + currpair + "/order_book").accept(MediaType.APPLICATION_JSON).retrieve()
				.body(String.class);
	}

	private RestClient buildRestClient(String url) {
		return this.restClientBuilder.clone().baseUrl(url).build();
	}
}