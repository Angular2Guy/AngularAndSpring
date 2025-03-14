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
import { Component, DestroyRef, OnInit, inject } from "@angular/core";
import { BitstampService } from "../../services/bitstamp.service";
import { ItbitService } from "../../services/itbit.service";
import { BitfinexService } from "../../services/bitfinex.service";
import { Router } from "@angular/router";
import { OrderbookBs } from "../../common/orderbook-bs";
import { OrderbookBf, OrderBf } from "../../common/orderbook-bf";
import { OrderbookIb } from "../../common/orderbook-ib";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";

@Component({
    selector: "app-orderbooks",
    templateUrl: "./orderbooks.component.html",
    styleUrls: ["./orderbooks.component.scss"],
    standalone: false
})
export class OrderbooksComponent implements OnInit {
  public currencies: MyCurr[];
  protected orderbookBs: OrderbookBs;
  protected orderbookBf: OrderbookBf;
  protected orderbookIb: OrderbookIb;
  protected model = new MyModel(null, false, false, false, 1, null);
  protected bsOrders: MyOrder[] = [];
  protected bfOrders: MyOrder[] = [];
  protected ibOrders: MyOrder[] = [];
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(
    private router: Router,
    private serviceBs: BitstampService,
    private serviceIb: ItbitService,
    private serviceBf: BitfinexService,
  ) {}

  ngOnInit() {
    this.currencies = [
      new MyCurr(this.serviceBf.BTCUSD, "Btc - Usd"),
      new MyCurr(this.serviceBf.ETHUSD, "Eth - Usd"),
      new MyCurr(this.serviceBf.LTCUSD, "Ltc - Usd"),
      new MyCurr(this.serviceBf.XRPUSD, "Xrp - Usd"),
    ];
    console.log("hallo");
  }
  onSubmit() {
    //console.log( this.model );
    if (this.model.itbitCb && this.model.currpair === this.serviceBf.BTCUSD) {
      this.serviceIb
        .getOrderbook(this.serviceIb.BTCUSD)
        .pipe(takeUntilDestroyed(this.destroy))
        .subscribe((ob) => {
          //                this.orderbookIb = ob;
          this.ibOrders = this.filterObIb(ob);
        });
    } else {
      this.ibOrders = [];
    }
    if (this.model.bitstampCb) {
      this.serviceBs
        .getOrderbook(this.model.currpair)
        .pipe(takeUntilDestroyed(this.destroy))
        .subscribe((ob) => {
          //                this.orderbookBs = ob;
          this.bsOrders = this.filterObBs(ob);
        });
    } else {
      this.bsOrders = [];
    }
    if (this.model.bitfinexCb) {
      this.serviceBf
        .getOrderbook(this.model.currpair)
        .pipe(takeUntilDestroyed(this.destroy))
        .subscribe((ob) => {
          //                this.orderbookBf = ob;
          this.bfOrders = this.filterObBf(ob);
        });
    } else {
      this.bfOrders = [];
    }
  }
  back() {
    console.log("Back");
    this.router.navigate(["/overview"]);
  }

  private filterObBs(ob: OrderbookBs): MyOrder[] {
    const myOrders: MyOrder[] = [];
    let sum = 0;
    const bidAskArr = this.model.buysell === 1 ? ob.asks : ob.bids;
    for (const order of bidAskArr) {
      myOrders.push(
        new MyOrder(
          this.model.buysell,
          parseFloat(order[0]),
          parseFloat(order[1]),
          sum > this.model.amount ? "black" : "blue",
        ),
      );
      sum += parseFloat(order[1]);
      if (sum > this.model.amount * 1.5) {
        break;
      }
    }
    return myOrders;
  }

  private filterObBf(ob: OrderbookBf): MyOrder[] {
    const myOrders: MyOrder[] = [];
    let sum = 0;
    const bidAskArr = this.model.buysell === 1 ? ob.asks : ob.bids;
    for (const order of bidAskArr) {
      myOrders.push(
        new MyOrder(
          this.model.buysell,
          parseFloat(order.price),
          parseFloat(order.amount),
          sum > this.model.amount ? "black" : "blue",
        ),
      );
      sum += parseFloat(order.amount);
      if (sum > this.model.amount * 1.5) {
        break;
      }
    }
    return myOrders;
  }

  private filterObIb(ob: OrderbookIb): MyOrder[] {
    const myOrders: MyOrder[] = [];
    let sum = 0;
    const bidAskArr = this.model.buysell === 1 ? ob.asks : ob.bids;
    for (const order of bidAskArr) {
      myOrders.push(
        new MyOrder(
          this.model.buysell,
          parseFloat(order[0]),
          parseFloat(order[1]),
          sum > this.model.amount ? "black" : "blue",
        ),
      );
      sum += parseFloat(order[1]);
      if (sum > this.model.amount * 1.5) {
        break;
      }
    }
    return myOrders;
  }
}

export class MyOrder {
  constructor(
    public buysell: number,
    public price: number,
    public amount: number,
    public color: string,
  ) {}
}

export class MyModel {
  constructor(
    public currpair: string,
    public bitstampCb: boolean,
    public itbitCb: boolean,
    public bitfinexCb: boolean,
    public buysell: number,
    public amount: number,
  ) {}
}

export class MyCurr {
  constructor(
    public value: string,
    public name: string,
  ) {}
}
