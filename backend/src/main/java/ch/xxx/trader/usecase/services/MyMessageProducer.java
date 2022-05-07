package ch.xxx.trader.usecase.services;

import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;

public interface MyMessageProducer {
	void sendNewUser(MyUser dto);
	void sendUserLogout(RevokedToken dto);
}
