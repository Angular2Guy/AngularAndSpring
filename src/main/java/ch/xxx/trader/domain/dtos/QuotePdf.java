package ch.xxx.trader.domain.dtos;

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
