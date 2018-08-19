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
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { MaterialModule } from '../material.module';
import { ChartsModule } from 'ng2-charts';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { of, Observable } from 'rxjs';
import { BsdetailComponent } from './bsdetail.component';
import { RouterTestingModule } from '@angular/router/testing';
import { BitstampService } from '../services/bitstamp.service';
import { PlatformLocation } from "@angular/common";
import { QuoteBs } from '../common/quoteBs';

class MockService extends BitstampService {
    constructor(private http1: HttpClient, private pl1: PlatformLocation ) {
        super(http1, pl1);
    }

    getCurrentQuote(currencypair: string): Observable<QuoteBs> {
        const result: QuoteBs = {
            _id: '_id',
            pair: 'pair',
            createdAt: new Date('2018-01-01'),
            high: 1,
            last: 2,
            timestamp: new Date('2018-01-01'),
            bid: 3,
            vwap: 4,
            volume: 5,
            low: 6,
            ask: 7,
            open: 8
        };
        return of(result);
    }
     
    getTodayQuotes(currencypair: string): Observable<QuoteBs[]> {
        return of([]);
    }
} 

describe('BsdetailComponent', () => {
  let component: BsdetailComponent;
  let fixture: ComponentFixture<BsdetailComponent>;
  let mockService = new MockService(null,null);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule,
                BrowserModule,
                FormsModule,
                ReactiveFormsModule,
                HttpModule,
                HttpClientModule,
                BrowserAnimationsModule,
                MaterialModule,
                ChartsModule, ],
      declarations: [ BsdetailComponent ],
      providers:  [{provide: BitstampService, useValue: mockService } ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BsdetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });  
  it('should have value', () => {
      expect(component.currQuote.ask).toBe(7);
  });
  it('should show last', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#last')).nativeElement;      
      expect(el.textContent).toEqual('2.00');      
  });
  it('should show high', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#high')).nativeElement;      
      expect(el.textContent).toEqual('1.00');      
  });
  it('should show low', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#low')).nativeElement;      
      expect(el.textContent).toEqual('6.00');      
  });
  it('should show bid', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#bid')).nativeElement;      
      expect(el.textContent).toEqual('3.00');      
  });
  it('should show ask', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#ask')).nativeElement;      
      expect(el.textContent).toEqual('7.00');      
  });
  it('should show open', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#open')).nativeElement;      
      expect(el.textContent).toEqual('8.00');      
  });
  it('should show vwap', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#vwap')).nativeElement;      
      expect(el.textContent).toEqual('4.00');      
  });
  it('should show createdAt', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#createdAt')).nativeElement; 
      const myDate = component.currQuote.createdAt;
      let dateStr = (myDate.getMinutes().toString().length === 1 ? '0' + myDate.getMinutes() : myDate.getMinutes())
      +':'+(myDate.getSeconds().toString().length === 1 ? '0' + myDate.getSeconds() : myDate.getSeconds());
      expect(el.textContent.substr(3,el.textContent.length)).toEqual(dateStr);      
  });
  it('should show volume', () => {
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#volume')).nativeElement;      
      expect(el.textContent).toEqual('5.00');      
  });
});
