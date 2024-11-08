import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ArticulosPorMes,  DatosGraficosPorMes, GraficoMesAnnioResponse, GraficoProductoResponse, GraficoProductoSeleccionado, MesesInventarios, SelectorMesAnnioResponse, SelectorProductoResponse } from '../interfaces/response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PrincipalService {

  url: string = `${environment.HOST}/Acceso`;
  constructor(private http: HttpClient) { }


  cargaSelectorMesAnnio():Observable<SelectorMesAnnioResponse[]>{
    this.url = 'http://demo7311560.mockable.io/cargamesannio'
    return this.http.get<SelectorMesAnnioResponse[]>(this.url);
  }

  cargaSelectorMesAnnioPrincipal():Observable<MesesInventarios>{
    return this.http.get<MesesInventarios>(this.url+'/ObtenerMesesInventarios');
  }

  cargaSelectorProductos():Observable<SelectorProductoResponse[]>{
    this.url = 'http://demo7311560.mockable.io/selectorproductos'
    return this.http.get<SelectorProductoResponse[]>(this.url);
  }


  cargaSelectorProductosPorMes(mes:string, annio : string):Observable<ArticulosPorMes>{
    return this.http.get<ArticulosPorMes>(`${this.url}/ObtenerArticulosPorMes?Mes=${mes}&Annio=${annio}`);
  }

  obtenerDatosMesAnnio(mesAnnioSeleccionado:string):Observable<GraficoMesAnnioResponse[]>{
    this.url = 'http://demo7311560.mockable.io/graficoMesAnnio'
    return this.http.get<GraficoMesAnnioResponse[]>(this.url);

  }

  obtenerDatosMesAnnioGraficosSapFisico(mes:string, annio : string):Observable<DatosGraficosPorMes>{
    return this.http.get<DatosGraficosPorMes>(`${this.url}/ObtenerDatosGraficoPorMesAnnio?Mes=${mes}&Annio=${annio}`);

  }

  obtenerDatosPorProducto(productoSeleccion:string):Observable<GraficoProductoResponse[]>{
    this.url = 'http://demo7311560.mockable.io/graficoproductoseleccionado'
    return this.http.get<GraficoProductoResponse[]>(this.url);

  }

  obtenerDatosPorProductoPorMesGrafico(productoSeleccion:string):Observable<GraficoProductoSeleccionado>{
    return this.http.get<GraficoProductoSeleccionado>(`${this.url}/ObtenerDatosGraficoProductoSeleccionado?idProducto=${productoSeleccion}`);

  }



  
}
