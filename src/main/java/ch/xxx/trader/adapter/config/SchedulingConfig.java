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
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class SchedulingConfig implements SchedulingConfigurer {
	private final WebClient.Builder webClientBuilder;
	
	public SchedulingConfig(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}
	
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }
    
    @Bean
    public WebClient createWebClient() {
    	HttpClient httpClient = HttpClient.create()
    		      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3500).responseTimeout(Duration.ofMillis(4000));
    	ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);    	
    	return this.webClientBuilder.clientConnector(connector).build();
    }
}