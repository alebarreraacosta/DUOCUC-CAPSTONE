import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import * as CryptoJS from 'crypto-js';
import { LoginService } from './services/login.service';
import { LoginResponse } from './interfaces/response';
import { LoginRequest } from './interfaces/request';
import { Router } from '@angular/router';
import { SessionGlobalService } from 'src/app/services/session-global.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent  implements OnInit  {

  title:string = 'LOGIN - GARDILCIC';
  titulo:string = 'CIG';

  constructor(private spinnerService: SpinnerService,
    private alertService: AlertService,
    private loginService : LoginService,
    private router : Router,
    private sessionGlobal:SessionGlobalService
  ) {}

  ngOnInit(): void {

  }
 
  handleFormSubmit(form: FormGroup): void {
      this.spinnerService.showSpinner();
      this.loginService.login({
        username: form.get('username')?.value,
        password: this.encriptarContrasenna(form.get('password')?.value)
      } as LoginRequest).subscribe(
        {
          next:(result)=>{
            if(result){
              this.sessionGlobal.setDataUser(result);
              this.router.navigate(['private/home']);
              this.spinnerService.hideSpinner();
            }
          },
          error:(err)=>{
            this.spinnerService.hideSpinner();
            if(err.status == 401){
              this.alertService.showInfoAlert(null, 'Credenciales inválidas', 'Aceptar', 'error');
            }
            if(err.status >=499 && err.status <=599){
              console.error(err);
              this.alertService.showInfoAlert(null, 'Ha ocurrido un error, llamar a soporte técnico', 'Aceptar', 'error');
            }
            

          }
        }
      );
  }

  encriptarContrasenna(conversion: string): string {
    const keys = 'api-control-stoc';
    const iv = CryptoJS.lib.WordArray.random(16);
    const key = CryptoJS.enc.Utf8.parse(keys);
    const passwordEncryptOutput = CryptoJS.AES.encrypt(conversion, key, {
      keySize: 128 / 8,
      iv: iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7,
    }).toString();
    return iv.toString() + ':' + passwordEncryptOutput;
  }


 


}
