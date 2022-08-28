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

public class RangeDto {
	private BigDecimal min = BigDecimal.ZERO;
	private BigDecimal max = BigDecimal.ZERO;
	
	public RangeDto() {}
	
	public RangeDto(BigDecimal min, BigDecimal max) {
		this.min = min;
		this.max = max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}
	
}
