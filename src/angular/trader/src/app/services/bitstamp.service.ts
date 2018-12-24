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
import { QuoteBs } from '../common/quoteBs';
import { QuoteCb } from '../common/quoteCb';
import { QuoteIb } from '../common/quoteIb';
import { Utils } from './utils';
import { OrderbookBs } from '../common/orderbookBs';

@Injectable({providedIn: 'root'})
export class BitstampService {    
   
    private _reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
    private readonly _bitstamp = '/bitstamp';    
    BTCEUR = 'btceur';
    ETHEUR = 'etheur';
    LTCEUR = 'ltceur';
    XRPEUR = 'xrpeur';
    BTCUSD = 'btcusd';
    ETHUSD = 'ethusd';
    LTCUSD = 'ltcusd';
    XRPUSD = 'xrpusd';
    private _utils = new Utils(); 

    constructor(private http: HttpClient) { 
    }

    getCurrentQuote(currencypair: string): Observable<QuoteBs> {
        return this.http.get<QuoteBs>(this._bitstamp+'/'+currencypair+'/current', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBs>('getCurrentQuote')));
    }
     
    getTodayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return this.http.get<QuoteBs[]>(this._bitstamp+'/'+currencypair+'/today', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBs[]>('getTodayQuotes')));
    }
    
    get7DayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return this.http.get<QuoteBs[]>(this._bitstamp+'/'+currencypair+'/7days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBs[]>('get7DayQuotes')));
    }
    
    get30DayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return this.http.get<QuoteBs[]>(this._bitstamp+'/'+currencypair+'/30days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBs[]>('get30DayQuotes')));
    }
    
    get90DayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return this.http.get<QuoteBs[]>(this._bitstamp+'/'+currencypair+'/90days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteBs[]>('get90DayQuotes')));
    }
    
    getOrderbook(currencypair: string): Observable<OrderbookBs> {
        let reqOptions = {headers: this._utils.createTokenHeader()};
        return this.http.get<OrderbookBs>(this._bitstamp+'/'+currencypair+'/orderbook/', reqOptions).pipe(catchError(this._utils.handleError<OrderbookBs>('getOrderbook')));
    }
}
