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
import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatTableModule } from "@angular/material/table";
import { MatTabsModule } from "@angular/material/tabs";
import { MatToolbarModule } from "@angular/material/toolbar";
import { OverviewRoutingModule } from "./overview-routing.module";
import { LoginComponent } from "./login/login.component";
import { QuoteoverviewComponent } from "./quoteoverview/quoteoverview.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { LuxonDateModule } from "@angular/material-luxon-adapter";

@NgModule({
  imports: [
    CommonModule,
    OverviewRoutingModule,
    MatTableModule,
    MatToolbarModule,
    MatTabsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatInputModule,
    LuxonDateModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  declarations: [LoginComponent, QuoteoverviewComponent],
})
export class OverviewModule {}
