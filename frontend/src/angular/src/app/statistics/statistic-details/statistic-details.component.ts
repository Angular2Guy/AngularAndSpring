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
import { Component, Input, OnInit } from '@angular/core';
import { ChartBars, ChartBar } from 'ngx-simple-charts/bar';
import { tap } from 'rxjs';
import { CoinExchange, CommonStatistics, StatisticCurrencyPair } from 'src/app/common/common-statistics';
import { StatisticService } from 'src/app/services/statistic.service';

@Component({
  selector: 'app-statistic-details',
  templateUrl: './statistic-details.component.html',
  styleUrls: ['./statistic-details.component.scss']
})
export class StatisticDetailsComponent implements OnInit {
  @Input()
  coinExchange: CoinExchange;
  statisticCurrencyPair = StatisticCurrencyPair;
  selCurrency = StatisticCurrencyPair.bcUsd;
  commonStatistics = new CommonStatistics();
  chartBars!: ChartBars;
  chartsLoading = true;

  constructor(private statisticService: StatisticService) { }

  ngOnInit(): void {
	this.statisticService.getCommonStatistics(this.selCurrency, this.coinExchange)
	   .pipe(tap(result => this.chartBars = this.createChartBars(result)))
	   .subscribe(result => this.commonStatistics = result);
  }

  updateCurrency(): void {
	this.chartsLoading = true;
	this.statisticService.getCommonStatistics(this.selCurrency, this.coinExchange)
	   .pipe(tap(result => this.chartBars = this.createChartBars(result)))
	   .subscribe(result => this.commonStatistics = result);
  }

  private createChartBars(commonStatistics: CommonStatistics): ChartBars {
	const performanceValues = [{x: '1 Month',y: commonStatistics.performance1Month}, {x: '3 Months', y: commonStatistics.performance3Month}, 
	  {x: '6 Months', y: commonStatistics.performance6Month}, {x: '1 Year', y: commonStatistics.performance1Year}, {x: '2 Years', y: commonStatistics.performance2Year}, 
	  {x: '5 Years', y: commonStatistics.performance5Year}].reverse();	
	const myChartBars = {title: 'Performance', from: '', xScaleHeight: 100, yScaleWidth: 100, chartBars: performanceValues} as ChartBars;
	this.chartsLoading = false;
	return myChartBars;
  }
}
