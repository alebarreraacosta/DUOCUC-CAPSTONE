import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DetalleInventarioResponse, InventarioResponse, ListadoInventario } from '../interfaces/request';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CuadraturaService {

  url: string = `${environment.HOST}/Acceso`;
  constructor(private http: HttpClient) { }

  
  getListaInventarios():Observable<InventarioResponse[]>{
    this.url = "http://demo7311560.mockable.io/cargainventarios";
    return this.http.get<InventarioResponse[]>(this.url );
  }

  obtenerListaInventarios():Observable<ListadoInventario>{
    this.url = `${environment.HOST}/Acceso`;
    return this.http.get<ListadoInventario>(`${this.url}/ObtenerListadoInventario` );
  }

  getListaDetalleInventario(codInventario:string){
    this.url = "http://demo7311560.mockable.io/detalleproductoinventario";
    return this.http.get<DetalleInventarioResponse[]>(this.url );
  }
}
