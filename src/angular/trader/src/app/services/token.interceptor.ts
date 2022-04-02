/**
 *    Copyright 2019 Sven Loesekann
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
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { TokenService } from './token.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(private tokenService: TokenService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
	req = req.clone({
		headers: this.tokenService.createTokenHeader()
	});
  	return next.handle(req).pipe(tap(event => event, event => this.handleError(event)));
  }

  private handleError(event: HttpEvent<any>): HttpEvent<any> {
	if(event instanceof HttpErrorResponse) {
		const error = <HttpErrorResponse> event;
		console.log(`failed: ${error.message}`);
	}
	return event;
  }
}