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

import ch.xxx.trader.domain.model.entity.QuoteIb;
import ch.xxx.trader.domain.model.entity.paxos.PaxosQuote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class ScheduledTaskTest {
	private static final String VALUE = "{\"market\":\"BTCUSD\", \"best_bid\":{\"price\":\"29318.75\", \"amount\":\"0.40468933\"}, "
			+ "\"best_ask\":{\"price\":\"29319.00\", \"amount\":\"0.09997340\"}, \"last_execution\":{\"price\":\"29321.25\", "
			+ "\"amount\":\"0.05090164\"}, \"last_day\":{\"high\":\"29524.75\", \"low\":\"28921.00\", \"open\":\"29451.25\", "
			+ "\"volume\":\"146.48759230\", \"volume_weighted_average_price\":\"29270.74796455\", \"range\":{\"begin\":\"2023-04-28T06:53:17.898910Z\", "
			+ "\"end\":\"2023-04-29T06:53:17.898910Z\"}}, \"today\":{\"high\":\"29457.75\", \"low\":\"29232.25\", \"open\":\"29340.50\", "
			+ "\"volume\":\"24.83650853\", \"volume_weighted_average_price\":\"29361.10623022\", \"range\":{\"begin\":\"2023-04-29T00:00:00Z\", "
			+ "\"end\":\"2023-04-29T06:53:17.898910Z\"}}, \"snapshot_at\":\"2023-04-29T06:53:17.898910Z\"}";
	private JsonMapper objectMapper = new JsonMapper();

	@Test
	public void convertTest() throws JsonMappingException, JsonProcessingException {
		PaxosQuote paxosQuote = this.objectMapper.readValue(VALUE, PaxosQuote.class);
		ScheduledTask scheduledTask = new ScheduledTask(null, null, null, null, null, null, null);
		QuoteIb quoteIb = scheduledTask.convert(paxosQuote);
		Assertions.assertEquals("XBTUSD", quoteIb.getPair());
		Assertions.assertEquals(paxosQuote.getBestBid().getPrice().toString(), quoteIb.getBid().toString());
		Assertions.assertEquals(paxosQuote.getBestBid().getAmount().toString(), quoteIb.getBidAmt().toString());
		Assertions.assertEquals(paxosQuote.getBestAsk().getPrice().toString(), quoteIb.getAsk().toString());
		Assertions.assertEquals(paxosQuote.getBestAsk().getAmount().toString(), quoteIb.getAskAmt().toString());
		Assertions.assertEquals(paxosQuote.getSnapshotAt(), quoteIb.getServerTimeUTC());
		Assertions.assertEquals(paxosQuote.getLastDay().getHigh().toString(), quoteIb.getHigh24h().toString());
		Assertions.assertEquals(paxosQuote.getLastDay().getLow().toString(), quoteIb.getLow24h().toString());
		Assertions.assertEquals(paxosQuote.getLastDay().getLow().toString(), quoteIb.getLow24h().toString());
		Assertions.assertEquals(paxosQuote.getToday().getHigh().toString(), quoteIb.getHighToday().toString());
		Assertions.assertEquals(paxosQuote.getLastExecution().getPrice().toString(), quoteIb.getLastPrice().toString());
		Assertions.assertEquals(paxosQuote.getLastDay().getLow().toString(), quoteIb.getLow24h().toString());
		Assertions.assertEquals(paxosQuote.getToday().getLow().toString(), quoteIb.getLowToday().toString());
		Assertions.assertEquals(paxosQuote.getToday().getOpen().toString(), quoteIb.getOpenToday().toString());
		Assertions.assertEquals(paxosQuote.getLastExecution().getAmount().toString(), quoteIb.getStAmt().toString());
		Assertions.assertEquals(paxosQuote.getLastDay().getVolumeWeightedAveragePrice().toString(), quoteIb.getVwap24h().toString());
		Assertions.assertEquals(paxosQuote.getToday().getVolumeWeightedAveragePrice().toString(), quoteIb.getVwapToday().toString());
		Assertions.assertEquals(paxosQuote.getToday().getVolume().toString(), quoteIb.getVolumeToday().toString());
		Assertions.assertEquals(paxosQuote.getLastDay().getVolume().toString(), quoteIb.getVolume24h().toString());
	}
}
