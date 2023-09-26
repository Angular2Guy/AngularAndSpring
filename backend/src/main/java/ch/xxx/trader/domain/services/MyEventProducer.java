package ch.xxx.trader.domain.services;

import ch.xxx.trader.domain.model.entity.MyUser;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import reactor.core.publisher.Mono;

public interface MyEventProducer {
	Mono<MyUser> sendNewUser(MyUser dto);
	Mono<RevokedToken> sendUserLogout(RevokedToken dto);
}
