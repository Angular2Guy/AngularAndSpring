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
package ch.xxx.trader.domain.model.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document
public class QuoteBf implements Quote, QuotePairBf {

	@Id
	private ObjectId _id;
	@NotBlank
	@Indexed()
	@JsonProperty
	private String pair;
	@NotNull
	@Indexed(name = "QuoteBf-createdAt")
	@JsonProperty
	private Date createdAt = new Date();

	private BigDecimal mid;
	private BigDecimal bid;
	private BigDecimal ask;
	private BigDecimal last_price;
	private BigDecimal low;
	private BigDecimal high;
	private BigDecimal volume;
	private String timestamp;
	
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

	public void setMid(BigDecimal mid) {
		this.mid = mid;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

	public BigDecimal getLast_price() {
		return last_price;
	}

	public void setLast_price(BigDecimal last_price) {
		this.last_price = last_price;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "QuoteBf [_id=" + _id + ", pair=" + pair + ", createdAt=" + createdAt + ", mid=" + mid + ", bid=" + bid
				+ ", ask=" + ask + ", last_price=" + last_price + ", low=" + low + ", high=" + high + ", volume="
				+ volume + ", timestamp=" + timestamp + "]";
	}
	
}
