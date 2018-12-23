import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OrderbooksRoutingModule } from './orderbooks-routing.module';
import { OrderbooksComponent } from "./orderbooks/orderbooks.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatToolbarModule,
        MatSelectModule,
        MatRadioModule,
        MatInputModule,
        MatCheckboxModule,
        MatButtonModule,
        MatListModule,
        } from '@angular/material';

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
