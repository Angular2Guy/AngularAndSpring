import { CommonUtils } from './commonUtils';
import { formatDate } from '@angular/common';

/**
 *    Copyright 2019 Sven Loesekann
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
export interface MyChartData {
	name: string;
	series: MyChartValue[];
}

export interface MyChartValue {
	name: string;
	value: number;
}

export class Tuple<A,B> {
	constructor(private myA: A, private myB: B) {}
	 
	get A() {
		return this.myA;
	}
	
	get B() {
		return this.myB;
	}
}

export abstract class DetailBase {
	multi: MyChartData[] = [{ name: 'none', series: [] }];	
	legend = false;
	showLabels = true;
	animations = true;
	xAxis = true;
	yAxis = true;
	showYAxisLabel = true;
	showXAxisLabel = true;
	xAxisLabel: string = 'Time';
	yAxisLabel: string = 'Value';
	timeline = true;
	autoScale = true;
	utils = new CommonUtils();
    currPair = "";
    timeframe = this.utils.timeframes[0];
	
	constructor(protected locale: string) {}
	
	protected updateChartData(values: Tuple<string, number>[]): void {
		const dateFormatStr = this.timeframe === this.utils.timeframes[0] ? 'mediumTime' : 'shortDate';
		const quotes = values.map(tuple => ({ name: formatDate(tuple.A, dateFormatStr, this.locale), value: tuple.B } as MyChartValue));
		const myChartData = { name: this.currPair, series: quotes } as MyChartData;
		this.multi = [myChartData];
		//console.log(this.multi);
	}
}