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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DetailsRoutingModule } from './details-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { IbdetailComponent } from './ibdetail/ibdetail.component';
import { CbdetailComponent } from './cbdetail/cbdetail.component';
import { BsdetailComponent } from './bsdetail/bsdetail.component';
import { BfdetailComponent } from './bfdetail/bfdetail.component';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NgxSimpleChartsModule } from 'ngx-simple-charts';
import { NgxLineChartsModule } from 'ngx-simple-charts/line';
//import { NgxSimpleChartsModule } from '../charts/ngx-simple-charts.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatRadioModule,
    MatButtonModule,
    DetailsRoutingModule,
    NgxSimpleChartsModule,
	NgxLineChartsModule
  ],
  declarations: [
    IbdetailComponent,
    CbdetailComponent,
    BsdetailComponent,
    BfdetailComponent
  ]
})
export class DetailsModule { }
