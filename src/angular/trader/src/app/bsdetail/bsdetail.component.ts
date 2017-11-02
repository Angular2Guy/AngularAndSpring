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
import { BitstampService } from '../services/bitstamp.service';
import { QuoteBs } from '../common/quoteBs';
import { CommonUtils } from '../common/commonUtils';

@Component({
  selector: 'app-bsdetail',
  templateUrl: './bsdetail.component.html',
  styleUrls: ['./bsdetail.component.scss']
})
export class BsdetailComponent implements OnInit {

    currQuote: QuoteBs;
    todayQuotes: QuoteBs[] = [];
    chartdata: number[] = [];
    chartlabels: string[] = [];
    chartType = "line";
    private utils = new CommonUtils();
    currPair = "";
    
    constructor(private route: ActivatedRoute, private router: Router, private serviceBs: BitstampService) { }

    ngOnInit() {
        this.route.paramMap
        .switchMap((params: ParamMap) => this.serviceBs.getCurrentQuote(params.get('currpair')))
        .subscribe(quote => {
            this.currQuote = quote;
            this.currPair = this.utils.getCurrpairName(this.currQuote.pair);});
        this.route.paramMap
        .switchMap((params: ParamMap) => this.serviceBs.getTodayQuotes(params.get('currpair')))
        .subscribe(quotes => {
            this.todayQuotes = quotes;
            this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getMinutes().toString());
            this.chartdata = this.todayQuotes.map(quote => quote.last);
            });
    }              
    
}
