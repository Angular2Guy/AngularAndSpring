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
package ch.xxx.trader.adapter.clients.test;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import ch.xxx.trader.domain.model.QuoteBf;
import reactor.core.publisher.Mono;

public class WebsocketClient {
	private static final String BITSTAMP = "wss://ws.pusherapp.com/app/de504dc5763aeef9ff52?protocol=7&client=js&version=2.1.6&flash=false";
	private static final String BITFINEX = "wss://api.bitfinex.com/ws/";
	private WebSocketClient wsClient = new ReactorNettyWebSocketClient(); 
	
	public Mono<Void> executeBitstamp(WebSocketHandler handler) throws URISyntaxException {
		return wsClient.execute(new URI(BITSTAMP), handler);
	}
	
	public static void main(String[] args) throws URISyntaxException {
		WebSocketClient client = new ReactorNettyWebSocketClient();		
		client.execute(URI.create(BITFINEX), 
				session -> session.send(Mono.just(session.textMessage("{\"event\": \"subscribe\", \"channel\": \"ticker\", \"pair\": \"BTCUSD\" }")))
//				.and(session.send(Mono.just(session.textMessage("{\"event\": \"subscribe\", \"channel\": \"ticker\", \"pair\": \"ETHUSD\" }"))))
//				.and(session.send(Mono.just(session.textMessage("{\"event\": \"subscribe\", \"channel\": \"ticker\", \"pair\": \"LTCUSD\" }"))))
//				.and(session.send(Mono.just(session.textMessage("{\"event\": \"subscribe\", \"channel\": \"ticker\", \"pair\": \"XRPUSD\" }"))))				
			    .thenMany(session
			    	      .receive()
			    	      .map(WebSocketMessage::getPayloadAsText)
			    	      .filter(msg -> msg.indexOf("\"hb\"") < 0)
			    	      .map(msg -> {
			    	    	  	String[] strs = msg.replace(']', ' ').trim().split(",");
			    	    	  	if(strs.length > 9) {
//			    	    	  		System.out.println(strs[1]+" "+strs[2]+" "+strs[3]+" "+strs[4]+" "+strs[5]+" "+strs[6]+" "+strs[7]+" "+strs[8]+" "+strs[9]+" "+strs[10]);
			    	    	  		BigDecimal mid = (new BigDecimal(strs[9]).add(new BigDecimal(strs[10].trim()))).divide(BigDecimal.valueOf(2));
			    	    	  		QuoteBf qbf = new QuoteBf(mid, new BigDecimal(strs[1]), new BigDecimal(strs[3]), new BigDecimal(strs[7]), new BigDecimal(strs[10]), new BigDecimal(strs[9]), new BigDecimal(strs[8]), "");
			    	    	  		qbf.setPair("BTCUSD");
			    	    	  		return Optional.of(qbf);
			    	    	  	}
								return Optional.empty();
						})			    	      
			    	      .log())
			    	    .then()).block();		
		System.out.println("End");
	}
}
