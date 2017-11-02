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
import { Http, Response, RequestOptionsArgs, Headers } from '@angular/http';
import { PlatformLocation } from '@angular/common';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/observable/throw';
import { QuoteBs } from '../common/quoteBs';
import { QuoteCbSmall, QuoteCb } from '../common/quoteCb';
import { QuoteIb } from '../common/quoteIb';
import { Utils } from './utils';

@Injectable()
export class CoinbaseService {
    private _reqOptionsArgs: RequestOptionsArgs = { headers: new Headers() };
    private readonly _coinbase = '/coinbase';
    private _utils = new Utils();
    BTCUSD = 'btcusd';
    ETHUSD = 'ethusd';
    LTCUSD = 'ltcusd';
    
    constructor(private http: Http, private pl: PlatformLocation ) { 
        this._reqOptionsArgs.headers.set( 'Content-Type', 'application/json' );
    }

    getCurrentQuote(): Observable<QuoteCb> {
        return this.http.get(this._coinbase+'/current', this._reqOptionsArgs).map(res => this.lowercaseKeys(res.json())).catch(this._utils.handleError);
    }
    
    getTodayQuotes(): Observable<QuoteCbSmall[]> {
        return this.http.get(this._coinbase+'/today', this._reqOptionsArgs).map(res => <QuoteCbSmall[]>res.json()).catch(this._utils.handleError);
    }
    
    private lowercaseKeys(quote: QuoteCb): QuoteCb {
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
