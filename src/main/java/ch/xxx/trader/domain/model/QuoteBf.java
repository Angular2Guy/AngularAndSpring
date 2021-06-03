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
package ch.xxx.trader.domain.model;

import java.math.BigDecimal;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document
public class QuoteBf implements Quote {

	@Id
	private ObjectId _id;
	@Indexed
	@JsonProperty
	private String pair;
	@Indexed
	@JsonProperty
	private Date createdAt = new Date();

	private final BigDecimal mid;
	private final BigDecimal bid;
	private final BigDecimal ask;
	private final BigDecimal last_price;
	private final BigDecimal low;
	private final BigDecimal high;
	private final BigDecimal volume;
	private final String timestamp;
	
	public QuoteBf(@JsonProperty("mid") BigDecimal mid,@JsonProperty("bid") BigDecimal bid,@JsonProperty("ask") BigDecimal ask,@JsonProperty("last_price") BigDecimal last_price,@JsonProperty("low") BigDecimal low,
			@JsonProperty("high") BigDecimal high,@JsonProperty("volume") BigDecimal volume,@JsonProperty("timestamp") String timestamp) {
		super();
		this.mid = mid;
		this.bid = bid;
		this.ask = ask;
		this.last_price = last_price;
		this.low = low;
		this.high = high;
		this.volume = volume;
		this.timestamp = timestamp;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getPair() {
		return pair;
	}

	public void setPair(String pair) {
		this.pair = pair;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public BigDecimal getMid() {
		return mid;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public BigDecimal getLast_price() {
		return last_price;
	}

	public BigDecimal getLow() {
		return low;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "QuoteBf [_id=" + _id + ", pair=" + pair + ", createdAt=" + createdAt + ", mid=" + mid + ", bid=" + bid
				+ ", ask=" + ask + ", last_price=" + last_price + ", low=" + low + ", high=" + high + ", volume="
				+ volume + ", timestamp=" + timestamp + "]";
	}
	
}
