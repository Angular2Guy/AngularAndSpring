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
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CoinExchange, CommonStatistics, StatisticCurrencyPair } from '../common/common-statistics';
import { StatisticService } from '../services/statistic.service';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.scss']
})
export class StatisticsComponent implements OnInit {
  commonStatistics = new CommonStatistics();

  constructor(private router: Router, private statisticService: StatisticService) { }

  ngOnInit(): void {
	this.statisticService.getCommonStatistics(StatisticCurrencyPair.bcUsd, CoinExchange.bitstamp).subscribe(result => this.commonStatistics = result);
  }

  back(): void {
	this.router.navigate(['/']);
  }
}
