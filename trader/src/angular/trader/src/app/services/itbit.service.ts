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
import { QuoteCb } from '../common/quoteCb';
import { QuoteIb } from '../common/quoteIb';
import { Utils } from './utils';
import { OrderbookIb } from '../common/orderbookIb';

@Injectable()
export class ItbitService {
    private _reqOptionsArgs: RequestOptionsArgs = { headers: new Headers() };
    private readonly _itbit = '/itbit';
    private readonly _itbitOb = '';
    private _utils = new Utils();
    BTCEUR = 'btceur';
    BTCUSD = 'btcusd';
    
    constructor(private http: Http, private pl: PlatformLocation ) { 
        this._reqOptionsArgs.headers.set( 'Content-Type', 'application/json' );
    }

    getCurrentQuote(currencypair: string): Observable<QuoteIb> {
        return this.http.get(this._itbit+'/'+currencypair+'/current', this._reqOptionsArgs).map(res => <QuoteIb>res.json()).catch(this._utils.handleError);
    }
    
    getTodayQuotes(currencypair: string): Observable<QuoteIb[]> {
        return this.http.get(this._itbit+'/'+currencypair+'/today', this._reqOptionsArgs).map(res => <QuoteIb[]>res.json()).catch(this._utils.handleError);
    }
    
    getOrderbook(currencypair: string): Observable<OrderbookIb> {
        return this.http.get(this._itbitOb+'/'+currencypair, this._reqOptionsArgs).map(res => <OrderbookIb>res.json()).catch(this._utils.handleError);
    }
}
