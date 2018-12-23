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
import { trigger, state, animate, transition, style } from '@angular/animations';
import { Observable } from 'rxjs';
import { BitstampService } from '../../services/bitstamp.service';
import { QuoteBs } from '../../common/quoteBs';
import { CommonUtils } from '../../common/commonUtils';

@Component({
  selector: 'app-bsdetail',
  templateUrl: './bsdetail.component.html',
  styleUrls: ['./bsdetail.component.scss'],
  animations: [
               trigger('showChart', [
                 state('true' , style({ opacity: 1 })),
                 state('false', style({ opacity: 0 })),
                 transition('1 => 0', animate('300ms')),
                 transition('0 => 1', animate('300ms'))
               ])
             ]
})
export class BsdetailComponent implements OnInit {

    currQuote: QuoteBs;
    todayQuotes: QuoteBs[] = [];
    chartdata: number[] = [];
    chartlabels: string[] = [];
    chartType = "line";
    utils = new CommonUtils();
    currPair = "";    
    timeframe = this.utils.timeframes[0];
    
    constructor(private route: ActivatedRoute, private router: Router, private serviceBs: BitstampService) { }

    ngOnInit() { 
        this.route.params.subscribe(params => {            
        this.serviceBs.getCurrentQuote(params.currpair)
        .subscribe(quote => {
            this.currQuote = quote;
            this.currPair = this.utils.getCurrpairName(this.currQuote.pair);});        
        this.serviceBs.getTodayQuotes(this.route.snapshot.paramMap.get('currpair'))
        .subscribe(quotes => {
            this.todayQuotes = quotes;
            this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getHours().toString());
            this.chartdata = this.todayQuotes.map(quote => quote.last);
            });
        });
    }  
    
    changeTf() {
        this.chartdata = [];
        this.chartlabels = [];
        const currpair = this.route.snapshot.paramMap.get('currpair');
        let quoteObserv: Observable<QuoteBs[]>;
            if(this.timeframe === this.utils.timeframes[1]) quoteObserv = this.serviceBs.get7DayQuotes(currpair);
            else if(this.timeframe === this.utils.timeframes[2]) quoteObserv = this.serviceBs.get30DayQuotes(currpair);
            else if(this.timeframe === this.utils.timeframes[3]) quoteObserv = this.serviceBs.get90DayQuotes(currpair) 
                else quoteObserv = this.serviceBs.getTodayQuotes(currpair);
        
        quoteObserv.subscribe(quotes => {
            this.todayQuotes = quotes;
            if(this.timeframe === this.utils.timeframes[2] || this.timeframe === this.utils.timeframes[3]) 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getUTCDate().toString())            
            else if(this.timeframe === this.utils.timeframes[1]) 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getDay().toString())
             else 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getHours().toString());                                       
            this.chartdata = this.todayQuotes.map(quote => quote.last);
            });
    }
    
    showReport() {
        const currpair = this.route.snapshot.paramMap.get('currpair');
        let url = '/bitstamp' + this.utils.createReportUrl(this.timeframe, currpair);
        window.open(url);
    }
    
}
