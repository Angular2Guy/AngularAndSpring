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
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { QuoteBf } from '../common/quoteBf';
import { Utils } from './utils';
import { OrderbookBf } from '../common/orderbookBf';

@Injectable({providedIn: 'root'})
export class BitfinexService {
  private _reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
  private readonly _bitfinex = '/bitfinex';  
  BTCUSD = 'btcusd';
  ETHUSD = 'ethusd';
  LTCUSD = 'ltcusd';
  XRPUSD = 'xrpusd';
  
  private _utils = new Utils();
  
  constructor(private http: HttpClient) { 
  }

  getCurrentQuote(currencypair: string): Observable<QuoteBf> { 
      return this.http.get<QuoteBf>(this._bitfinex+'/'+currencypair+'/current', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBf>('getCurrentQuote')));
  }
   
  getTodayQuotes(currencypair: string): Observable<QuoteBf[]> {
      return this.http.get<QuoteBf[]>(this._bitfinex+'/'+currencypair+'/today', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBf[]>('getTodayQuotes')));
  }

  get7DayQuotes(currencypair: string): Observable<QuoteBf[]> {      
      return this.http.get<QuoteBf[]>(this._bitfinex+'/'+currencypair+'/7days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBf[]>('get7DayQuotes')));
  }

  get30DayQuotes(currencypair: string): Observable<QuoteBf[]> {
      return this.http.get<QuoteBf[]>(this._bitfinex+'/'+currencypair+'/30days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBf[]>('get30DayQuotes')));
  }
  
  get90DayQuotes(currencypair: string): Observable<QuoteBf[]> {
      return this.http.get<QuoteBf[]>(this._bitfinex+'/'+currencypair+'/90days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBf[]>('get90DayQuotes')));
  }
  
  getOrderbook(currencypair: string): Observable<OrderbookBf> {
      let reqOptions = {headers: this._utils.createTokenHeader()};
      return this.http.get<OrderbookBf>(this._bitfinex+'/'+currencypair+'/orderbook/', reqOptions).pipe(catchError(this._utils.handleError<OrderbookBf>('getOrderbook')));
  }
}
