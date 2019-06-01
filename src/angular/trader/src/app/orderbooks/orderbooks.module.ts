import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OrderbooksRoutingModule } from './orderbooks-routing.module';
import { OrderbooksComponent } from "./orderbooks/orderbooks.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatToolbarModule } from '@angular/material/toolbar';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatSelectModule,
    MatRadioModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatListModule,    
    OrderbooksRoutingModule
  ],
  declarations: [    
    OrderbooksComponent    
  ]
})
export class OrderbooksModule { }
