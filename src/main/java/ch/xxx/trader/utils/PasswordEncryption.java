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
package ch.xxx.trader.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

@Component
public class PasswordEncryption {

	public boolean authenticate(String attemptedPassword, String encryptedPassword, String salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Encrypt the clear-text password using the same salt that was used to
		// encrypt the original password
		String encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);
		// Authentication succeeds if encrypted password that the user entered
		// is equal to the stored hash
		return encryptedPassword.equals(encryptedAttemptedPassword);

	}

	public String getEncryptedPassword(String password, String salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
		// specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
		String algorithm = "PBKDF2WithHmacSHA1";
		// SHA-1 generates 160 bit hashes, so that's what makes sense here
		int derivedKeyLength = 160;
		// Pick an iteration count that works for you. The NIST recommends at
		// least 1,000 iterations:
		// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
		// iOS 4.x reportedly uses 10,000:
		// http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
		int iterations = 20000;
		char[] pwd = new String(password).toCharArray();		
		KeySpec spec = new PBEKeySpec(pwd, Base64.getDecoder().decode(salt), iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		byte[] bytes = f.generateSecret(spec).getEncoded();
		return Base64.getEncoder().encodeToString(bytes);
	}

	public String generateSalt() throws NoSuchAlgorithmException {
		// VERY important to use SecureRandom instead of just Random
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

}