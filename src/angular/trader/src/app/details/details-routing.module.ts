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
import { Routes, RouterModule } from '@angular/router';
import { BsdetailComponent } from "./bsdetail/bsdetail.component";
import { IbdetailComponent } from "./ibdetail/ibdetail.component";
import { CbdetailComponent } from "./cbdetail/cbdetail.component";
import { BfdetailComponent } from "./bfdetail/bfdetail.component";

const routes: Routes = [
                        {path: 'bsdetail/:currpair', component: BsdetailComponent},
                        {path: 'ibdetail/:currpair', component: IbdetailComponent},
                        {path: 'cbdetail/:currpair', component: CbdetailComponent},
                        {path: 'bfdetail/:currpair', component: BfdetailComponent},
                        ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DetailsRoutingModule { }
