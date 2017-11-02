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
package ch.xxx.trader;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.xxx.trader.clients.AuthCheck;
import ch.xxx.trader.clients.MyUser;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/myuser")
public class MyUserController {
	@Autowired
	private ReactiveMongoOperations operations;
	@Autowired
	private PasswordEncryption passwordEncryption;

	@PostMapping("/authorize")
	public Mono<AuthCheck> postAuthorize(@RequestBody AuthCheck authcheck) {
		Query query = new Query();
		query.addCriteria(Criteria.where("salt").is(authcheck.getHash()));
		return this.operations.findOne(query, MyUser.class).switchIfEmpty(Mono.just(new MyUser())).map(user -> mapMyUser(user, authcheck));
	}

	private AuthCheck mapMyUser(MyUser myUser, AuthCheck authcheck) {
		if(myUser.getUserId() != null) {
			return new AuthCheck(authcheck.getHash(), authcheck.getPath(), true);
		}
		return new AuthCheck(authcheck.getHash(), authcheck.getPath(), false);
	}
	
	@PostMapping("/signin")
	public Mono<MyUser> postUserSignin(@RequestBody MyUser myUser)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(myUser.getUserId()));
		MyUser user = this.operations.findOne(query, MyUser.class).switchIfEmpty(Mono.just(new MyUser())).block();
		if (user.getUserId() == null) {
			String salt = this.passwordEncryption.generateSalt();
			String encryptedPassword = this.passwordEncryption.getEncryptedPassword(myUser.getPassword(), salt);
			myUser.setPassword(encryptedPassword);
			myUser.setSalt(salt);
			this.operations.save(myUser).block();
			return Mono.just(myUser);
		}
		return Mono.just(user);
	}

	@PostMapping("/login")
	public Mono<MyUser> postUserLogin(@RequestBody MyUser myUser)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(myUser.getUserId()));
		return this.operations.findOne(query, MyUser.class).switchIfEmpty(Mono.just(new MyUser())).map(user1 ->  loginHelp(user1, myUser.getPassword()));
	}

	private MyUser loginHelp(MyUser user, String passwd) {
		if (user.getUserId() != null) {
			String encryptedPassword;
			try {
				encryptedPassword = this.passwordEncryption.getEncryptedPassword(passwd, user.getSalt());
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				return new MyUser();
			}
			if (user.getPassword().equals(encryptedPassword)) {
				return user;
			}
		}
		return new MyUser();
	}
}
