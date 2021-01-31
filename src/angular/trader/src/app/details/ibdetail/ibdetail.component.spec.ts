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
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { of, Observable } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { IbdetailComponent } from './ibdetail.component';
import { ItbitService } from '../../services/itbit.service';
import { QuoteIb } from '../../common/quote-ib';
import { MatRadioModule } from '@angular/material/radio';
import { MatToolbarModule } from '@angular/material/toolbar';

class MockService extends ItbitService {
    constructor(private http1: HttpClient) {
        super(http1);
    }

    getCurrentQuote(currencypair: string): Observable<QuoteIb> {
        const result: QuoteIb = {
            _id: 'id',
            createdAt: '2018-01-01',
            pair: 'pair',
            bid: 1,
            bidAmt: 2,
            ask: 3,
            askAmt: 4,
            lastPrice: 5,
            stAmt: 6,
            volume24h: 7,
            volumeToday: 8,
            high24h: 9,
            low24h: 10,
            openToday: 11,
            vwapToday: 12,
            vwap24h: 13,
            serverTimeUTC: '2018-01-01'
        };
        return of(result);
    }
     
    getTodayQuotes(currencypair: string): Observable<QuoteIb[]> {
        return of([]);
    }
} 

describe('IbdetailComponent', () => {
  let component: IbdetailComponent;
  let fixture: ComponentFixture<IbdetailComponent>;
  let mockService = new MockService(null);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
        imports: [RouterTestingModule,
                  BrowserModule,
                  FormsModule,
                  ReactiveFormsModule,                  
                  HttpClientModule,
                  BrowserAnimationsModule,                 
                  MatToolbarModule, 
                  MatRadioModule],
      declarations: [ IbdetailComponent ],
      providers:  [{provide: ItbitService, useValue: mockService } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IbdetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should have value', () => {
      expect(component.currQuote.ask).toBe(3);
  });
  it('should show lastPrice', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#lastPrice')).nativeElement;      
      expect(el.textContent).toEqual('5.00');      
  });
  it('should show high24h', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#high24h')).nativeElement;      
      expect(el.textContent).toEqual('9.00');      
  });
  it('should show low24h', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#low24h')).nativeElement;      
      expect(el.textContent).toEqual('10.00');      
  });
  it('should show bid', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#bid')).nativeElement;      
      expect(el.textContent).toEqual('1.00');      
  });
  it('should show ask', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#ask')).nativeElement;      
      expect(el.textContent).toEqual('3.00');      
  });
  it('should show openToday', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#openToday')).nativeElement;      
      expect(el.textContent).toEqual('11.00');      
  });
  it('should show vwap24h', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#vwap24h')).nativeElement;      
      expect(el.textContent).toEqual('13.00');      
  });
  it('should show createdAt', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#createdAt')).nativeElement; 
      const myDate = new Date(component.currQuote.createdAt);
      let dateStr = (myDate.getMinutes().toString().length === 1 ? '0' + myDate.getMinutes() : myDate.getMinutes())
      +':'+(myDate.getSeconds().toString().length === 1 ? '0' + myDate.getSeconds() : myDate.getSeconds());
      expect(el.textContent.substr(3,el.textContent.length)).toEqual(dateStr);      
  });
  it('should show volume24', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#volume24')).nativeElement;      
      expect(el.textContent).toEqual('7.00');      
  });
});
