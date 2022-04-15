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
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { QuoteBs } from '../common/quote-bs';
import { QuoteCb } from '../common/quote-cb';
import { QuoteIb } from '../common/quote-ib';
import { Utils } from './utils';
import { OrderbookBs } from '../common/orderbook-bs';

@Injectable({providedIn: 'root'})
export class BitstampService {

    // eslint-disable-next-line @typescript-eslint/naming-convention
    BTCEUR = 'btceur';
// eslint-disable-next-line @typescript-eslint/naming-convention
    ETHEUR = 'etheur';
// eslint-disable-next-line @typescript-eslint/naming-convention
    LTCEUR = 'ltceur';
// eslint-disable-next-line @typescript-eslint/naming-convention
    XRPEUR = 'xrpeur';
// eslint-disable-next-line @typescript-eslint/naming-convention
    BTCUSD = 'btcusd';
// eslint-disable-next-line @typescript-eslint/naming-convention
    ETHUSD = 'ethusd';
// eslint-disable-next-line @typescript-eslint/naming-convention
    LTCUSD = 'ltcusd';
// eslint-disable-next-line @typescript-eslint/naming-convention
    XRPUSD = 'xrpusd';
    private reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
    private readonly bitstamp = '/bitstamp';
    private utils = new Utils();

    constructor(private http: HttpClient) {}

    getCurrentQuote(currencypair: string): Observable<QuoteBs> {
        return this.http.get<QuoteBs>(this.bitstamp+'/'+currencypair+'/current', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteBs>('getCurrentQuote')));
    }

    getTodayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return this.http.get<QuoteBs[]>(this.bitstamp+'/'+currencypair+'/today', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteBs[]>('getTodayQuotes')));
    }

    get7DayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return this.http.get<QuoteBs[]>(this.bitstamp+'/'+currencypair+'/7days', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteBs[]>('get7DayQuotes')));
    }

    get30DayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return this.http.get<QuoteBs[]>(this.bitstamp+'/'+currencypair+'/30days', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteBs[]>('get30DayQuotes')));
    }

    get90DayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return this.http.get<QuoteBs[]>(this.bitstamp+'/'+currencypair+'/90days', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteBs[]>('get90DayQuotes')));
    }

    getOrderbook(currencypair: string): Observable<OrderbookBs> {
        const reqOptions = {headers: this.utils.createTokenHeader()};
        return this.http.get<OrderbookBs>(this.bitstamp+'/'+currencypair+'/orderbook/', reqOptions)
			.pipe(catchError(this.utils.handleError<OrderbookBs>('getOrderbook')));
    }
}
