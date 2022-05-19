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
package ch.xxx.trader.usecase.common;

import java.sql.Timestamp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.xxx.trader.domain.model.entity.RevokedToken;

public class LastlogoutTimestampExtractor implements TimestampExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LastlogoutTimestampExtractor.class);
	
	@Override
	public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
		RevokedToken revokedToken = DtoUtils.produceObjectMapper().convertValue(record.value(), RevokedToken.class);
//		LOGGER.info(revokedToken.toString());
		return Timestamp.valueOf(revokedToken.getLastLogout()).getTime();
	}

}
