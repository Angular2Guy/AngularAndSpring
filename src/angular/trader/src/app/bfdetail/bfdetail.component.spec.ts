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
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BitfinexService} from '../services/bitfinex.service';
import { BfdetailComponent } from './bfdetail.component';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { MaterialModule } from '../material.module';
import { ChartsModule } from 'ng2-charts';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { of, Observable } from 'rxjs';
import { QuoteBf } from '../common/quoteBf';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PlatformLocation } from '@angular/common';

class MockBfService extends BitfinexService {
    constructor(private http1: HttpClient, private pl1: PlatformLocation ) {
        super(http1, pl1);
    }
    getCurrentQuote(currencypair: string): Observable<QuoteBf> {
        let quoteBf = { 
                _id: 'id',
                pair: 'pair',
                createdAt: new Date('2018-01-01'),
                mid: 1,
                bid: 2,
                ask: 3,
                last_price: 4,
                low: 5,
                high: 6,
                volume: 7,
                timestamp: 'timestamp'
         };
        return of(quoteBf);
    }
    getTodayQuotes(currencypair: string): Observable<QuoteBf[]> {
        return of([]);
    }
}

describe('BfdetailComponent', () => {
  let component: BfdetailComponent;
  let fixture: ComponentFixture<BfdetailComponent>;
  let mockService = new MockBfService(null,null); 

  beforeEach(async(() => {              
    TestBed.configureTestingModule({
      imports: [ 
                RouterTestingModule,
                BrowserModule,
                FormsModule,
                ReactiveFormsModule,
                HttpModule,
                HttpClientModule,
                BrowserAnimationsModule,
                MaterialModule,
                ChartsModule,                
              ],
      declarations: [ BfdetailComponent ],
      providers:  [{provide: BitfinexService, useValue: mockService } ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BfdetailComponent);
    component = fixture.componentInstance;
    const quoteBf = { 
            _id: 'id',
            pair: 'pair',
            createdAt: new Date('2018-01-01'),
            mid: 1,
            bid: 2,
            ask: 3,
            last_price: 4,
            low: 5,
            high: 6,
            volume: 7,
            timestamp: 'timestamp'
     };
    fixture.detectChanges();          
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should have value', () => {
      expect(component.currQuote.mid).toBe(1);
  }); 
  it('should show last_price', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#last_price')).nativeElement;      
      expect(el.textContent).toEqual('4.00');      
  });
  it('should show high', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#high')).nativeElement;      
      expect(el.textContent).toEqual('6.00');      
  });
  it('should show low', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#low')).nativeElement;      
      expect(el.textContent).toEqual('5.00');      
  });
  it('should show bid', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#bid')).nativeElement;      
      expect(el.textContent).toEqual('2.00');      
  });
  it('should show ask', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#ask')).nativeElement;      
      expect(el.textContent).toEqual('3.00');      
  });
  it('should show mid', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#mid')).nativeElement;      
      expect(el.textContent).toEqual('1.00');      
  });
  it('should show createdAt', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#createdAt')).nativeElement;      
      expect(el.textContent).toEqual('01:00:00');      
  });
  it('should show volume', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#volume')).nativeElement;      
      expect(el.textContent).toEqual('7.00');      
  });
});

