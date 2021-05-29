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
package ch.xxx.trader.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document
public class QuoteCbSmall implements Quote {
	@Id
	private ObjectId _id;
	private final Date createdAt;
	private final BigDecimal usd;
	private final BigDecimal eur;
	private final BigDecimal eth;
	private final BigDecimal ltc;

	public QuoteCbSmall(@JsonProperty("createdAt") Date createdAt, @JsonProperty("usd") BigDecimal usd, @JsonProperty("eur") BigDecimal eur,
			@JsonProperty("eth") BigDecimal eth, @JsonProperty("ltc") BigDecimal ltc) {
		super();
		this.createdAt = createdAt;
		this.usd = usd;
		this.eur = eur;
		this.eth = eth;
		this.ltc = ltc;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public BigDecimal getUsd() {
		return usd;
	}

	public BigDecimal getEth() {
		return eth;
	}

	public BigDecimal getLtc() {
		return ltc;
	}

	public BigDecimal getEur() {
		return eur;
	}

}
