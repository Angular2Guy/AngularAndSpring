/*
   Copyright 2016 Sven Loesekann

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
import { Component, Inject, LOCALE_ID, OnInit } from '@angular/core';
import { environment } from './../environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'app';

  constructor(@Inject(LOCALE_ID) private locale: string) {}

  ngOnInit(): void {
	//console.log(window.location.href);
	//console.log(this.locale);
	if(environment.production && window.location.href.split('/').length > 3) {
		let urlStr = '/' + window.location.href.split('/').slice(3).join('/');
		urlStr = urlStr.indexOf(`/${this.locale}/`) !== 0 ? `/${this.locale}` + urlStr : urlStr;
		//console.log(urlStr);
		window.history.pushState({foo: 'bar'},'', urlStr);
	}
  }
}
