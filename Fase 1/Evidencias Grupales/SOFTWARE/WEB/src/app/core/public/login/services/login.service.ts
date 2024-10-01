import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {environment} from 'src/environments/environment'
import { LoginRequest} from '../interfaces/request';
import { LoginResponse} from '../interfaces/response';
import { Observable, of } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class LoginService {

  url: string = `${environment.HOST}`;
  private USERDATA = 'userData';
  constructor(private http: HttpClient) { }

  login(data:LoginRequest){
    //TODO: RECORDAR AGREGAR Acceso/ObtenerUsuario?correo=${data.username}&contrasena=${data.password}
    //this.url = 'http://demo7311560.mockable.io/login'
    return this.http.get<LoginResponse>(this.url+`Acceso/ObtenerUsuario?correo=${data.username}&contrasena=${data.password}`);
  }

  isAuthorized(): Observable<boolean> {
    if (!sessionStorage.getItem(this.USERDATA)) {
      return of(false);
    }
    return of(true);
  }
}
