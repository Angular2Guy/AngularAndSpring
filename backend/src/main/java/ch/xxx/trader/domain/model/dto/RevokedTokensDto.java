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

import java.util.LinkedList;
import java.util.List;

import ch.xxx.trader.domain.model.entity.RevokedToken;

public class RevokedTokensDto {
	private List<RevokedToken> revokedTokens = new LinkedList<>();

	public RevokedTokensDto() {		
	}
	
	public RevokedTokensDto(List<RevokedToken> revokedTokens) {
		super();
		this.revokedTokens = revokedTokens;
	}

	public List<RevokedToken> getRevokedTokens() {
		return revokedTokens;
	}

	public void setRevokedTokens(List<RevokedToken> revokedTokens) {
		this.revokedTokens = revokedTokens;
	}
}
