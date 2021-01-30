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
import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from "@angular/router";
import { trigger, state, animate, transition, style } from '@angular/animations';

@Component({
  selector: 'app-splash',
  templateUrl: './splash.component.html',
  styleUrls: ['./splash.component.scss'],
  animations: [
               trigger( 'showSplash', [
                   state( 'true', style( { opacity: 1 } ) ),
                   state( 'false', style( { opacity: 0 } ) ),
                   transition( '1 => 0', animate( '750ms' ) ),
                   transition( '0 => 1', animate( '750ms' ) )
               ])]  
})
export class SplashComponent implements OnInit,AfterViewInit {    
  myState = false;
  
  constructor(private router: Router) { }

  ngOnInit() {      
      this.router.navigateByUrl("overview");
  }
  
  ngAfterViewInit(): void {
      setTimeout(() => this.myState = true);
  }

}
