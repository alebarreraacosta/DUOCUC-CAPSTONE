import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CuadraturaDataSessionService {

  constructor() { }

  setDataInventario(datosInventario:any){
    sessionStorage.setItem('dataInventario',JSON.stringify(datosInventario));
  }

  getDataInventario(){
    return sessionStorage.getItem('dataInventario');
  }

  
}
