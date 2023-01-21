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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
public class FlapDoodleConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(FlapDoodleConfig.class);
	private static final int MONGO_DB_PORT = 27017;
	private MongodExecutable mongodExecutable = null;
	private MongodProcess mongod = null;
	@Value("${server.port:}")
	private String serverPort;
	@Value("${spring.profiles.active:}")
	private String activeProfiles;
	
	@PostConstruct
	public void initMongoDb() {
		if (this.serverPort.isBlank() || this.serverPort.contains("8080") || this.serverPort.matches("\\d")
				&& (this.activeProfiles.isBlank() || !this.activeProfiles.toLowerCase().contains("prod"))) {
			try {
				MongodStarter starter = MongodStarter.getDefaultInstance();
				MongodConfig mongodConfig = MongodConfig.builder().version(Version.Main.V4_4)
						.net(new Net(MONGO_DB_PORT, Network.localhostIsIPv6())).build();
				this.mongodExecutable = starter.prepare(mongodConfig);
				this.mongod = this.mongodExecutable.start();
				LOGGER.info("MongoDb process: {}, state: {}", this.mongod.getProcessId(),
						this.mongod.isProcessRunning());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@PreDestroy
	public void stopMongoDb() {
		if (this.serverPort.isBlank() || this.serverPort.contains("8080") || this.serverPort.matches("\\d")
				&& (this.activeProfiles.isBlank() || !this.activeProfiles.toLowerCase().contains("prod"))) {
			try {
				this.mongodExecutable.stop();
				LOGGER.info("MongoDb proces: {}, state: {}", this.mongod.getProcessId(),
						this.mongod.isProcessRunning());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
