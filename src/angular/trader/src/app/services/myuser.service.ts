/*
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
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { MyUser } from '../common/my-user';
import { Utils } from './utils';
import { AuthCheck } from '../common/authcheck';
import { TokenService } from './token.service';

@Injectable({providedIn: 'root'})
export class MyuserService {
  private reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
  private utils = new Utils();
  private myUserUrl = '/myuser';

  constructor(private http: HttpClient, private tokenService: TokenService) {}

  postLogin(user: MyUser): Observable<MyUser> {
      return this.http.post<MyUser>(this.myUserUrl+'/login', user, this.reqOptionsArgs).pipe(map(res => {
          const retval = res as MyUser;
          this.tokenService.token = res.token;
          this.tokenService.userId = res.userId;
          return retval;
      }),catchError(this.utils.handleError<MyUser>('postLogin')));
  }

  postSignin(user: MyUser): Observable<MyUser> {
      return this.http.post<MyUser>(this.myUserUrl+'/signin', user, this.reqOptionsArgs).pipe(map(res => {
              const retval = res as MyUser;
              retval.salt = 'xxx';
              retval.password = 'yyy';
              return retval;
          }),catchError(this.utils.handleError<MyUser>('postSignin')));
  }

  postCheckAuthorisation(path: string): Observable<AuthCheck> {
      const authcheck = new AuthCheck();
      authcheck.path = path;
      const reqOptions = {headers: this.utils.createTokenHeader()};
      return this.http.post<AuthCheck>(this.myUserUrl+'/authorize', authcheck, reqOptions)
		.pipe(catchError(this.utils.handleError<AuthCheck>('postCheckAuthorisation')));
  }

  postLogout(): Observable<boolean> {
      return this.http.put<boolean>(this.myUserUrl+'/logout', this.reqOptionsArgs)
		.pipe(catchError(this.utils.handleError<boolean>('postLogout')));
  }
}
