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

public class StatisticsCommon {
	public static enum StatisticsCurrPair {
		BcUsd("btcusd","btcusd"), EthUsd("ethusd","ethusd"), LcUsd("ltcusd","ltcusd"), RpUsd("xrpusd","xrpusd");
		
		private String bitStampKey;
		private String bitfinexKey;
		
		StatisticsCurrPair(String bitStampKey, String bitfinexKey) {
			this.bitStampKey = bitStampKey;
			this.bitfinexKey = bitfinexKey;
		}

		public String getBitStampKey() {
			return bitStampKey;
		}

		public String getBitfinexKey() {
			return bitfinexKey;
		}
	}

	public enum CoinExchange {
		Bitfinex, Bitstamp
	}
}
