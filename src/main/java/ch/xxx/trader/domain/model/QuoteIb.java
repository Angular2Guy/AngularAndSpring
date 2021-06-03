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
public class QuoteIb implements Quote {
	
	@Id
	private ObjectId _id;
	@Indexed
	@JsonProperty
	private Date createdAt = new Date();		
	private final String pair;
	private final BigDecimal bid;
	private final BigDecimal bidAmt;
	private final BigDecimal ask;
	private final BigDecimal askAmt;
	private final BigDecimal lastPrice;
	private final BigDecimal stAmt;
	private final BigDecimal volume24h;
	private final BigDecimal volumeToday;
	private final BigDecimal high24h;
	private final BigDecimal low24h;
	private final BigDecimal openToday;
	private final BigDecimal vwapToday;
	private final BigDecimal vwap24h;
	private final Date serverTimeUTC;
	
	
	
	public QuoteIb(@JsonProperty("pair") String pair,@JsonProperty("bid")  BigDecimal bid, @JsonProperty("bidAmt") BigDecimal bidAmt,@JsonProperty("ask")  BigDecimal ask,@JsonProperty("askAmt")  BigDecimal askAmt,
			@JsonProperty("lastPrice") BigDecimal lastPrice,@JsonProperty("stAmt") BigDecimal stAmt, @JsonProperty("volume24h") BigDecimal volume24h,@JsonProperty("volumeToday") BigDecimal volumeToday,@JsonProperty("high24h") BigDecimal high24h,
			@JsonProperty("low24h") BigDecimal low24h,@JsonProperty("openToday") BigDecimal openToday,@JsonProperty("vwapToday") BigDecimal vwapToday,@JsonProperty("vwap24h") BigDecimal vwap24h,@JsonProperty("serverTimeUTC") Date serverTimeUTC) {
		super();
		this.pair = pair;
		this.bid = bid;
		this.bidAmt = bidAmt;
		this.ask = ask;
		this.askAmt = askAmt;
		this.lastPrice = lastPrice;
		this.stAmt = stAmt;
		this.volume24h = volume24h;
		this.volumeToday = volumeToday;
		this.high24h = high24h;
		this.low24h = low24h;
		this.openToday = openToday;
		this.vwapToday = vwapToday;
		this.vwap24h = vwap24h;
		this.serverTimeUTC = serverTimeUTC;
	}
	public String getPair() {
		return pair;
	}
	public BigDecimal getBid() {
		return bid;
	}
	public BigDecimal getBidAmt() {
		return bidAmt;
	}
	public BigDecimal getAsk() {
		return ask;
	}
	public BigDecimal getAskAmt() {
		return askAmt;
	}
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	public BigDecimal getStAmt() {
		return stAmt;
	}
	public BigDecimal getVolume24h() {
		return volume24h;
	}
	public BigDecimal getVolumeToday() {
		return volumeToday;
	}
	public BigDecimal getHigh24h() {
		return high24h;
	}
	public BigDecimal getLow24h() {
		return low24h;
	}
	public BigDecimal getOpenToday() {
		return openToday;
	}
	public BigDecimal getVwapToday() {
		return vwapToday;
	}
	public BigDecimal getVwap24h() {
		return vwap24h;
	}
	public Date getServerTimeUTC() {
		return serverTimeUTC;
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
	@Override
	public String toString() {
		return "QuoteIb [_id=" + _id + ", createdAt=" + createdAt + ", pair=" + pair + ", bid=" + bid + ", bidAmt="
				+ bidAmt + ", ask=" + ask + ", askAmt=" + askAmt + ", lastPrice=" + lastPrice + ", stAmt=" + stAmt
				+ ", volume24h=" + volume24h + ", volumeToday=" + volumeToday + ", high24h=" + high24h + ", low24h="
				+ low24h + ", openToday=" + openToday + ", vwapToday=" + vwapToday + ", vwap24h=" + vwap24h
				+ ", serverTimeUTC=" + serverTimeUTC + "]";
	}
}
