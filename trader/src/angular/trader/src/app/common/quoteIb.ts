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
export interface QuoteIb {
    _id: string;
    createdAt: Date;
    pair: string;
    bid: number;
    bidAmt: number;
    ask: number;
    askAmt: number;
    lastPrice: number;
    stAmt: number;
    volume24h: number;
    volumeToday: number;
    high24h: number;
    low24h: number;
    openToday: number;
    vwapToday: number;
    vwap24h: number;
    serverTimeUTC: Date;
}