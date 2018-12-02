import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OrderbooksComponent } from "./orderbooks/orderbooks.component";

const routes: Routes = [
{
    path: '',
    component: OrderbooksComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrderbooksRoutingModule { }
