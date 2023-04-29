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

import java.util.Date;

public class PaxosQuote {	
	private String market;
	private PaxosPrice bestBid;
	private PaxosPrice bestAsk;
	private PaxosPrice lastExecution;
	private PaxosDay today;
	private PaxosDay lastDay;
	private Date snapshotAt;

	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public PaxosPrice getBestBid() {
		return bestBid;
	}
	public void setBestBid(PaxosPrice bestBid) {
		this.bestBid = bestBid;
	}
	public PaxosPrice getBestAsk() {
		return bestAsk;
	}
	public void setBestAsk(PaxosPrice bestAsk) {
		this.bestAsk = bestAsk;
	}
	public PaxosPrice getLastExecution() {
		return lastExecution;
	}
	public void setLastExecution(PaxosPrice lastExecution) {
		this.lastExecution = lastExecution;
	}
	public PaxosDay getToday() {
		return today;
	}
	public void setToday(PaxosDay today) {
		this.today = today;
	}
	public PaxosDay getLastDay() {
		return lastDay;
	}
	public void setLastDay(PaxosDay lastDay) {
		this.lastDay = lastDay;
	}
	public Date getSnapshotAt() {
		return snapshotAt;
	}
	public void setSnapshotAt(Date snapshotAt) {
		this.snapshotAt = snapshotAt;
	}
}
