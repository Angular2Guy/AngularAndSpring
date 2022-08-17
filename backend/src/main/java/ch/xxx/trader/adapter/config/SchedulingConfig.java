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
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.netty.channel.ChannelOption;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class SchedulingConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingConfig.class);
    		
    @Bean
    TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    @Bean
    public WebClient createWebClient() {
		ConnectionProvider provider = ConnectionProvider.builder("Client").maxConnections(40)
				.maxIdleTime(Duration.ofSeconds(6)).maxLifeTime(Duration.ofSeconds(7))
				.pendingAcquireTimeout(Duration.ofSeconds(25)).evictInBackground(Duration.ofSeconds(10)).build();

		WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)
				.option(ChannelOption.SO_KEEPALIVE, false)				
				.responseTimeout(Duration.ofSeconds(16L)))).build();
		return webClient;
    }
    
    @Bean(name = "clientTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        return this.createThreadPoolTaskExecutor(20);
    }
    
    @Bean(name = "avgTaskExecutor")
    public Executor avgTreadPoolTaskExecutor() {
    	return this.createThreadPoolTaskExecutor(10);
    }
    
    private Executor createThreadPoolTaskExecutor(int maxPoolSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(1);
        executor.setKeepAliveSeconds(1);
        return executor;
    }
}