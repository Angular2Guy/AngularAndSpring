import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OrderbooksRoutingModule } from './orderbooks-routing.module';
import { OrderbooksComponent } from "./orderbooks/orderbooks.component";
import { MaterialModule } from "../material.module";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    OrderbooksRoutingModule
  ],
  declarations: [    
    OrderbooksComponent    
  ]
})
export class OrderbooksModule { }
