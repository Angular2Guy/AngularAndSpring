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
package ch.xxx.trader.usecase.mappers;

import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.dtos.QuoteBf;
import ch.xxx.trader.domain.dtos.QuoteBs;
import ch.xxx.trader.domain.dtos.QuoteIb;
import ch.xxx.trader.domain.dtos.QuotePdf;

@Service
public class ReportMapper {

	public QuotePdf convert(QuoteIb quote) {
		QuotePdf quotePdf = new QuotePdf(quote.getLastPrice(), quote.getPair(), quote.getVolume24h(),
				quote.getCreatedAt(), quote.getBid(), quote.getAsk());
		return quotePdf;
	}
	
	public QuotePdf convert(QuoteBs quote) {
		QuotePdf quotePdf = new QuotePdf(quote.getLast(), quote.getPair(), quote.getVolume(), quote.getCreatedAt(),
				quote.getBid(), quote.getAsk());
		return quotePdf;
	}
	
	public QuotePdf convert(QuoteBf quote) {
		QuotePdf quotePdf = new QuotePdf(quote.getLast_price(), quote.getPair(), quote.getVolume(), quote.getCreatedAt(), quote.getBid(), quote.getAsk());		
		return quotePdf;
	}
}
