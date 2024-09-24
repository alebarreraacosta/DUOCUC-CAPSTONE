import { Injectable } from '@angular/core';
import { LoginResponse } from '../core/public/login/interfaces/response';

@Injectable({
  providedIn: 'root'
})
export class SessionGlobalService {


  constructor() { }

  setDataUser(data:LoginResponse){
    sessionStorage.setItem('userData',JSON.stringify(data)!);
  }

  getDataUser(){
    return sessionStorage.getItem('userData');
  }

  cleanLocalStorageSessionStorage(){
    localStorage.clear();
    sessionStorage.clear();
  }
  
}
