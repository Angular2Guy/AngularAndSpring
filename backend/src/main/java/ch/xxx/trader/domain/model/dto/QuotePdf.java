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
package ch.xxx.trader.domain.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public class QuotePdf {
	private BigDecimal last;
	private String pair;
	private BigDecimal volume;
	private Date timestamp;
	private BigDecimal bid;
	private BigDecimal ask;
	
	public QuotePdf() {
		
	}
	
	public QuotePdf(BigDecimal last, String pair, BigDecimal volume, Date timestamp, BigDecimal bid, BigDecimal ask) {
		super();
		this.last = last;
		this.pair = pair;
		this.volume = volume;
		this.timestamp = timestamp;
		this.bid = bid;
		this.ask = ask;
	}
	public BigDecimal getLast() {
		return last;
	}
	public void setLast(BigDecimal last) {
		this.last = last;
	}
	public String getPair() {
		return pair;
	}
	public void setPair(String pair) {
		this.pair = pair;
	}
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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
	
}
