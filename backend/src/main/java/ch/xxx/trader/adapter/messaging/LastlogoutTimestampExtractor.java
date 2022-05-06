/**
 *    Copyright 2019 Sven Loesekann
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
package ch.xxx.trader.adapter.messaging;

import java.sql.Timestamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import ch.xxx.trader.adapter.config.ApplicationConfig;
import ch.xxx.trader.domain.model.entity.RevokedToken;

public class LastlogoutTimestampExtractor implements TimestampExtractor {
	private final ApplicationConfig helper = new ApplicationConfig();
	
	@Override
	public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
		RevokedToken revokedToken = this.helper.createObjectMapper().convertValue(record.value(), RevokedToken.class);
		return Timestamp.valueOf(revokedToken.getLastLogout()).getTime();
	}

}
