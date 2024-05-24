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
package ch.xxx.trader.adapter.config;

import java.time.Duration;

import javax.net.ssl.SSLException;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.xxx.trader.usecase.common.DtoUtils;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider.SslContextSpec;

@Configuration
public class ApplicationConfig {

	@Bean
	public ObjectMapper createObjectMapper() {
		return DtoUtils.produceObjectMapper();
	}
	
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
    
    @Bean
    public WebClient.Builder createWebClient() {
		ConnectionProvider provider = ConnectionProvider.builder("Client").maxConnections(20)
				.maxIdleTime(Duration.ofSeconds(6)).maxLifeTime(Duration.ofSeconds(7))
				.pendingAcquireTimeout(Duration.ofSeconds(9L)).evictInBackground(Duration.ofSeconds(10)).build();

		var webClientBuilder = WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient
				.create(provider).secure(spec -> sslTimeouts(spec)).option(ChannelOption.SO_KEEPALIVE, false)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
				.doOnConnected(
						c -> c.addHandlerLast(new ReadTimeoutHandler(6)).addHandlerLast(new WriteTimeoutHandler(7)))
				.responseTimeout(Duration.ofSeconds(7L))));
		return webClientBuilder;
	}

	private void sslTimeouts(SslContextSpec spec) {
		try {
			spec.sslContext(SslContextBuilder.forClient().build()).handshakeTimeout(Duration.ofSeconds(8))
					.closeNotifyFlushTimeout(Duration.ofSeconds(6)).closeNotifyReadTimeout(Duration.ofSeconds(6));
		} catch (SSLException e) {
			throw new RuntimeException(e);
		}
	}
}
