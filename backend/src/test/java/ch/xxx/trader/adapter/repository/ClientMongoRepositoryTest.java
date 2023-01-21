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
package ch.xxx.trader.adapter.repository;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ch.xxx.trader.adapter.config.FlapDoodleConfig;
import ch.xxx.trader.domain.model.entity.MyUser;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = "ch.xxx.trader", includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FlapDoodleConfig.class))
@TestMethodOrder(OrderAnnotation.class)
public class ClientMongoRepositoryTest {
	private static final String USERID = "userId";
	@Autowired
	private ClientMongoRepository clientMongoRepository;
	
	@Test
	@Order(1)
	public void saveMyUser() throws Exception {		
		MyUser myUser = this.createMyUser();
		MyUser result = this.clientMongoRepository.save(myUser).block();
		Assertions.assertNotNull(result);
		Assertions.assertEquals(myUser.getUserId(), result.getUserId());
		Assertions.assertEquals(myUser.getPassword(), result.getPassword());
	}
	
	@Test
	@Order(2)
	public void findMyUserFound() throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(USERID));
		Optional<MyUser> resultOpt = this.clientMongoRepository.findOne(query, MyUser.class).blockOptional();
		Assertions.assertTrue(resultOpt.isPresent());
		Assertions.assertEquals(USERID, resultOpt.get().getUserId());
	}
	
	@Test
	@Order(3)
	public void findMyUserNotFound() throws Exception {
		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is("XXX123"));
		Optional<MyUser> resultOpt = this.clientMongoRepository.findOne(query, MyUser.class).blockOptional();
		Assertions.assertFalse(resultOpt.isPresent());
	}
	
	private MyUser createMyUser() {
		MyUser myUser = new MyUser();
		myUser.setCreatedAt(new Date());
		myUser.setEmail("email");
		myUser.setPassword("password");
		myUser.setSalt("salt");
		myUser.setToken("token");
		myUser.setUserId(USERID);		
		return myUser;
	}
}
