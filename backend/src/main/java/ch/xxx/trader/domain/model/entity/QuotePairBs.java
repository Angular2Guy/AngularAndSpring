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

public interface QuotePairBs {
    BigDecimal getHigh();
    void setHigh(BigDecimal high);

    BigDecimal getLast();
    void setLast(BigDecimal last);

    Date getTimestamp();
    void setTimestamp(Date timestamp);

    BigDecimal getBid();
    void setBid(BigDecimal bid);

    BigDecimal getVwap();
    void setVwap(BigDecimal vwap);

    BigDecimal getVolume();
    void setVolume(BigDecimal volume);

    BigDecimal getLow();
    void setLow(BigDecimal low);

    BigDecimal getAsk();
    void setAsk(BigDecimal ask);

    BigDecimal getOpen();
    void setOpen(BigDecimal open);

    ObjectId get_id();
    void set_id(ObjectId _id);

    Date getCreatedAt();
    void setCreatedAt(Date createdAt);

    String getPair();
    void setPair(String pair);
}
