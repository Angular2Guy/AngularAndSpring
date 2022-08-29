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
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatisticsComponent } from './statistics.component';
import { StatisticsRoutingModule } from './statistics-routing.module';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import  {MatRadioModule } from '@angular/material/radio';
import { StatisticDetailsComponent } from './statistic-details/statistic-details.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxSimpleChartsModule } from 'ngx-simple-charts';
import { NgxBarChartsModule } from 'ngx-simple-charts/bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@NgModule({
  declarations: [
    StatisticsComponent,
    StatisticDetailsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    StatisticsRoutingModule,
    MatToolbarModule,
    MatButtonModule,
    MatTabsModule,
    MatRadioModule,
    NgxSimpleChartsModule,
    NgxBarChartsModule,
    MatProgressSpinnerModule
  ]
})
export class StatisticsModule { }
