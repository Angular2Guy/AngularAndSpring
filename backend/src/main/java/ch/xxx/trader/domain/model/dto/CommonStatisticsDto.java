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
package ch.xxx.trader.domain.model.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonRootName;

import ch.xxx.trader.domain.model.dto.StatisticsCommon.StatisticsCurrPair;

@JsonRootName(value = "CommonStatistics")
public class CommonStatisticsDto {	
	private StatisticsCurrPair currPair;
	private Double performance1Month;
	private Double performance3Month;
	private Double performance6Month;
	private Double performance1Year;
	private Double performance2Year;
	private Double performance5Year;
	private BigDecimal volatility1Month;
	private BigDecimal volatility3Month;
	private BigDecimal volatility6Month;
	private BigDecimal volatility1Year;
	private BigDecimal volatility2Year;
	private BigDecimal volatility5Year;
	private BigDecimal avgVolume1Month;
	private BigDecimal avgVolume3Month;
	private BigDecimal avgVolume6Month;
	private BigDecimal avgVolume1Year;
	private BigDecimal avgVolume2Year;
	private BigDecimal avgVolume5Year;
	private RangeDto range1Month = new RangeDto();
	private RangeDto range3Month = new RangeDto();
	private RangeDto range6Month = new RangeDto();
	private RangeDto range1Year = new RangeDto();
	private RangeDto range2Year = new RangeDto();
	private RangeDto range5Year = new RangeDto();
	
	public Double getPerformance1Month() {
		return performance1Month;
	}
	public void setPerformance1Month(Double performance1Month) {
		this.performance1Month = performance1Month;
	}
	public Double getPerformance3Month() {
		return performance3Month;
	}
	public void setPerformance3Month(Double performance3Month) {
		this.performance3Month = performance3Month;
	}
	public Double getPerformance6Month() {
		return performance6Month;
	}
	public void setPerformance6Month(Double performance6Month) {
		this.performance6Month = performance6Month;
	}
	public Double getPerformance1Year() {
		return performance1Year;
	}
	public void setPerformance1Year(Double performance1Year) {
		this.performance1Year = performance1Year;
	}
	public Double getPerformance2Year() {
		return performance2Year;
	}
	public void setPerformance2Year(Double performance2Year) {
		this.performance2Year = performance2Year;
	}
	public Double getPerformance5Year() {
		return performance5Year;
	}
	public void setPerformance5Year(Double performance5Year) {
		this.performance5Year = performance5Year;
	}
	public BigDecimal getVolatility1Month() {
		return volatility1Month;
	}
	public void setVolatility1Month(BigDecimal volatility1Month) {
		this.volatility1Month = volatility1Month;
	}
	public BigDecimal getVolatility3Month() {
		return volatility3Month;
	}
	public void setVolatility3Month(BigDecimal volatility3Month) {
		this.volatility3Month = volatility3Month;
	}
	public BigDecimal getVolatility6Month() {
		return volatility6Month;
	}
	public void setVolatility6Month(BigDecimal volatility6Month) {
		this.volatility6Month = volatility6Month;
	}
	public BigDecimal getVolatility1Year() {
		return volatility1Year;
	}
	public void setVolatility1Year(BigDecimal volatility1Year) {
		this.volatility1Year = volatility1Year;
	}
	public BigDecimal getVolatility2Year() {
		return volatility2Year;
	}
	public void setVolatility2Year(BigDecimal volatility2Year) {
		this.volatility2Year = volatility2Year;
	}
	public BigDecimal getVolatility5Year() {
		return volatility5Year;
	}
	public void setVolatility5Year(BigDecimal volatility5Year) {
		this.volatility5Year = volatility5Year;
	}
	public BigDecimal getAvgVolume1Month() {
		return avgVolume1Month;
	}
	public void setAvgVolume1Month(BigDecimal avgVolume1Month) {
		this.avgVolume1Month = avgVolume1Month;
	}
	public BigDecimal getAvgVolume3Month() {
		return avgVolume3Month;
	}
	public void setAvgVolume3Month(BigDecimal avgVolume3Month) {
		this.avgVolume3Month = avgVolume3Month;
	}
	public BigDecimal getAvgVolume6Month() {
		return avgVolume6Month;
	}
	public void setAvgVolume6Month(BigDecimal avgVolume6Month) {
		this.avgVolume6Month = avgVolume6Month;
	}
	public BigDecimal getAvgVolume1Year() {
		return avgVolume1Year;
	}
	public void setAvgVolume1Year(BigDecimal avgVolume1Year) {
		this.avgVolume1Year = avgVolume1Year;
	}
	public BigDecimal getAvgVolume2Year() {
		return avgVolume2Year;
	}
	public void setAvgVolume2Year(BigDecimal avgVolume2Year) {
		this.avgVolume2Year = avgVolume2Year;
	}
	public BigDecimal getAvgVolume5Year() {
		return avgVolume5Year;
	}
	public void setAvgVolume5Year(BigDecimal avgVolume5Year) {
		this.avgVolume5Year = avgVolume5Year;
	}
	public RangeDto getRange1Month() {
		return range1Month;
	}
	public void setRange1Month(RangeDto range1Month) {
		this.range1Month = range1Month;
	}
	public RangeDto getRange3Month() {
		return range3Month;
	}
	public void setRange3Month(RangeDto range3Month) {
		this.range3Month = range3Month;
	}
	public RangeDto getRange6Month() {
		return range6Month;
	}
	public void setRange6Month(RangeDto range6Month) {
		this.range6Month = range6Month;
	}
	public RangeDto getRange1Year() {
		return range1Year;
	}
	public void setRange1Year(RangeDto range1Year) {
		this.range1Year = range1Year;
	}
	public RangeDto getRange2Year() {
		return range2Year;
	}
	public void setRange2Year(RangeDto range2Year) {
		this.range2Year = range2Year;
	}
	public RangeDto getRange5Year() {
		return range5Year;
	}
	public void setRange5Year(RangeDto range5Year) {
		this.range5Year = range5Year;
	}
	public StatisticsCurrPair getCurrPair() {
		return currPair;
	}
	public void setCurrPair(StatisticsCurrPair currPair) {
		this.currPair = currPair;
	}
}
