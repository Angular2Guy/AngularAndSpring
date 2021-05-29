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

import java.net.URI;
import java.time.Duration;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.core.publisher.Mono;

public class WsClient {
	public static void main(String[] args) throws InterruptedException {
		WebSocketClient client = new ReactorNettyWebSocketClient();		
		client.execute(URI.create("wss://echo.websocket.org"), 
				session -> session.send(Mono.just(
						session.textMessage("{\"event\":\"ping\"}")))
			    .thenMany(session
			    	      .receive()
			    	      .map(WebSocketMessage::getPayloadAsText)
			    	      .log())
			    	    .then()).block(Duration.ofSeconds(10));		
		System.out.println("End");
	}
	
}
