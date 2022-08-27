/*
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
export enum StatisticCurrencyPair {bcUsd='BcUsd', ethUsd='EthUsd', lcUsd='LcUsd', rpUsd='RpUsd'}

export enum CoinExchange { bitfinex='Bitfinex', bitstamp='Bitstamp' }

export class CommonStatistics {
	currPair: StatisticCurrencyPair;
	performance1Month: number;
	performance3Month: number;
	performance6Month: number;
	performance1Year: number;
	performance2Year: number;
	performance5Year: number;
	volatility1Month: number;
	volatility3Month: number;
	volatility6Month: number;
	volatility1Year: number;
	volatility2Year: number;
	volatility5Year: number;
	avgVolume1Month: number;
	avgVolume3Month: number;
	avgVolume6Month: number;
	avgVolume1Year: number;
	avgVolume2Year: number;
	avgVolume5Year: number;
	range1Month: RangeValues;
	range3Month: RangeValues;
	range6Month: RangeValues;
	range1Year: RangeValues;
	range2Year: RangeValues;
	range5Year: RangeValues;
}

export class RangeValues {
	start: number;
	end: number;
}
