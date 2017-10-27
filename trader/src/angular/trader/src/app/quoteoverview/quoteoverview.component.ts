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
import { Component, OnInit, OnDestroy } from '@angular/core';
import { BitstampService } from '../services/bitstamp.service';
import { CoinbaseService } from '../services/coinbase.service';
import { ItbitService } from '../services/itbit.service';
import { BitfinexService } from '../services/bitfinex.service';
import { QuoteBs } from '../common/quoteBs';
import { QuoteCb } from '../common/quoteCb';
import { QuoteIb } from '../common/quoteIb';
import { QuoteBf } from '../common/quoteBf';
import { Observable } from 'rxjs/Observable';
import {DataSource, CollectionViewer} from '@angular/cdk/collections';
import 'rxjs/add/observable/of';
import {Subject} from 'rxjs/Subject';
import { Router } from '@angular/router';
import { CommonUtils } from "../common/commonUtils";
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { LoginComponent } from '../login/login.component';
import { MyuserService } from '../services/myuser.service';

@Component({
  selector: 'app-quoteoverview',
  templateUrl: './quoteoverview.component.html',
  styleUrls: ['./quoteoverview.component.scss']
})
export class QuoteoverviewComponent implements OnInit,OnDestroy {        
    
    datasource = new Myds();    
    private interval: any;
    private utils = new CommonUtils();
    hash = null;    

    constructor(private router: Router, 
            private serviceBs: BitstampService, 
            private serviceCb: CoinbaseService, 
            private serviceIb: ItbitService, 
            private serviceBf: BitfinexService,
            private serviceMu: MyuserService,
            public dialog: MatDialog) {        
    }

    ngOnInit() {
      this.refreshData();
      if(this.interval){
          clearInterval(this.interval);
      }
      this.interval = setInterval(() => {
          this.refreshData();
      }, 15000);
      for(let i = 0;i<17;i++) {
          this.datasource.rows.push(new Myrow("","",0,0, null));
      }
      this.hash = this.serviceMu.salt;
      console.log(this.hash);
    }  
     
    ngOnDestroy(): void {
      if(this.interval){
          clearInterval(this.interval);
      }
    }
    
    openLoginDialog(): void {
      let dialogRef = this.dialog.open(LoginComponent, {
        width: '500px',
        data: { hash: this.hash}
      });
    
      dialogRef.afterClosed().subscribe(result => {        
        this.hash = result;
      });  
    }
    
    logout():void {
        this.hash = null;
        this.serviceMu.logout();
    }
    
    orderbooks(): void {      
      this.router.navigateByUrl("orderbooks");
    }
      
    selectedRow(row: Myrow):void {
      console.log(row);
      if(row.exchange === 'Bitstamp') {
          this.router.navigateByUrl("bsdetail/"+row.pair);
      } else if(row.exchange === 'Itbit' && row.pair === 'XBTUSD') {
          this.router.navigateByUrl("ibdetail/"+this.serviceIb.BTCUSD);
      } else if (row.exchange === 'Itbit' && row.pair === 'XBTEUR') {
          this.router.navigateByUrl("ibdetail/"+this.serviceIb.BTCEUR);
      } else if(row.exchange === 'Coinbase') {
          this.router.navigateByUrl("cbdetail/"+row.pair);
      } else if(row.exchange === 'Bitfinex') {
          this.router.navigateByUrl("bfdetail/"+row.pair);
      }
    }
      
    private refreshData() {
        this.serviceBs.getCurrentQuote(this.serviceBs.BTCEUR).subscribe(quote => { 
            this.datasource.rows[0] = this.createRowBs(quote, "Bitstamp", this.utils.getCurrpairName(this.serviceBs.BTCEUR));
            this.datasource.updateRows();});
        this.serviceBs.getCurrentQuote(this.serviceBs.ETHEUR).subscribe(quote => { 
            this.datasource.rows[1] = this.createRowBs(quote, "Bitstamp", this.utils.getCurrpairName(this.serviceBs.ETHEUR));
            this.datasource.updateRows();});
        this.serviceBs.getCurrentQuote(this.serviceBs.LTCEUR).subscribe(quote => { 
            this.datasource.rows[2] = this.createRowBs(quote, "Bitstamp", this.utils.getCurrpairName(this.serviceBs.LTCEUR));
            this.datasource.updateRows();});
        this.serviceBs.getCurrentQuote(this.serviceBs.XRPEUR).subscribe(quote => { 
            this.datasource.rows[3] = this.createRowBs(quote, "Bitstamp", this.utils.getCurrpairName(this.serviceBs.XRPEUR));
            this.datasource.updateRows();});
        this.serviceBs.getCurrentQuote(this.serviceBs.BTCUSD).subscribe(quote => { 
            this.datasource.rows[4] = this.createRowBs(quote, "Bitstamp", this.utils.getCurrpairName(this.serviceBs.BTCUSD));
            this.datasource.updateRows();});
        this.serviceBs.getCurrentQuote(this.serviceBs.ETHUSD).subscribe(quote => { 
            this.datasource.rows[5] = this.createRowBs(quote, "Bitstamp", this.utils.getCurrpairName(this.serviceBs.ETHUSD));
            this.datasource.updateRows();});
        this.serviceBs.getCurrentQuote(this.serviceBs.LTCUSD).subscribe(quote => { 
            this.datasource.rows[6] = this.createRowBs(quote, "Bitstamp", this.utils.getCurrpairName(this.serviceBs.LTCUSD));            
            this.datasource.updateRows();});
        this.serviceBs.getCurrentQuote(this.serviceBs.XRPUSD).subscribe(quote => { 
            this.datasource.rows[7] = this.createRowBs(quote, "Bitstamp", this.utils.getCurrpairName(this.serviceBs.XRPUSD));            
            this.datasource.updateRows();});        
        this.serviceIb.getCurrentQuote(this.serviceIb.BTCEUR).subscribe(quote => { 
            this.datasource.rows[8] = this.createRowIb(quote, "Itbit", this.utils.getCurrpairName(this.serviceIb.BTCEUR));
            this.datasource.updateRows();});
        this.serviceIb.getCurrentQuote(this.serviceIb.BTCUSD).subscribe(quote => { 
            this.datasource.rows[9] = this.createRowIb(quote, "Itbit", this.utils.getCurrpairName(this.serviceIb.BTCUSD));
            this.datasource.updateRows();});
        this.serviceCb.getCurrentQuote().subscribe(quote => { 
            let myrows = this.createRowCb(quote); 
            this.datasource.rows[10] = myrows[0]; 
            this.datasource.rows[11] = myrows[1]; 
            this.datasource.rows[12] = myrows[2];
            this.datasource.updateRows();});
        this.serviceBf.getCurrentQuote(this.serviceBf.BTCUSD).subscribe(quote => { 
            this.datasource.rows[13] = this.createRowBf(quote, "Bitfinex", this.utils.getCurrpairName(this.serviceBf.BTCUSD));
            this.datasource.updateRows();});
        this.serviceBf.getCurrentQuote(this.serviceBf.ETHUSD).subscribe(quote => { 
            this.datasource.rows[14] = this.createRowBf(quote, "Bitfinex", this.utils.getCurrpairName(this.serviceBf.ETHUSD));
            this.datasource.updateRows();});
        this.serviceBf.getCurrentQuote(this.serviceBf.LTCUSD).subscribe(quote => { 
            this.datasource.rows[15] = this.createRowBf(quote, "Bitfinex", this.utils.getCurrpairName(this.serviceBf.LTCUSD));
            this.datasource.updateRows();});
        this.serviceBf.getCurrentQuote(this.serviceBf.XRPUSD).subscribe(quote => { 
            this.datasource.rows[16] = this.createRowBf(quote, "Bitfinex", this.utils.getCurrpairName(this.serviceBf.XRPUSD));
            this.datasource.updateRows();});
    }
    
    private formatNumber(x: number) : number {
        return isNaN(x) ? 0 : Math.round(x*100)/100;
    }
    
    createRowBs(quote: QuoteBs, exchange: string, currpair: string) : Myrow {        
        return new Myrow(exchange, currpair, this.formatNumber(quote.last), this.formatNumber(quote.volume), quote.pair);
    }
    
    createRowBf(quote: QuoteBf, exchange: string, currpair: string) : Myrow {  
        return new Myrow(exchange, currpair, this.formatNumber(quote.last_price), this.formatNumber(quote.volume), quote.pair);
    }
    
    createRowCb(quote: QuoteCb) : Myrow[] {           
        let rows: Myrow[] = [];
        rows.push(new Myrow("Coinbase", this.utils.getCurrpairName(this.serviceCb.BTCUSD), this.formatNumber(typeof quote.usd != 'undefined' ? quote.usd : quote.USD), -1, this.serviceCb.BTCUSD));
        rows.push(new Myrow("Coinbase", this.utils.getCurrpairName(this.serviceCb.ETHUSD), this.formatNumber((typeof quote.usd != 'undefined' ? quote.usd : quote.USD) / (typeof quote.eth != 'undefined' ? quote.eth : quote.ETH)), -1, this.serviceCb.ETHUSD));
        rows.push(new Myrow("Coinbase", this.utils.getCurrpairName(this.serviceCb.LTCUSD), this.formatNumber((typeof quote.usd != 'undefined' ? quote.usd : quote.USD) / (typeof quote.ltc != 'undefined' ? quote.ltc : quote.LTC)), -1, this.serviceCb.LTCUSD));        
        return rows;
    }
    
    createRowIb(quote: QuoteIb, exchange: string, currpair: string) : Myrow {
       return new Myrow(exchange, currpair, this.formatNumber(quote.lastPrice), this.formatNumber(quote.volume24h), quote.pair); 
    }
}

export class Myds extends DataSource<Myrow> {
    rows: Myrow[] = [];     
    private subject:Subject<Myrow[]> = new Subject();        

    updateRows() : void {        
        this.subject.next(this.rows);
    }
    
    connect(collectionViewer: CollectionViewer): Observable<Myrow[]> {        
        return this.subject;
    }
    disconnect(collectionViewer: CollectionViewer): void {          
    }  
}     
    
export class Myrow {
    constructor(public exchange: string, public currpair: string, public last: number, public volume: number, public pair: string) {
        
    }
}
