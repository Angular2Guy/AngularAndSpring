import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from "@angular/router";
import { trigger, state, animate, transition, style } from '@angular/animations';

@Component({
  selector: 'app-splash',
  templateUrl: './splash.component.html',
  styleUrls: ['./splash.component.scss'],
  animations: [
               trigger( 'showSplash', [
                   state( 'true', style( { opacity: 1 } ) ),
                   state( 'false', style( { opacity: 0 } ) ),
                   transition( '1 => 0', animate( '750ms' ) ),
                   transition( '0 => 1', animate( '750ms' ) )
               ])]  
})
export class SplashComponent implements OnInit,AfterViewInit {    
  myState = false;
  
  constructor(private router: Router) { }

  ngOnInit() {      
      this.router.navigateByUrl("overview");
  }
  
  ngAfterViewInit(): void {
      setTimeout(() => this.myState = true);
  }

}
