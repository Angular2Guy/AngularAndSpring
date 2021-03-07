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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.xxx.trader.utils.PasswordEncryption;



public class PasswordEncryptionTest {
	PasswordEncryption pe = new PasswordEncryption();
	
	@Test
	public void abcEncryption() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String salt = pe.generateSalt();
		String encryptedPassword = pe.getEncryptedPassword("abc", salt);
		Assertions.assertTrue(pe.authenticate("abc", encryptedPassword, salt));
	}
	
	@Test
	public void strNumEncryption() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String salt = pe.generateSalt();
		String encryptedPassword = pe.getEncryptedPassword("abc123", salt);
		Assertions.assertTrue(pe.authenticate("abc123", encryptedPassword, salt));
	}	
	
	@Test
	public void realPwdEncryption() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String salt = pe.generateSalt();
		String encryptedPassword = pe.getEncryptedPassword("abc123%&/?!", salt);
		Assertions.assertTrue(pe.authenticate("abc123%&/?!", encryptedPassword, salt));
	}
	
}
