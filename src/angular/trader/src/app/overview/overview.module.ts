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
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BitstampService } from '../services/bitstamp.service';
import { CoinbaseService } from '../services/coinbase.service';
import { ItbitService } from '../services/itbit.service';
import { BitfinexService} from '../services/bitfinex.service';
import { MyuserService } from '../services/myuser.service';
import { AuthGuardService } from '../services/auth-guard.service';
import { MatTableModule,MatToolbarModule,MatTabsModule,MatButtonModule, MatDialogModule,MatFormFieldModule,MatInputModule } from '@angular/material';
import { OverviewRoutingModule } from './overview-routing.module';
import { LoginComponent } from "./login/login.component";
import { QuoteoverviewComponent } from "./quoteoverview/quoteoverview.component";
import { FormsModule,ReactiveFormsModule } from '@angular/forms';

@NgModule({
    entryComponents: [
                      LoginComponent
                    ],
    imports: [
      CommonModule,
      OverviewRoutingModule,
      MatTableModule,
      MatToolbarModule,
      MatTabsModule, 
      MatButtonModule,
      MatDialogModule,
      MatFormFieldModule,
      MatInputModule,
      FormsModule,
      ReactiveFormsModule
    ],
    
  declarations: [
      LoginComponent,
      QuoteoverviewComponent
                 ]
})
export class OverviewModule { }
