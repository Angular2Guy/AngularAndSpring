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
import { QuoteCb, QuoteCbSmall } from '../common/quoteCb';
import { CoinbaseService } from '../services/coinbase.service';
import { CommonUtils } from '../common/commonUtils';

@Component({
  selector: 'app-cbdetail',
  templateUrl: './cbdetail.component.html',
  styleUrls: ['./cbdetail.component.scss']
})
export class CbdetailComponent implements OnInit {

    currQuote: QuoteCb;
    todayQuotes: QuoteCbSmall[] = [];
    chartdata: number[] = [];
    chartlabels: string[] = [];
    chartType = "line";
    currpair: string;
    myCurrPair = "";
    private utils = new CommonUtils();
    BTCUSD: string;
    ETHUSD: string;
    LTCUSD: string
    
    constructor(private route: ActivatedRoute, private router: Router, private serviceCb: CoinbaseService) { 
        this.BTCUSD = this.serviceCb.BTCUSD;
        this.ETHUSD = this.serviceCb.ETHUSD;
        this.LTCUSD = this.serviceCb.LTCUSD;
    }    

    ngOnInit() {
        this.route.paramMap
            .switchMap((params: ParamMap) => { 
                this.currpair = params.get('currpair');
                this.myCurrPair = this.utils.getCurrpairName(this.currpair);
                return this.serviceCb.getCurrentQuote();})
            .subscribe(quote => this.currQuote = quote);
        this.route.paramMap
            .switchMap((params: ParamMap) => {
                this.currpair = params.get('currpair'); 
                return this.serviceCb.getTodayQuotes();})
            .subscribe(quotes => {
                this.todayQuotes = quotes;
                this.chartlabels = this.todayQuotes.map(quote => new Date(quote.createdAt).getMinutes().toString());
                if(this.currpair === this.serviceCb.BTCUSD) {
                    this.chartdata = this.todayQuotes.map(quote => quote.usd);
                } else if(this.currpair === this.serviceCb.ETHUSD) {
                    this.chartdata = this.todayQuotes.map(quote => quote.usd/quote.eth)
                } else if(this.currpair === this.serviceCb.LTCUSD) {
                    this.chartdata = this.todayQuotes.map(quote => quote.usd/quote.ltc)
                }
            });
    }

}
