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
package ch.xxx.trader.clients;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import dtos.QuoteIb;

public class RestClientItbit {

	private static final String URL = "https://api.itbit.com";
	
	public static void main(String[] args) {
		WebClient wc = WebClient.create(URL);
		QuoteIb quote = wc.get().uri("/v1/markets/XBTUSD/ticker")
                .accept(MediaType.APPLICATION_JSON).exchange().flatMap(response -> response.bodyToMono(QuoteIb.class))
                .block();
		System.out.println(quote.toString());
	}
}
