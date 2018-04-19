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
package dtos;

import java.math.BigDecimal;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document
public class QuoteBs implements Quote {
	
	@Id
	private ObjectId _id;
	@JsonProperty
	private String pair;
	@JsonProperty
	private Date createdAt = new Date();
	private final BigDecimal high;
	private final BigDecimal last;
	private final Date timestamp;
	private final BigDecimal bid;
	private final BigDecimal vwap;
	private final BigDecimal volume;
	private final BigDecimal low;
	private final BigDecimal ask;
	private final BigDecimal open;
	
	public QuoteBs(@JsonProperty("high") BigDecimal high,@JsonProperty("last") BigDecimal last,@JsonProperty("timestamp") Date timestamp,@JsonProperty("bid") BigDecimal bid,@JsonProperty("vwap") BigDecimal vwap,@JsonProperty("volume") BigDecimal volume,
			@JsonProperty("low") BigDecimal low,@JsonProperty("ask") BigDecimal ask, @JsonProperty("open") BigDecimal open) {
		super();
		this.high = high;
		this.last = last;
		this.timestamp = timestamp;
		this.bid = bid;
		this.vwap = vwap;
		this.volume = volume;
		this.low = low;
		this.ask = ask;
		this.open = open;		
	}

	public BigDecimal getHigh() {
		return high;
	}

	public BigDecimal getLast() {
		return last;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public BigDecimal getVwap() {
		return vwap;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public BigDecimal getLow() {
		return low;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public BigDecimal getOpen() {
		return open;
	}	

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getPair() {
		return pair;
	}

	public void setPair(String pair) {
		this.pair = pair;
	}

	@Override
	public String toString() {
		return "QuoteBs [_id=" + _id + ", pair=" + pair + ", createdAt=" + createdAt + ", high=" + high + ", last="
				+ last + ", timestamp=" + timestamp + ", bid=" + bid + ", vwap=" + vwap + ", volume=" + volume
				+ ", low=" + low + ", ask=" + ask + ", open=" + open + "]";
	}
	
	
}
