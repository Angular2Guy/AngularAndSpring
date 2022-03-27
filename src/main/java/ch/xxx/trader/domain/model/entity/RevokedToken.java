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
package ch.xxx.trader.domain.model.entity;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document
public class RevokedToken {
	@Id
	private ObjectId _id;
	@JsonProperty
	private String name;
	@JsonProperty
	private String uuid;
	@JsonProperty
	private LocalDateTime lastLogout;
	
	public RevokedToken() {}
	
	public RevokedToken(ObjectId _id, String name, String uuid, LocalDateTime lastLogout) {
		super();
		this._id = _id;
		this.name = name;
		this.uuid = uuid;
		this.lastLogout = lastLogout;
	}

	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public LocalDateTime getLastLogout() {
		return lastLogout;
	}
	public void setLastLogout(LocalDateTime lastLogout) {
		this.lastLogout = lastLogout;
	}
}
