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

import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.Date;

public interface QuotePairBf {
    ObjectId get_id();
    void set_id(ObjectId _id);

    String getPair();
    void setPair(String pair);

    Date getCreatedAt();
    void setCreatedAt(Date createdAt);

    BigDecimal getMid();
    void setMid(BigDecimal mid);

    BigDecimal getBid();
    void setBid(BigDecimal bid);

    BigDecimal getAsk();
    void setAsk(BigDecimal ask);

    BigDecimal getLast_price();
    void setLast_price(BigDecimal last_price);

    BigDecimal getLow();
    void setLow(BigDecimal low);

    BigDecimal getHigh();
    void setHigh(BigDecimal high);

    BigDecimal getVolume();
    void setVolume(BigDecimal volume);

    String getTimestamp();
    void setTimestamp(String timestamp);
}
