/*
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
import {
  Component,
  OnInit,
  Inject,
  LOCALE_ID,
  DestroyRef,
  inject,
} from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import {
  trigger,
  state,
  animate,
  transition,
  style,
} from "@angular/animations";
import { BehaviorSubject, Observable, repeat } from "rxjs";
import { QuoteIb } from "../../common/quote-ib";
import { ItbitService } from "../../services/itbit.service";
import { DetailBase, Tuple } from "src/app/common/detail-base";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";

@Component({
    selector: "app-ibdetail",
    templateUrl: "./ibdetail.component.html",
    styleUrls: ["./ibdetail.component.scss"],
    animations: [
        trigger("showChart", [
            transition("false => true", [
                style({ opacity: 0 }),
                animate(1000, style({ opacity: 1 })),
            ]),
        ]),
    ],
    standalone: false
})
export class IbdetailComponent extends DetailBase implements OnInit {
  public currQuote: QuoteIb;
  protected chartShow = new BehaviorSubject(false);
  protected todayQuotes: QuoteIb[] = [];
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private serviceIb: ItbitService,
    @Inject(LOCALE_ID) private myLocale: string,
  ) {
    super(myLocale);
  }

  ngOnInit() {
    this.chartShow.next(false);
    this.route.params.subscribe((params) => {
      this.currPair = params.currpair;
      this.serviceIb
        .getCurrentQuote(this.currPair)
        .pipe(repeat({ delay: 10000 }), takeUntilDestroyed(this.destroy))
        .subscribe((quote) => (this.currQuote = quote));
      this.serviceIb
        .getTodayQuotes(this.currPair)
        .pipe(takeUntilDestroyed(this.destroy))
        .subscribe((quotes) => {
          this.todayQuotes = quotes;
          this.updateChartData(
            quotes.map(
              (quote) =>
                new Tuple<string, number>(quote.createdAt, quote.lastPrice),
            ),
          );
          this.chartShow.next(true);
        });
    });
  }

  back(): void {
    this.router.navigate(["/"]);
  }

  changeTf() {
    this.chartShow.next(false);
    const currpair = this.route.snapshot.paramMap.get("currpair");
    let quoteObserv: Observable<QuoteIb[]>;
    if (this.timeframe === this.utils.MyTimeFrames.Day7) {
      quoteObserv = this.serviceIb.get7DayQuotes(currpair);
    } else if (this.timeframe === this.utils.MyTimeFrames.Day30) {
      quoteObserv = this.serviceIb.get30DayQuotes(currpair);
    } else if (this.timeframe === this.utils.MyTimeFrames.Day90) {
      quoteObserv = this.serviceIb.get90DayQuotes(currpair);
    } else if (this.timeframe === this.utils.MyTimeFrames.Day180) {
      quoteObserv = this.serviceIb.get6MonthsQuotes(currpair);
    } else if (this.timeframe === this.utils.MyTimeFrames.Day365) {
      quoteObserv = this.serviceIb.get1YearQuotes(currpair);
    } else {
      quoteObserv = this.serviceIb.getTodayQuotes(currpair);
    }
    quoteObserv.pipe(takeUntilDestroyed(this.destroy)).subscribe((quotes) => {
      this.todayQuotes = quotes;
      this.updateChartData(
        quotes.map(
          (quote) =>
            new Tuple<string, number>(quote.createdAt, quote.lastPrice),
        ),
      );
      this.chartShow.next(true);
    });
  }

  showReport() {
    const currpair = this.route.snapshot.paramMap.get("currpair");
    const url = "/itbit" + this.utils.createReportUrl(this.timeframe, currpair);
    window.open(url);
  }
}
