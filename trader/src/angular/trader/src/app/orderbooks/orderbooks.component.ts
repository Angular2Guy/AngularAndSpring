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
import { BitstampService } from '../services/bitstamp.service';
import { ItbitService } from '../services/itbit.service';
import { BitfinexService } from '../services/bitfinex.service';
import { Router } from '@angular/router';
import { OrderbookBs } from '../common/orderbookBs';
import { OrderbookBf, Order } from '../common/orderbookBf';
import { OrderbookIb } from '../common/orderbookIb';

@Component({
  selector: 'app-orderbooks',
  templateUrl: './orderbooks.component.html',
  styleUrls: ['./orderbooks.component.scss']
})
export class OrderbooksComponent implements OnInit {

  orderbookBs: OrderbookBs;
  orderbookBf: OrderbookBf;
  orderbookIb: OrderbookIb;
  currencies: MyCurr[];
  model = new MyModel(null, false, false, false, 1, null);  
  
  constructor(private router: Router, 
          private serviceBs: BitstampService, 
          private serviceIb: ItbitService, 
          private serviceBf: BitfinexService) { }

  ngOnInit() {
      this.serviceBs.getOrderbook(this.serviceBs.BTCUSD).subscribe(ob => this.orderbookBs = ob);
      this.currencies = [new MyCurr(this.serviceBf.BTCUSD, 'Btc - Usd'), new MyCurr(this.serviceBf.ETHUSD,'Eth - Usd'), new MyCurr(this.serviceBf.LTCUSD,'Ltc - Usd'), new MyCurr(this.serviceBf.XRPUSD, 'Xrp - Usd')];
  }

  onSubmit() {
      console.log(this.model);
  }
  
  onBack() {
      this.router.navigateByUrl("/overview");
  }
  
}

export class MyModel {
    constructor(public currpair: string, public bitstampCb: boolean, public itbitCb: boolean, public bitfinexCb: boolean, public buysell: number, public amount: number) {}
}

export class MyCurr {
    constructor(public value: string, public name: string) { }
}
