/***    Copyright 2016 Sven Loesekann

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
import { AuthGuardService } from './services/auth-guard.service';
import { SplashComponent } from './splash/splash.component';

const routes: Routes = [
    {path: 'overview', loadChildren: () => import('./overview/overview.module').then(m => m.OverviewModule)},
    {path: 'details', loadChildren: () => import('./details/details.module').then(m => m.DetailsModule)},
    {path: 'orderbooks', loadChildren: () => import('./orderbooks/orderbooks.module')
		.then(m => m.OrderbooksModule), canActivate: [AuthGuardService]},
    {path: '**', component: SplashComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
