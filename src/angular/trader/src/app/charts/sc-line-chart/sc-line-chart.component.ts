/**
 *    Copyright 2021 Sven Loesekann
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
import { Component, ViewChild, ElementRef, Input, OnChanges, SimpleChanges, ViewEncapsulation, AfterViewInit } from '@angular/core';
import { select, Selection, ContainerElement } from 'd3-selection';
import { scaleLinear, scaleTime, ScaleLinear, ScaleTime } from 'd3-scale';
import { extent } from 'd3-array';
import { line, curveMonotoneX } from 'd3-shape';
import { axisBottom, axisLeft } from 'd3-axis';
import { ChartPoints, ChartPoint } from '../model/chart-points';

@Component({
	selector: 'sc-line-chart',
	templateUrl: './sc-line-chart.component.html',
	styleUrls: ['./sc-line-chart.component.scss'],
	encapsulation: ViewEncapsulation.Emulated,
})
export class ScLineChartComponent implements AfterViewInit, OnChanges {
	@ViewChild("svgchart")
	private chartContainer!: ElementRef;
	private d3Svg!: Selection<ContainerElement, ChartPoint, HTMLElement, any>;
	@Input()
	private chartPoints: ChartPoints[] = [];
	private gAttribute!: Selection<SVGGElement, ChartPoint, HTMLElement, any>;
	private gxAttribute!: Selection<SVGGElement, ChartPoint, HTMLElement, any>;
	private gyAttribute!: Selection<SVGGElement, ChartPoint, HTMLElement, any>;
	private gPathAttribute!: Selection<SVGPathElement, ChartPoint, HTMLElement, any>;

	ngAfterViewInit(): void {
		this.d3Svg = select<ContainerElement, ChartPoint>(this.chartContainer.nativeElement);

		this.gAttribute = this.d3Svg.append('g').attr('transform', 'translate(0,0)');
		this.gxAttribute = this.gAttribute.append('gx');
		this.gxAttribute = this.gxAttribute.attr('class', 'x axis');
		this.gyAttribute = this.gAttribute.append('gy');
		this.gyAttribute = this.gyAttribute.attr('class', 'y axis');
		this.gPathAttribute = this.gAttribute.append('path');

		this.updateChart();
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (!!changes['chartPoints'] && !changes['chartPoints'].isFirstChange()) {
			this.updateChart();
		}
	}

	private updateChart(): void {
		const contentWidth = isNaN(parseInt(this.d3Svg.style('width').replace(/[^0-9\.]+/g, ''), 10)) ? 0 : parseInt(this.d3Svg.style('width').replace(/[^0-9\.]+/g, ''), 10);
		const contentHeight = isNaN(parseInt(this.d3Svg.style('height').replace(/[^0-9\.]+/g, ''), 10)) ? 0 : parseInt(this.d3Svg.style('height').replace(/[^0-9\.]+/g, ''), 10);
		if (contentHeight < 1 || contentWidth < 1 || !this.chartPoints || this.chartPoints.length === 0 
			|| !this.chartPoints[0].chartPointList || this.chartPoints[0].chartPointList.length === 0) {
			console.log(`contentHeight: ${contentHeight} contentWidth: ${contentWidth} chartPoints: ${this.chartPoints.length}`);
			return;
		}
		console.log(`chartPoints: ${this.chartPoints.length} chartPointList: ${this.chartPoints[0].chartPointList.length}`);
		
		let xScale: ScaleLinear<number, number, never> | ScaleTime<number, number, never>;
		if (this.chartPoints[0].chartPointList[0].x instanceof Date) {
			xScale = scaleTime()
				.domain(extent(this.chartPoints[0].chartPointList, p => p.x as Date) as [Date, Date])
				.range([0, contentWidth])
		} else {
			xScale = scaleLinear()
				.domain([0, this.chartPoints[0].chartPointList.length - 1]).nice()
				.range([0, contentWidth]);
		}

		const yScale = scaleLinear()
			.domain(extent<ChartPoint, number>(this.chartPoints[0].chartPointList, p => p.y) as [number, number]).nice()
			.range([0, contentWidth]);

		const myLine = line()
			.defined(p => (p as unknown as ChartPoint).y !== null && !isNaN((p as unknown as ChartPoint).y))
			.x((p, i) => xScale(i))
			.y((p) => yScale((p as unknown as ChartPoint).y))
			.curve((p) => curveMonotoneX(p));
			
		this.gPathAttribute.datum(this.chartPoints[0].chartPointList.filter((p) => p.y !== null && !isNaN(p.y)))
			.attr('class', 'line').attr('d', myLine as any);
		this.gPathAttribute.datum(this.chartPoints[0].chartPointList.filter((p) => isNaN(p.y) || p.y === null))
			.attr('class', 'line').attr('d', myLine as any);

		this.gxAttribute
			.attr("transform", "translate(" + 0 + "," + contentWidth + ")")
			.call(axisBottom(xScale));

		this.gyAttribute
			.attr("transform", "translate(" + 0 + "," + contentHeight + ")")
			.call(axisLeft(yScale));		
	}
}
