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
package ch.xxx.trader.adapter.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ch.xxx.trader.domain.exceptions.AuthenticationException;
import ch.xxx.trader.domain.exceptions.JwtTokenValidationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(JwtTokenValidationException.class)
	public ResponseEntity<?> jwtTokenHandler(Exception ex, WebRequest request) {
		super.logger.warn("Jwt Token invalid.", ex);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<?> authenticationHandler(Exception ex, WebRequest request) {
		super.logger.warn("Unauthenticated.", ex);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
}