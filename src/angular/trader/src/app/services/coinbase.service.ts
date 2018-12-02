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
import { PlatformLocation } from '@angular/common';
import { Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { QuoteBs } from '../common/quoteBs';
import { QuoteCbSmall, QuoteCb } from '../common/quoteCb';
import { QuoteIb } from '../common/quoteIb';
import { Utils } from './utils';

@Injectable({providedIn: 'root'})
export class CoinbaseService {
    private _reqOptionsArgs= { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
    private readonly _coinbase = '/coinbase';
    private _utils = new Utils();
    BTCUSD = 'btcusd';
    ETHUSD = 'ethusd';
    LTCUSD = 'ltcusd';
    
    constructor(private http: HttpClient, private pl: PlatformLocation ) { 
    }

    getCurrentQuote(): Observable<QuoteCb> {
        return this.http.get<QuoteCb>(this._coinbase+'/current', this._reqOptionsArgs).pipe(map(res => this.lowercaseKeys(<QuoteCb>res)), catchError(this._utils.handleError<QuoteCb>('getCurrentQuote')));
    }
    
    getTodayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get<QuoteCbSmall[]>(this._coinbase+'/today', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteCbSmall[]>('getTodayQuotes')));
    }
    
    get7DayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get<QuoteCbSmall[]>(this._coinbase+'/7days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteCbSmall[]>('get7DayQuotes')));
    }
    
    get30DayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get<QuoteCbSmall[]>(this._coinbase+'/30days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteCbSmall[]>('get30DayQuotes')));
    }
    
    get90DayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get<QuoteCbSmall[]>(this._coinbase+'/90days', this._reqOptionsArgs).pipe(catchError(this._utils.handleError<QuoteCbSmall[]>('get90DayQuotes')));
    }
    
    lowercaseKeys(quote: QuoteCb): QuoteCb {
        for (let p in quote) {
          if( quote.hasOwnProperty(p) && p !== '_id' && p !== 'createdAt') {
            quote[p.toLowerCase()] = quote[p];  
            //console.log( p + " , " + quote[p] + "\n");
            //console.log( p.toLowerCase() + " , " + quote[p.toLowerCase()] + "\n");
          } 
        }     
        return quote;
    }
}
