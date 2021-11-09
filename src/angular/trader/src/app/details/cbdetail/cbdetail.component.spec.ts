/*
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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { of, Observable } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { CbdetailComponent } from './cbdetail.component';
import { CoinbaseService } from '../../services/coinbase.service';
import { QuoteCb, QuoteCbSmall } from '../../common/quote-cb';
import { MatRadioModule } from '@angular/material/radio';
import { MatToolbarModule } from '@angular/material/toolbar';
//import { NgxSimpleChartsModule } from 'src/app/charts/ngx-simple-charts.module';
import { NgxSimpleChartsModule } from 'ngx-simple-charts';
import { NgxLineChartsModule } from 'ngx-simple-charts/line';


//{"AED":30446.14,"AFN":588333.46,"ALL":894939.58,"AMD":4014504.15,"ANG":14884.19,"AOA":1903986.64,"ARS":201164.55,"AUD":11028.00,"AWG":14812.73,"AZN":14111.65,"BAM":13754.33,"BBD":16577.56,"BDT":703178.24,"BGN":13738.65,"BHD":3127.713,"BIF":14578207,"BMD":8288.78,"BND":11038.17,"BOB":57299.09,"BRL":30523.42,"BSD":8288.78,"BTC":1.00000000,"BTN":562230.25,"BWP":82310.36,"BYN":16605.18,"BYR":166051782,"BZD":16668.31,"CAD":10600.94,"CDF":13372478.79,"CHF":8298.63,"CLF":191.1393,"CLP":5225247,"CNY":52827.71,"COP":23782499.13,"CRC":4694786.20,"CUC":8288.78,"CVE":775485.82,"CZK":179273.32,"DJF":1475817,"DKK":52310.24,"DOP":411082.04,"DZD":962037.25,"EEK":121129.67,"EGP":148203.39,"ERN":125081.83,"ETB":228217.78,"ETH":11.88848600,"EUR":7031.92,"FJD":17294.15,"FKP":6147.58,"GBP":6168.36,"GEL":20167.53,"GGP":6147.58,"GHS":38021.02,"GIP":6147.58,"GMD":391188.97,"GNF":74815772,"GTQ":61592.53,"GYD":1732214.33,"HKD":65075.63,"HNL":197841.79,"HRK":51863.00,"HTG":534996.18,"HUF":2218668,"IDR":116699462.10,"ILS":29762.94,"IMP":6147.58,"INR":561108.96,"IQD":9883961.704,"ISK":865329,"JEP":6147.58,"JMD":1040265.51,"JOD":5879.248,"JPY":913792,"KES":831474.76,"KGS":566779.74,"KHR":33699278.41,"KMF":3410419,"KRW":8937626,"KWD":2503.236,"KYD":6910.78,"KZT":2728583.49,"LAK":69082422.47,"LBP":12545482.97,"LKR":1310953.44,"LRD":1097851.25,"LSL":104179.10,"LTC":59.97001499,"LTL":26730.03,"LVL":5439.60,"LYD":11217.761,"MAD":78191.38,"MDL":137380.10,"MGA":26909109.4,"MKD":432477.11,"MMK":11215133.78,"MNT":19877232.78,"MOP":67045.52,"MRO":2950805.3,"MTL":5667.35,"MUR":283907.29,"MVR":128476.43,"MWK":5967465.72,"MXN":162421.13,"MYR":32885.47,"MZN":498404.34,"NAD":101721.15,"NGN":2982929.39,"NIO":260051.50,"NOK":67005.34,"NPR":899720.93,"NZD":12017.95,"OMR":3191.172,"PAB":8288.78,"PEN":27118.90,"PGK":27070.60,"PHP":432715.76,"PKR":958398.48,"PLN":30006.63,"PYG":46456954,"QAR":30179.44,"RON":32565.03,"RSD":829270.68,"RUB":511397.00,"RWF":7208517,"SAR":31089.97,"SBD":64805.29,"SCR":111529.28,"SEK":72193.08,"SGD":11109.47,"SHP":6147.58,"SLL":63846448.73,"SOS":4795653.79,"SRD":61900.61,"SSP":1079724.66,"STD":171355480.85,"SVC":72563.56,"SZL":103926.13,"THB":265294.84,"TJS":74424.43,"TMT":29010.61,"TND":20867.841,"TOP":18875.90,"TRY":36606.32,"TTD":55889.17,"TWD":247444.95,"TZS":18913338.20,"UAH":217501.73,"UGX":30811053,"USD":8288.78,"UYU":253198.76,"UZS":66551443.50,"VEF":579489331.75,"VND":188732016,"VUV":890740,"WST":21341.63,"XAF":4607113,"XAG":506,"XAU":6,"XCD":22400.84,"XDR":5808,"XOF":4607113,"XPD":8,"XPF":838126,"XPT":9,"YER":2075192.57,"ZAR":103133.20,"ZMK":43541585.11,"ZMW":83306.10,"ZWL":2671929.77,"_id":{"timestamp":1526498426,"machineIdentifier":694106,"processIdentifier":3590,"counter":15517016,"date":"2018-05-16T19:20:26.000+0000","time":1526498426000,"timeSecond":1526498426},"createdAt":"2018-05-16T19:20:26.320+0000"};
class MockService extends CoinbaseService {
    getCurrentQuote(): Observable<QuoteCb> {
        const result1 = {"AED":30446.14,"AFN":588333.46,"ALL":894939.58,"AMD":4014504.15,"ANG":14884.19,"AOA":1903986.64,"ARS":201164.55,"AUD":11028.00,"AWG":14812.73,"AZN":14111.65,"BAM":13754.33,"BBD":16577.56,"BDT":703178.24,"BGN":13738.65,"BHD":3127.713,"BIF":14578207,"BMD":8288.78,"BND":11038.17,"BOB":57299.09,"BRL":30523.42,"BSD":8288.78,"BTC":1.00000000,"BTN":562230.25,"BWP":82310.36,"BYN":16605.18,"BYR":166051782,"BZD":16668.31,"CAD":10600.94,"CDF":13372478.79,"CHF":8298.63,"CLF":191.1393,"CLP":5225247,"CNY":52827.71,"COP":23782499.13,"CRC":4694786.20,"CUC":8288.78,"CVE":775485.82,"CZK":179273.32,"DJF":1475817,"DKK":52310.24,"DOP":411082.04,"DZD":962037.25,"EEK":121129.67,"EGP":148203.39,"ERN":125081.83,"ETB":228217.78,"ETH":11.88848600,"EUR":7031.92,"FJD":17294.15,"FKP":6147.58,"GBP":6168.36,"GEL":20167.53,"GGP":6147.58,"GHS":38021.02,"GIP":6147.58,"GMD":391188.97,"GNF":74815772,"GTQ":61592.53,"GYD":1732214.33,"HKD":65075.63,"HNL":197841.79,"HRK":51863.00,"HTG":534996.18,"HUF":2218668,"IDR":116699462.10,"ILS":29762.94,"IMP":6147.58,"INR":561108.96,"IQD":9883961.704,"ISK":865329,"JEP":6147.58,"JMD":1040265.51,"JOD":5879.248,"JPY":913792,"KES":831474.76,"KGS":566779.74,"KHR":33699278.41,"KMF":3410419,"KRW":8937626,"KWD":2503.236,"KYD":6910.78,"KZT":2728583.49,"LAK":69082422.47,"LBP":12545482.97,"LKR":1310953.44,"LRD":1097851.25,"LSL":104179.10,"LTC":59.97001499,"LTL":26730.03,"LVL":5439.60,"LYD":11217.761,"MAD":78191.38,"MDL":137380.10,"MGA":26909109.4,"MKD":432477.11,"MMK":11215133.78,"MNT":19877232.78,"MOP":67045.52,"MRO":2950805.3,"MTL":5667.35,"MUR":283907.29,"MVR":128476.43,"MWK":5967465.72,"MXN":162421.13,"MYR":32885.47,"MZN":498404.34,"NAD":101721.15,"NGN":2982929.39,"NIO":260051.50,"NOK":67005.34,"NPR":899720.93,"NZD":12017.95,"OMR":3191.172,"PAB":8288.78,"PEN":27118.90,"PGK":27070.60,"PHP":432715.76,"PKR":958398.48,"PLN":30006.63,"PYG":46456954,"QAR":30179.44,"RON":32565.03,"RSD":829270.68,"RUB":511397.00,"RWF":7208517,"SAR":31089.97,"SBD":64805.29,"SCR":111529.28,"SEK":72193.08,"SGD":11109.47,"SHP":6147.58,"SLL":63846448.73,"SOS":4795653.79,"SRD":61900.61,"SSP":1079724.66,"STD":171355480.85,"SVC":72563.56,"SZL":103926.13,"THB":265294.84,"TJS":74424.43,"TMT":29010.61,"TND":20867.841,"TOP":18875.90,"TRY":36606.32,"TTD":55889.17,"TWD":247444.95,"TZS":18913338.20,"UAH":217501.73,"UGX":30811053,"USD":8288.78,"UYU":253198.76,"UZS":66551443.50,"VEF":579489331.75,"VND":188732016,"VUV":890740,"WST":21341.63,"XAF":4607113,"XAG":506,"XAU":6,"XCD":22400.84,"XDR":5808,"XOF":4607113,"XPD":8,"XPF":838126,"XPT":9,"YER":2075192.57,"ZAR":103133.20,"ZMK":43541585.11,"ZMW":83306.10,"ZWL":2671929.77,"_id":"_id","createdAt":"2018-05-16T19:20:26.320+0000"};        
        let result = this.lowercaseKeys1(result1); 
        return of(result); 
    }
    
    lowercaseKeys1(quote: any): QuoteCb {
        for (let p in quote) {
          if( quote.hasOwnProperty(p) && p !== '_id' && p !== 'createdAt') {
            quote[p.toLowerCase()] = quote[p];  
          } 
        }     
        return quote;
    }
    
    getTodayQuotes(): Observable<QuoteCbSmall[]> {
        return of([]);
    }
}


describe('CbdetailComponent', () => {
  let component: CbdetailComponent;
  let fixture: ComponentFixture<CbdetailComponent>;
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
                  MatRadioModule,
                  NgxLineChartsModule,
				  NgxSimpleChartsModule],
      declarations: [ CbdetailComponent ],
      providers: [{provide: CoinbaseService, useValue: mockService }]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CbdetailComponent);
    component = fixture.componentInstance;    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should have value', () => {
      expect(component.currQuote.aed).toBe(30446.14);
  });
  it('should show usd', () => {
      component.currpair = 'btcusd';
      fixture.autoDetectChanges();
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#usd')).nativeElement;
      expect(el.textContent).toEqual('8,288.78');      
  });
  it('should show eur', () => {
      component.currpair = 'btcusd';
      fixture.autoDetectChanges();
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#eur')).nativeElement;
      expect(el.textContent).toEqual('7,031.92');      
  });
  it('should show gbp', () => {
      component.currpair = 'btcusd';
      fixture.autoDetectChanges();
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#gbp')).nativeElement;
      expect(el.textContent).toEqual('6,168.36');      
  });
  
  it('should show createdAt', () => {
      component.currpair = 'btcusd';
      fixture.autoDetectChanges();
      const de: DebugElement = fixture.debugElement;
      const el: HTMLElement = de.query(By.css('#createdAt')).nativeElement;
      const myDate = new Date('2018-05-16T19:20:26');
      const timezoneOffset = myDate.getTimezoneOffset() / 60;      
      let dateStr = (myDate.getMinutes().toString().length === 1 ? '0' + myDate.getMinutes() : myDate.getMinutes())
      +':'+(myDate.getSeconds().toString().length === 1 ? '0' + myDate.getSeconds() : myDate.getSeconds());
      expect(el.textContent.substr(3,el.textContent.length)).toEqual(dateStr);      
  });
  
});
