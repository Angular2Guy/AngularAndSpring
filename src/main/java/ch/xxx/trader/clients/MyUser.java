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
package ch.xxx.trader.clients;

import java.math.BigDecimal;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document
public class MyUser {

	@Id
	private ObjectId _id;
	@JsonProperty
	private String userId;
	@JsonProperty
	private String password;
	@JsonProperty
	private String salt;
	@JsonProperty
	private String email;	
	@JsonProperty
	private BigDecimal btcAmount;
	@JsonProperty
	private BigDecimal ethAmount;
	@JsonProperty
	private BigDecimal ltcAmount;
	@JsonProperty
	private BigDecimal xrpAmount;
	
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public BigDecimal getBtcAmount() {
		return btcAmount;
	}
	public void setBtcAmount(BigDecimal btcAmount) {
		this.btcAmount = btcAmount;
	}
	public BigDecimal getEthAmount() {
		return ethAmount;
	}
	public void setEthAmount(BigDecimal ethAmount) {
		this.ethAmount = ethAmount;
	}
	public BigDecimal getLtcAmount() {
		return ltcAmount;
	}
	public void setLtcAmount(BigDecimal ltcAmount) {
		this.ltcAmount = ltcAmount;
	}
	public BigDecimal getXrpAmount() {
		return xrpAmount;
	}
	public void setXrpAmount(BigDecimal xrpAmount) {
		this.xrpAmount = xrpAmount;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
}
