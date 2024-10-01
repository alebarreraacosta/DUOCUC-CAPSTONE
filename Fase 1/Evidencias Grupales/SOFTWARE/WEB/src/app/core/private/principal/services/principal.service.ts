import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { GraficoMesAnnioResponse, GraficoProductoResponse, SelectorMesAnnioResponse, SelectorProductoResponse } from '../interfaces/response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PrincipalService {

  url: string = `${environment.HOST}/${environment.apiContextPath}/private`;
  constructor(private http: HttpClient) { }


  cargaSelectorMesAnnio():Observable<SelectorMesAnnioResponse[]>{
    this.url = 'http://demo7311560.mockable.io/cargamesannio'
    return this.http.get<SelectorMesAnnioResponse[]>(this.url);
  }

  cargaSelectorProductos():Observable<SelectorProductoResponse[]>{
    this.url = 'http://demo7311560.mockable.io/selectorproductos'
    return this.http.get<SelectorProductoResponse[]>(this.url);
  }

  obtenerDatosMesAnnio(mesAnnioSeleccionado:string):Observable<GraficoMesAnnioResponse[]>{
    this.url = 'http://demo7311560.mockable.io/graficoMesAnnio'
    return this.http.get<GraficoMesAnnioResponse[]>(this.url);

  }

  obtenerDatosPorProducto(productoSeleccion:string):Observable<GraficoProductoResponse[]>{
    this.url = 'http://demo7311560.mockable.io/graficoproductoseleccionado'
    return this.http.get<GraficoProductoResponse[]>(this.url);

  }



  
}
