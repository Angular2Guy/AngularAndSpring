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
import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, ParamMap } from '@angular/router';
import 'rxjs/add/operator/switchMap';
import { BitfinexService } from '../services/bitfinex.service';
import { QuoteBf } from '../common/quoteBf';
import { CommonUtils } from '../common/commonUtils';

@Component({
  selector: 'app-bfdetail',
  templateUrl: './bfdetail.component.html',
  styleUrls: ['./bfdetail.component.scss']
})
export class BfdetailComponent implements OnInit {

    currQuote: QuoteBf;
    todayQuotes: QuoteBf[] = [];
    chartdata: number[] = [];
    chartlabels: string[] = [];
    chartType = "line";
    utils = new CommonUtils();
    currPair = "";
    timeframe = this.utils.timeframes[0];

    constructor(private route: ActivatedRoute, private router: Router, private serviceBf: BitfinexService) { }

    ngOnInit() {
        this.route.paramMap
        .switchMap((params: ParamMap) => this.serviceBf.getCurrentQuote(params.get('currpair')))
        .subscribe(quote => {
            this.currQuote = quote;
            this.currPair = this.utils.getCurrpairName(this.currQuote.pair);});
        this.route.paramMap
            .switchMap((params: ParamMap) => this.serviceBf.getTodayQuotes(params.get('currpair')))
            .subscribe(quotes => {
                this.todayQuotes = quotes;
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getMinutes().toString());
                this.chartdata = this.todayQuotes.map(quote => quote.last_price);
        });
    }  

    changeTf() {
        this.chartdata = [];
        this.chartlabels = [];
        this.route.paramMap
        .switchMap((params: ParamMap) => {            
            if(this.timeframe === this.utils.timeframes[1]) return this.serviceBf.get7DayQuotes(params.get('currpair'));
            if(this.timeframe === this.utils.timeframes[2]) return this.serviceBf.get30DayQuotes(params.get('currpair'));
            if(this.timeframe === this.utils.timeframes[3]) return this.serviceBf.get90DayQuotes(params.get('currpair')) 
                else return this.serviceBf.getTodayQuotes(params.get('currpair'));
        })            
        .subscribe(quotes => { 
            this.todayQuotes = quotes;
            if(this.timeframe === this.utils.timeframes[2] || this.timeframe === this.utils.timeframes[3]) 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getDay().toString())            
            else if(this.timeframe === this.utils.timeframes[1]) 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getHours().toString())
             else 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getMinutes().toString());                                       
            this.chartdata = this.todayQuotes.map(quote => quote.last_price);
            });
    }
}
