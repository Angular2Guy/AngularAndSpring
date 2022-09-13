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
import { Component, OnInit, Inject } from '@angular/core';
import { QuoteoverviewComponent } from '../quoteoverview/quoteoverview.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MyuserService } from '../../services/myuser.service';
import { MyUser } from '../../common/my-user';
import { FormGroup, FormControl, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { TokenService } from 'ngx-simple-charts/base-service';


enum FormFields {
	Username = 'username',
	Password = 'password',
	Password2 = 'password2',
	Email = 'email',
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  signinForm: FormGroup;
  loginForm: FormGroup;
  loginFailed = false;
  signinFailed = false;
  pwMatching = true;
  private user = new MyUser();

  constructor(public dialogRef: MatDialogRef<QuoteoverviewComponent>, private tokenService: TokenService,
          @Inject(MAT_DIALOG_DATA) public data: any, private myuserService: MyuserService, fb: FormBuilder) {
      this.signinForm = fb.group({
          [FormFields.Username]: ['', Validators.required],
          [FormFields.Password]: ['', Validators.required],
          [FormFields.Password2]: ['', Validators.required],
          [FormFields.Email]: ['', Validators.required]
      },{
          validator: this.validate.bind(this)
      });
      this.loginForm = fb.group({
          [FormFields.Username]: ['', Validators.required],
          [FormFields.Password]: ['', Validators.required]
      });
  }

  ngOnInit() {}

  validate(group: FormGroup) {
      if(group.get(FormFields.Password).touched || group.get(FormFields.Password2).touched) {
          this.pwMatching = group.get(FormFields.Password).value === group.get(FormFields.Password2).value && group.get(FormFields.Password).value !== '';
          if(!this.pwMatching) {
              // eslint-disable-next-line @typescript-eslint/naming-convention
              group.get(FormFields.Password).setErrors({MatchPassword: true});
			  // eslint-disable-next-line @typescript-eslint/naming-convention
              group.get(FormFields.Password2).setErrors({MatchPassword: true});
          } else {
              group.get(FormFields.Password).setErrors(null);
              group.get(FormFields.Password2).setErrors(null);
          }
      }
      return this.pwMatching;
  }

  onSigninClick(): void {
      const myUser = new MyUser();
      myUser.userId = this.signinForm.get(FormFields.Username).value;
      myUser.password = this.signinForm.get(FormFields.Password).value;
      myUser.email = this.signinForm.get(FormFields.Email).value;
//      console.log(this.signinForm);
//      console.log(myUser);
      this.myuserService.postSignin(myUser).subscribe(us => this.signin(us),err => console.log(err));
  }

  onLoginClick(): void {
      const myUser = new MyUser();
      myUser.userId = this.loginForm.get(FormFields.Username).value;
      myUser.password = this.loginForm.get(FormFields.Password).value;
//      console.log(myUser);
      this.myuserService.postLogin(myUser).subscribe(us => this.login(us),err => console.log(err));
  }

  signin(us: MyUser): void {
      this.user = us;
      this.data.loggedIn = !!us?.token;
      if(this.user.userId !== null) {
          this.signinFailed = false;
          this.dialogRef.close(this.data.loggedIn);
      } else {
          this.signinFailed = true;
      }
  }

  login(us: MyUser): void {
      this.user = us;
      this.data.loggedIn = !!us?.token;
      if(this.user.userId !== null) {
          this.loginFailed = false;
          this.tokenService.token = us.token;
          this.tokenService.userId = us.userId;
          this.dialogRef.close(this.data.loggedIn);
      } else {
          this.loginFailed = true;
      }
  }

  onCancelClick(): void {
      this.dialogRef.close();
  }
}
