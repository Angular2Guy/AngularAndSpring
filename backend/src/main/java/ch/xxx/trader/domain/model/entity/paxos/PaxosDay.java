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
package ch.xxx.trader.domain.model.entity.paxos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaxosDay {
	private String high;
	private String low;
	private String open;
	private String volume;
	@JsonProperty("volume_weighted_average_price")
	private String volumeWeightedAveragePrice;
	@JsonProperty("last_execution")
	private String lastExecution;
	private PaxosTimeRange range;

	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getVolumeWeightedAveragePrice() {
		return volumeWeightedAveragePrice;
	}
	public void setVolumeWeightedAveragePrice(String volumeWeightedAveragePrice) {
		this.volumeWeightedAveragePrice = volumeWeightedAveragePrice;
	}
	public PaxosTimeRange getRange() {
		return range;
	}
	public void setRange(PaxosTimeRange range) {
		this.range = range;
	}
}
