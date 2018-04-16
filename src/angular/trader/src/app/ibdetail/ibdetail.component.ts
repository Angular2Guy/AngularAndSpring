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
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import 'rxjs/add/operator/switchMap';
import { QuoteIb } from '../common/quoteIb';
import { ItbitService} from '../services/itbit.service';
import { CommonUtils } from '../common/commonUtils';

@Component({
  selector: 'app-ibdetail',
  templateUrl: './ibdetail.component.html',
  styleUrls: ['./ibdetail.component.scss']
})
export class IbdetailComponent implements OnInit {
    currQuote: QuoteIb;
    todayQuotes: QuoteIb[] = [];
    chartdata: number[] = [];
    chartlabels: string[] = [];
    chartType = "line";
    utils = new CommonUtils();
    currPair = "";
    timeframe = this.utils.timeframes[0];
    
    constructor(private route: ActivatedRoute, private router: Router, private serviceIb: ItbitService) { }

    ngOnInit() {
        this.route.paramMap
            .switchMap((params: ParamMap) => {
                this.currPair = this.utils.getCurrpairName(params.get('currpair'));
                return this.serviceIb.getCurrentQuote(params.get('currpair'));})
            .subscribe(quote => this.currQuote = quote);
        this.route.paramMap
            .switchMap((params: ParamMap) => this.serviceIb.getTodayQuotes(params.get('currpair')))
            .subscribe(quotes => {
                this.todayQuotes = quotes;
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getMinutes().toString());
                this.chartdata = this.todayQuotes.map(quote => quote.lastPrice);
        });
    }
    
    changeTf() {
        this.chartdata = [];
        this.chartlabels = [];
        this.route.paramMap
        .switchMap((params: ParamMap) => {           
            if(this.timeframe === this.utils.timeframes[1]) return this.serviceIb.get7DayQuotes(params.get('currpair'));
            if(this.timeframe === this.utils.timeframes[2]) return this.serviceIb.get30DayQuotes(params.get('currpair'));
            if(this.timeframe === this.utils.timeframes[3]) return this.serviceIb.get90DayQuotes(params.get('currpair')) 
                else return this.serviceIb.getTodayQuotes(params.get('currpair'));
        })            
        .subscribe(quotes => { 
            this.todayQuotes = quotes;
            if(this.timeframe === this.utils.timeframes[2] || this.timeframe === this.utils.timeframes[3]) 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getDay().toString())            
            else if(this.timeframe === this.utils.timeframes[1]) 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getHours().toString())
             else 
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getMinutes().toString());                                       
            this.chartdata = this.todayQuotes.map(quote => quote.lastPrice);
            });
    }
}
