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
import { Component, OnInit, LOCALE_ID, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { trigger, state, animate, transition, style } from '@angular/animations';
import { Observable } from 'rxjs';
import { QuoteCb, QuoteCbSmall } from '../../common/quote-cb';
import { CoinbaseService } from '../../services/coinbase.service';
import { DetailBase, Tuple } from 'src/app/common/detail-base';

@Component( {
    selector: 'app-cbdetail',
    templateUrl: './cbdetail.component.html',
    styleUrls: ['./cbdetail.component.scss'],
    animations: [
        trigger( 'showChart', [
            state( 'true', style( { opacity: 1 } ) ),
            state( 'false', style( { opacity: 0 } ) ),
            transition( '1 => 0', animate( '300ms' ) ),
            transition( '0 => 1', animate( '300ms' ) )
        ] )
    ]
} )
export class CbdetailComponent extends DetailBase implements OnInit {

    currQuote: QuoteCb;
    todayQuotes: QuoteCbSmall[] = [];
    currpair: string;
    myCurrPair = "";
    BTCUSD: string;
    ETHUSD: string;
    LTCUSD: string;

    constructor( private route: ActivatedRoute, private router: Router, private serviceCb: CoinbaseService, 
		@Inject(LOCALE_ID) private myLocale: string) { 
		super(myLocale);
        this.BTCUSD = this.serviceCb.BTCUSD;
        this.ETHUSD = this.serviceCb.ETHUSD;
        this.LTCUSD = this.serviceCb.LTCUSD;
    }

    ngOnInit() {
        this.route.params.subscribe( params => {
            this.currpair = params.currpair;
            this.myCurrPair = this.utils.getCurrpairName( this.currpair );
            this.serviceCb.getCurrentQuote()
                .subscribe( quote => this.currQuote = quote );
            this.serviceCb.getTodayQuotes()
                .subscribe( quotes => {
                    this.todayQuotes = quotes;
                    if ( this.currpair === this.serviceCb.BTCUSD ) {
                        this.updateChartData(quotes.map( quote => new Tuple<string, number>(quote.createdAt, quote.usd)));
                    } else if ( this.currpair === this.serviceCb.ETHUSD ) {
                        this.updateChartData(quotes.map( quote => new Tuple<string, number>(quote.createdAt, (quote.usd / quote.eth))));
                    } else if ( this.currpair === this.serviceCb.LTCUSD ) {
                        this.updateChartData(quotes.map( quote => new Tuple<string, number>(quote.createdAt, (quote.usd / quote.ltc ))));
                    }
                } );
        } );
    }

    changeTf() {
        this.currpair = this.route.snapshot.paramMap.get( 'currpair' );
        let quoteObserv: Observable<QuoteCbSmall[]>;
        if ( this.timeframe === this.utils.timeframes[1] ) quoteObserv = this.serviceCb.get7DayQuotes();
        else if ( this.timeframe === this.utils.timeframes[2] ) quoteObserv = this.serviceCb.get30DayQuotes();
        else if ( this.timeframe === this.utils.timeframes[3] ) quoteObserv = this.serviceCb.get90DayQuotes()
        else quoteObserv = this.serviceCb.getTodayQuotes();

        quoteObserv.subscribe( quotes => {
            this.todayQuotes = quotes;

            if ( this.currpair === this.serviceCb.BTCUSD ) {
                this.updateChartData(quotes.map( quote => new Tuple<string, number>(quote.createdAt, quote.usd)));
            } else if ( this.currpair === this.serviceCb.ETHUSD ) {
                this.updateChartData(quotes.map( quote => new Tuple<string, number>(quote.createdAt, (quote.usd / quote.eth))));
            } else if ( this.currpair === this.serviceCb.LTCUSD ) {
               this.updateChartData(quotes.map( quote => new Tuple<string, number>(quote.createdAt, (quote.usd / quote.ltc ))));
            }
        } );
    }

}
