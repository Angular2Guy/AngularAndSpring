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
package dtos;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthCheck {
	@JsonProperty
	private final Date createdAt = new Date();
	private final String hash;
	private final String path;
	private final boolean authorized;
	
	public AuthCheck(@JsonProperty("hash") String hash,@JsonProperty("path") String path, @JsonProperty("authorized") boolean authorized) {
		super();
		this.hash = hash;
		this.path = path;
		this.authorized = authorized;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public String getHash() {
		return hash;
	}

	public String getPath() {
		return path;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	@Override
	public String toString() {
		return "AuthCheck [createdAt=" + createdAt + ", hash=" + hash + ", path=" + path + ", authorized=" + authorized
				+ "]";
	}

}
