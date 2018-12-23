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
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { QuoteoverviewComponent } from './quoteoverview/quoteoverview.component';
import { BitstampService } from './services/bitstamp.service';
import { CoinbaseService } from './services/coinbase.service';
import { ItbitService } from './services/itbit.service';
import { BitfinexService} from './services/bitfinex.service';
import { LoginComponent } from './login/login.component';
import { MyuserService } from './services/myuser.service';
import { AuthGuardService } from './services/auth-guard.service';
import { MatTableModule,MatToolbarModule,MatTabsModule,MatButtonModule, MatDialogModule,MatFormFieldModule,MatInputModule } from '@angular/material';

  
@NgModule({
  declarations: [
    AppComponent,
    QuoteoverviewComponent,
    LoginComponent,
  ],
  entryComponents: [
    LoginComponent
  ],
  imports: [    
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    HttpClientModule,
    BrowserAnimationsModule,  
    AppRoutingModule,
    MatTableModule,
    MatToolbarModule,
    MatTabsModule, 
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
