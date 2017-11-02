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
import { Injectable } from '@angular/core';
import { Http, Response, RequestOptionsArgs, Headers } from '@angular/http';
import { PlatformLocation } from '@angular/common';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/observable/throw';
import { MyUser } from '../common/myUser';
import { Utils } from './utils';
import { AuthCheck } from '../common/authcheck';

@Injectable()
export class MyuserService {
  private _reqOptionsArgs: RequestOptionsArgs = { headers: new Headers() };
  private _utils = new Utils();
  private myUserUrl = "/myuser";
  private _salt:string = null;
  
  constructor(private http: Http, private pl: PlatformLocation ) { 
      this._reqOptionsArgs.headers.set( 'Content-Type', 'application/json' );
  }

  postLogin(user: MyUser): Observable<MyUser> {
      return this.http.post(this.myUserUrl+'/login', user, this._reqOptionsArgs).map(res => {
              let retval = <MyUser>res.json();
              this._salt = retval.salt;
              retval.password = 'yyy';
              return retval;
          }).catch(this._utils.handleError);
  }

  postSignin(user: MyUser): Observable<MyUser> {
      return this.http.post(this.myUserUrl+'/signin', user, this._reqOptionsArgs).map(res => {
              let retval = <MyUser>res.json();              
              retval.salt = 'xxx';
              retval.password = 'yyy';
              return retval;
          }).catch(this._utils.handleError);
  }
  
  postCheckAuthorisation(path: string): Observable<AuthCheck> {      
      let authcheck = new AuthCheck();
      authcheck.hash = this._salt;
      authcheck.path = path;
      return this.http.post(this.myUserUrl+'/authorize', authcheck, this._reqOptionsArgs).map(res => <AuthCheck>res.json()).catch(this._utils.handleError);
  }
     
  logout(): void {
      this._salt = null;
  }
  
  get salt():string {
      return this._salt;
  }
}
