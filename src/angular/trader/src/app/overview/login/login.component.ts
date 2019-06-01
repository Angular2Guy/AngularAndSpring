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
import { Component, OnInit, Inject } from '@angular/core';
import { QuoteoverviewComponent } from '../quoteoverview/quoteoverview.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MyuserService } from '../../services/myuser.service';
import { MyUser } from '../../common/myUser';
import { FormGroup, FormControl, FormBuilder, Validators, AbstractControl } from '@angular/forms';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  signinForm: FormGroup;
  loginForm: FormGroup;
  private user = new MyUser();
  loginFailed = false;
  signinFailed = false;
  pwMatching = true;
  
  constructor(public dialogRef: MatDialogRef<QuoteoverviewComponent>,
          @Inject(MAT_DIALOG_DATA) public data: any, private myuserService: MyuserService, fb: FormBuilder) { 
      this.signinForm = fb.group({
          username: ['', Validators.required],
          password: ['', Validators.required],
          password2: ['', Validators.required],
          email: ['', Validators.required]
      },{
          validator: this.validate.bind(this)
      });
      this.loginForm = fb.group({
          username: ['', Validators.required],
          password: ['', Validators.required]          
      });
  }

  ngOnInit() {
      
  }

  validate(group: FormGroup) {
      if(group.get('password').touched || group.get('password2').touched) {
          this.pwMatching = group.get('password').value === group.get('password2').value && group.get('password').value !== '';
          if(!this.pwMatching) {
              group.get('password').setErrors({MatchPassword: true});
              group.get('password2').setErrors({MatchPassword: true});
          } else {
              group.get('password').setErrors(null);
              group.get('password2').setErrors(null);
          }
      }
      return this.pwMatching;
  }
  
  onSigninClick(): void {
      let myUser = new MyUser();
      myUser.userId = this.signinForm.get('username').value;
      myUser.password = this.signinForm.get('password').value;
      myUser.email = this.signinForm.get('email').value;
//      console.log(this.signinForm);
//      console.log(myUser);
      this.myuserService.postSignin(myUser).subscribe(us => this.signin(us),err => console.log(err));
  }
  
  onLoginClick(): void {
      let myUser = new MyUser();
      myUser.userId = this.loginForm.get('username').value;
      myUser.password = this.loginForm.get('password').value;
//      console.log(myUser);
      this.myuserService.postLogin(myUser).subscribe(us => this.login(us),err => console.log(err));
  }
  
  signin(us: MyUser):void {
      this.user = us;   
      this.data.hash = null;
      if(this.user.userId !== null) {
          this.signinFailed = false;
          this.dialogRef.close();
      } else {
          this.signinFailed = true;
      }      
  }
  
  login(us: MyUser):void {
      this.user = us;
      if(this.user.userId !== null) {
          this.loginFailed = false;
          this.data.hash = us.salt;                    
          this.dialogRef.close(this.data.hash);          
      } else {
          this.loginFailed = true;
      }      
  }
  
  onCancelClick(): void {
      this.dialogRef.close();
  }
}
