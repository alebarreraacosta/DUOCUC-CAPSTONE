import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { DatosProductosGraficosReporte, SelectorMesAnnioResponse } from '../interfaces/response';

@Injectable({
  providedIn: 'root'
})
export class ReportesService {

  url: string = `${environment.HOST}/${environment.apiContextPath}/private`;
  constructor(private http: HttpClient) { }


  cargaSelectorMesAnnio():Observable<SelectorMesAnnioResponse[]>{
    this.url = 'http://demo7311560.mockable.io/cargamesannio'
    return this.http.get<SelectorMesAnnioResponse[]>(this.url);
  }


  cargaDatosProductosInventarios(mesAnnio:string):Observable<DatosProductosGraficosReporte[]>{
    this.url = 'http://demo7311560.mockable.io/productos-inventario'
    return this.http.get<DatosProductosGraficosReporte[]>(this.url);
  }

  cargaDatosProductosSAP(mesAnnio:string):Observable<DatosProductosGraficosReporte[]>{
    this.url = 'http://demo7311560.mockable.io/productos-sap'
    return this.http.get<DatosProductosGraficosReporte[]>(this.url);
  }

  
}
