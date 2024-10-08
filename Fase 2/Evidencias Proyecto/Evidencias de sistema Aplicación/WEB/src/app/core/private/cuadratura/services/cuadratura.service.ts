import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DetalleInventarioResponse, InventarioResponse } from '../interfaces/request';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CuadraturaService {

  url: string = `${environment.HOST}`;
  constructor(private http: HttpClient) { }

  
  getListaInventarios():Observable<InventarioResponse[]>{
    this.url = "http://demo7311560.mockable.io/cargainventarios";
    return this.http.get<InventarioResponse[]>(this.url );
  }

  getListaDetalleInventario(codInventario:string){
    this.url = "http://demo7311560.mockable.io/detalleproductoinventario";
    return this.http.get<DetalleInventarioResponse[]>(this.url );
  }
}
