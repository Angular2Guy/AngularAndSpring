/**
 *    Copyright 2018 Sven Loesekann
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

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RandomNumberTest {
	private Random rand = new Random();

	@Test
	public void generateRandomNumber() {
		Assertions.assertNotNull(rand.nextLong());
	}

	// generates the bas64 encoded random number for the jwt signature
	// property: security.jwt.token.secret-key
	@Test
	public void generateBase64RandomNumber() {
		final int numberOfBytes = 32;
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES*numberOfBytes);
		for(int i = 0;i<numberOfBytes;i++) {
			buffer.putLong(i*8,rand.nextLong());
		}
		String encoded = Base64.getUrlEncoder().encodeToString(buffer.array());
		Assertions.assertFalse(encoded.isBlank());
		Assertions.assertArrayEquals(buffer.array(), Base64.getUrlDecoder().decode(encoded));
		//System.out.println(encoded);
	}
}

