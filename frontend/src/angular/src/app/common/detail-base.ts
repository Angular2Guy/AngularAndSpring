/*
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
import { CommonUtils } from './common-utils';
import { formatDate } from '@angular/common';
import { ChartPoint,ChartPoints } from 'ngx-simple-charts/line';

export class Tuple<A,B> {
	constructor(private myA: A, private myB: B) {}

	// eslint-disable-next-line @typescript-eslint/naming-convention
	get A() {
		return this.myA;
	}
	// eslint-disable-next-line @typescript-eslint/naming-convention
	get B() {
		return this.myB;
	}
}

export abstract class DetailBase {
	chartPoints: ChartPoints[] = [];
	utils = new CommonUtils();
    currPair = '';
    timeframe = this.utils.timeframes[0];
	readonly yScaleWidth = 50;
	readonly xScaleHeight = 20;

	constructor(protected locale: string) {}

	protected updateChartData(values: Tuple<string, number>[]): void {
		const myChartPoint = values.filter(value => value.B > 0.009).map(myCP => ({x: new Date(myCP.A), y: myCP.B} as ChartPoint));
		this.chartPoints = [{name: this.currPair, chartPointList: myChartPoint,
			yScaleWidth: this.yScaleWidth, xScaleHeight: this.xScaleHeight} as ChartPoints];
		//console.log(this.chartPoints);
	}
}
