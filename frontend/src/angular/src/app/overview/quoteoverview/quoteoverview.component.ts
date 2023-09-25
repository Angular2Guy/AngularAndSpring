/* Copyright 2016 Sven Loesekann

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
import { Component, OnInit, OnDestroy } from "@angular/core";
import { BitstampService } from "../../services/bitstamp.service";
import { CoinbaseService } from "../../services/coinbase.service";
import { ItbitService } from "../../services/itbit.service";
import { BitfinexService } from "../../services/bitfinex.service";
import { QuoteBs } from "../../common/quote-bs";
import { QuoteCb } from "../../common/quote-cb";
import { QuoteIb } from "../../common/quote-ib";
import { QuoteBf } from "../../common/quote-bf";
import { BehaviorSubject, Observable, Subject } from "rxjs";
import { DataSource, CollectionViewer } from "@angular/cdk/collections";
import { Router } from "@angular/router";
import { CommonUtils } from "../../common/common-utils";
import { MatDialog } from "@angular/material/dialog";
import { LoginComponent } from "../login/login.component";
import { MyuserService } from "../../services/myuser.service";
import { filter } from "rxjs/operators";
import { TokenService } from "ngx-simple-charts/base-service";
import { DateTime, Duration } from "luxon";

@Component({
  selector: "app-quoteoverview",
  templateUrl: "./quoteoverview.component.html",
  styleUrls: ["./quoteoverview.component.scss"],
})
export class QuoteoverviewComponent implements OnInit, OnDestroy {
  protected datasource = new Myds();
  protected loggedIn = false;
  private interval: any;
  private utils = new CommonUtils();

  constructor(
    private router: Router,
    private serviceBs: BitstampService,
    private serviceCb: CoinbaseService,
    private serviceIb: ItbitService,
    private serviceBf: BitfinexService,
    private serviceMu: MyuserService,
    private tokenService: TokenService,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    if (this.interval) {
      clearInterval(this.interval);
    }
    this.interval = setInterval(() => {
      this.refreshData();
    }, 15000);
    if (this.datasource.rows.length < 16) {
      for (let i = 0; i < 16; i++) {
        this.datasource.rows.push(
          new Myrow(
            "",
            "",
            0,
            DateTime.now()
              .minus(Duration.fromObject({ minutes: 5 }))
              .toISO(),
            0,
            null,
            -1,
            -1
          )
        );
      }
      this.datasource.updateRows();
    }
    this.refreshData();
    this.loggedIn = !!this.tokenService.token;
    //console.log(this.hash);
  }

  ngOnDestroy(): void {
    if (this.interval) {
      clearInterval(this.interval);
    }
  }

  openLoginDialog(): void {
    const dialogRef = this.dialog.open(LoginComponent, {
      width: "600px",
      data: { loggedIn: this.loggedIn },
    });

    dialogRef.afterClosed().subscribe((result) => {
      this.loggedIn = result;
    });
  }

  isElementOutdated(element: Myrow): boolean {
    const result =
      DateTime.fromISO(element.createdAt).diffNow().toMillis() < -120000;
    return result;
  }

  logout(): void {
    this.loggedIn = !this.serviceMu.postLogout();
  }

  orderbooks(): void {
    this.router.navigateByUrl("/orderbooks");
  }

  statistics(): void {
    this.router.navigateByUrl("/statistics");
  }

  selectedRow(row: Myrow): void {
    //console.log(row);
    if (row.exchange === "Bitstamp") {
      this.router.navigateByUrl("details/bsdetail/" + row.pair);
    } else if (row.exchange === "Itbit" && row.pair === "XBTUSD") {
      this.router.navigateByUrl("details/ibdetail/" + this.serviceIb.BTCUSD);
    } else if (row.exchange === "Coinbase") {
      this.router.navigateByUrl("details/cbdetail/" + row.pair);
    } else if (row.exchange === "Bitfinex") {
      this.router.navigateByUrl("details/bfdetail/" + row.pair);
    }
  }

  createRowBs(quote: QuoteBs, exchange: string, currpair: string): Myrow {
    return !quote
      ? new Myrow(exchange, currpair)
      : new Myrow(
          exchange,
          currpair,
          this.formatNumber(quote?.last),
          quote.createdAt,
          this.formatNumber(quote?.volume),
          quote?.pair,
          this.formatNumber(quote?.high),
          this.formatNumber(quote?.low)
        );
  }

  createRowBf(quote: QuoteBf, exchange: string, currpair: string): Myrow {
    return !quote
      ? new Myrow(exchange, currpair)
      : new Myrow(
          exchange,
          currpair,
          this.formatNumber(quote?.last_price),
          quote.createdAt,
          this.formatNumber(quote?.volume),
          quote?.pair,
          this.formatNumber(quote?.high),
          this.formatNumber(quote?.low)
        );
  }

  private createCbRow(quote: QuoteCb, value: number, currPair: string): Myrow {
    return new Myrow(
      "Coinbase",
      this.utils.getCurrpairName(currPair),
      value,
      quote.createdAt,
      -1,
      currPair,
      -1,
      -1
    );
  }

  createRowsCb(quote: QuoteCb): Myrow[] {
    const rows: Myrow[] = [];
    rows.push(
      this.createCbRow(
        quote,
        !quote ? 0 : this.formatNumber(quote.usd),
        this.serviceCb.BTCUSD
      )
    );
    rows.push(
      this.createCbRow(
        quote,
        !quote ? 0 : this.formatNumber(quote.usd / quote.eth),
        this.serviceCb.ETHUSD
      )
    );
    rows.push(
      this.createCbRow(
        quote,
        !quote ? 0 : this.formatNumber(quote.usd / quote.ltc),
        this.serviceCb.LTCUSD
      )
    );
    return rows;
  }

  createRowIb(quote: QuoteIb, exchange: string, currpair: string): Myrow {
    return !quote
      ? new Myrow(exchange, currpair)
      : new Myrow(
          exchange,
          currpair,
          this.formatNumber(quote?.lastPrice),
          quote.createdAt,
          this.formatNumber(quote?.volume24h),
          quote?.pair,
          this.formatNumber(quote?.high24h),
          this.formatNumber(quote?.low24h)
        );
  }

  private refeshBsData(currPair: string, rowId: number): void {
    this.serviceBs
      .getCurrentQuote(currPair)
      .pipe(filter((result) => !!result?.last))
      .subscribe((quote) => {
        this.datasource.rows[rowId] = this.createRowBs(
          quote,
          "Bitstamp",
          this.utils.getCurrpairName(currPair)
        );
        this.datasource.updateRows();
      });
  }

  private refreshBfData(currPair: string, rowId: number): void {
    this.serviceBf
      .getCurrentQuote(currPair)
      .pipe(filter((result) => !!result?.last_price))
      .subscribe((quote) => {
        this.datasource.rows[rowId] = this.createRowBf(
          quote,
          "Bitfinex",
          this.utils.getCurrpairName(currPair)
        );
        this.datasource.updateRows();
      });
  }

  private refreshData(): void {
    this.refeshBsData(this.serviceBs.BTCEUR, 0);
    this.refeshBsData(this.serviceBs.ETHEUR, 1);
    this.refeshBsData(this.serviceBs.LTCEUR, 2);
    this.refeshBsData(this.serviceBs.XRPEUR, 3);
    this.refeshBsData(this.serviceBs.BTCUSD, 4);
    this.refeshBsData(this.serviceBs.ETHUSD, 5);
    this.refeshBsData(this.serviceBs.LTCUSD, 6);
    this.refeshBsData(this.serviceBs.XRPUSD, 7);
    this.serviceIb
      .getCurrentQuote(this.serviceIb.BTCUSD)
      .pipe(filter((result) => !!result?.lastPrice))
      .subscribe((quote) => {
        this.datasource.rows[8] = this.createRowIb(
          quote,
          "Itbit",
          this.utils.getCurrpairName(this.serviceIb.BTCUSD)
        );
        this.datasource.updateRows();
      });
    this.serviceCb
      .getCurrentQuote()
      .pipe(filter((result) => !!result?.btc))
      .subscribe((quote) => {
        const myrows = this.createRowsCb(quote);
        this.datasource.rows[9] = myrows[0];
        this.datasource.rows[10] = myrows[1];
        this.datasource.rows[11] = myrows[2];
        this.datasource.updateRows();
      });
    this.refreshBfData(this.serviceBf.BTCUSD, 12);
    this.refreshBfData(this.serviceBf.ETHUSD, 13);
    this.refreshBfData(this.serviceBf.LTCUSD, 14);
    this.refreshBfData(this.serviceBf.XRPUSD, 15);
  }

  private formatNumber(x: number): number {
    return isNaN(x) ? 0 : Math.round(x * 100) / 100;
  }
}

export class Myds extends DataSource<Myrow> {
  rows: Myrow[] = [];
  private subject = new BehaviorSubject<Myrow[]>([]);

  updateRows(): void {
    this.subject.next(this.rows);
  }

  connect(collectionViewer: CollectionViewer): Observable<Myrow[]> {
    return this.subject;
  }
  disconnect(collectionViewer: CollectionViewer): void {
    this.subject.unsubscribe();
  }
}

export class Myrow {
  constructor(
    public exchange: string = "",
    public currpair: string = "",
    public last: number = 0,
    public createdAt: string = DateTime.now().toISO(),
    public volume: number = 0,
    public pair: string = "",
    public high: number = 0,
    public low: number = 0
  ) {}
}
