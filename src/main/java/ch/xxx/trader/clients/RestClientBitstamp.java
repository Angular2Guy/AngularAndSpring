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

public class RestClientBitstamp {
	private static final String URL = "https://www.bitstamp.net/api";

	public static void main(String[] args) {
		WebClient wc = WebClient.create(URL);
		QuoteBs quote = wc.get().uri("/v2/ticker/xrpeur/")
                .accept(MediaType.APPLICATION_JSON).exchange().flatMap(response -> response.bodyToMono(QuoteBs.class))
                .map(res -> {res.setPair("xrpeur");return res;}).block();
		System.out.println(quote.toString());
	}
}
