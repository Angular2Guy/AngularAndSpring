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
import { QuoteCbSmall, QuoteCb } from '../common/quote-cb';
import { QuoteIb } from '../common/quote-ib';
import { Utils } from './utils';

@Injectable({providedIn: 'root'})
export class CoinbaseService {
    // eslint-disable-next-line @typescript-eslint/naming-convention
    BTCUSD = 'btcusd';
    // eslint-disable-next-line @typescript-eslint/naming-convention
    ETHUSD = 'ethusd';
    // eslint-disable-next-line @typescript-eslint/naming-convention
    LTCUSD = 'ltcusd';
    private reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
    private readonly coinbase = '/coinbase';
    private utils = new Utils();

    constructor(private http: HttpClient) {}

    getCurrentQuote(): Observable<QuoteCb> {
        return this.http.get<QuoteCb>(this.coinbase+'/current', this.reqOptionsArgs)
			.pipe(map(res => this.lowercaseKeys(res as QuoteCb)),
				catchError(this.utils.handleError<QuoteCb>('getCurrentQuote')));
    }

    getTodayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get<QuoteCbSmall[]>(this.coinbase+'/today', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteCbSmall[]>('getTodayQuotes')));
    }

    get7DayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get<QuoteCbSmall[]>(this.coinbase+'/7days', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteCbSmall[]>('get7DayQuotes')));
    }

    get30DayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get<QuoteCbSmall[]>(this.coinbase+'/30days', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteCbSmall[]>('get30DayQuotes')));
    }

    get90DayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get<QuoteCbSmall[]>(this.coinbase+'/90days', this.reqOptionsArgs)
			.pipe(catchError(this.utils.handleError<QuoteCbSmall[]>('get90DayQuotes')));
    }

    lowercaseKeys(quote: QuoteCb): QuoteCb {
        for (const p in quote) {
          if( quote.hasOwnProperty(p) && p !== '_id' && p !== 'createdAt') {
            quote[p.toLowerCase()] = quote[p];
            //console.log( p + " , " + quote[p] + "\n");
            //console.log( p.toLowerCase() + " , " + quote[p.toLowerCase()] + "\n");
          }
        }
        return quote;
    }
}
