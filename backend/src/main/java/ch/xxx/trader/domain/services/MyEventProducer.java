package ch.xxx.trader.domain.services;

import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;

public interface MyEventProducer {
	MyUser sendNewUser(MyUser dto);
	RevokedToken sendUserLogout(RevokedToken dto);
}