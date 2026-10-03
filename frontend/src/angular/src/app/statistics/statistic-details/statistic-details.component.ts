/*
   Copyright 2016 Sven Loesekann

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
import { CommonModule } from "@angular/common";
import {
  Component,
  DestroyRef,
  OnInit,
  effect,
  inject,
  input,
  signal,
  untracked,
  ChangeDetectionStrategy,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatRadioModule } from "@angular/material/radio";
import { MatTabsModule } from "@angular/material/tabs";
import { MatToolbarModule } from "@angular/material/toolbar";
import { ChartBars, ChartBar, NgxBarChartsModule } from "ngx-simple-charts/bar";
import { tap } from "rxjs";
import {
  CoinExchange,
  CommonStatistics,
  StatisticCurrencyPair,
} from "../../common/common-statistics";
import { StatisticService } from "../../services/statistic.service";

@Component({
  selector: "app-statistic-details",
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatButtonModule,
    MatTabsModule,
    MatRadioModule,
    NgxBarChartsModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: "./statistic-details.component.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./statistic-details.component.scss"],
})
export class StatisticDetailsComponent implements OnInit {
  readonly coinExchange = input<CoinExchange>(CoinExchange.bitfinex);
  readonly tabIndex = input<number>(0);
  protected statisticCurrencyPair = StatisticCurrencyPair;
  protected readonly selCurrency = signal<StatisticCurrencyPair>(
    StatisticCurrencyPair.bcUsd,
  );
  protected commonStatistics = signal<CommonStatistics>({} as CommonStatistics);
  protected chartBars = signal<ChartBars>({} as ChartBars);
  protected chartsLoading = signal(true);
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(private statisticService: StatisticService) {
    // Re-fetch when parent tab selection or exchange changes (after initial load).
    // updateCurrency() reads chartsLoading/selCurrency and the fetch
    // completion writes chartsLoading — tracking those would retrigger this
    // effect after every fetch (endless HTTP loop), so only tabIndex and
    // coinExchange are tracked while the side effect itself runs untracked.
    effect(() => {
      void this.tabIndex();
      void this.coinExchange();
      untracked(() => this.updateCurrency());
    });
  }

  ngOnInit(): void {
    this.statisticService
      .getCommonStatistics(this.selCurrency(), this.coinExchange())
      .pipe(
        tap((result) => this.chartBars.set(this.createChartBars(result))),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe((result) => this.commonStatistics.set(result));
  }

  updateCurrency(): void {
    if (!this.chartsLoading()) {
      this.chartsLoading.set(true);
      this.statisticService
        .getCommonStatistics(this.selCurrency(), this.coinExchange())
        .pipe(
          tap((result) => this.chartBars.set(this.createChartBars(result))),
          takeUntilDestroyed(this.destroy),
        )
        .subscribe((result) => this.commonStatistics.set(result));
    }
  }

  private createChartBars(commonStatistics: CommonStatistics): ChartBars {
    const performanceValues = [
      {
        x: $localize`:@@Month1:1 Month`,
        y: commonStatistics.performance1Month,
      },
      {
        x: $localize`:@@Month3:3 Months`,
        y: commonStatistics.performance3Month,
      },
      {
        x: $localize`:@@Month6:6 Months`,
        y: commonStatistics.performance6Month,
      },
      { x: $localize`:@@Year1:1 Year`, y: commonStatistics.performance1Year },
      { x: $localize`:@@Year2:2 Years`, y: commonStatistics.performance2Year },
      { x: $localize`:@@Year5:5 Years`, y: commonStatistics.performance5Year },
    ].reverse() as [ChartBar];
    const myChartBars = {
      title: $localize`:@@statisticsPerformance:Performance`,
      from: "",
      xScaleHeight: 100,
      yScaleWidth: 100,
      chartBars: performanceValues,
    } as ChartBars;
    this.chartsLoading.set(false);
    // console.log(myChartBars);
    return myChartBars;
  }
}
