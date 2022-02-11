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
package ch.xxx.trader.adapter.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskStarter {
	private static final Logger log = LoggerFactory.getLogger(TaskStarter.class);
	private final PrepareDataTask prepareDataTask;
	
	public TaskStarter(PrepareDataTask prepareDataTask) {
		this.prepareDataTask = prepareDataTask;
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void initAvgs() {
		log.info("ApplicationReady");
		this.prepareDataTask.createBsAvg();
		this.prepareDataTask.createBfAvg();
		this.prepareDataTask.createIbAvg();
		this.prepareDataTask.createCbHAvg();
	}
}
