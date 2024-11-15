import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DetalleInventario, DetalleInventarioResponse, InventarioResponse, ListadoInventario, VerProductoCuadrado } from '../interfaces/request';
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

  getListaDetalleInventario(codInventario:string):Observable<DetalleInventario>{
    this.url =  `${environment.HOST}Acceso`;
    return this.http.get<DetalleInventario>(`${this.url}/ObtenerCuadraturaDiferenciasCompleta?codigoInventario=${codInventario}` );
  }

  verProductoCuadrado(idInventario:string, idProducto:string):Observable<VerProductoCuadrado>{
    this.url =  `${environment.HOST}Acceso`;
    return this.http.get<VerProductoCuadrado>(`${this.url}/VerProductoCuadrado?codigoInventario=${idInventario}&codigoProducto=${idProducto}` );
  }


  cuadrarProducto(formData:FormData):Observable<VerProductoCuadrado>{
    this.url =  `${environment.HOST}Acceso`;
    return this.http.post<VerProductoCuadrado>(`${this.url}/CuadrarProducto`,formData );
  }

  downloadFile(fileUrl: string) {
    return this.http.get(fileUrl, { responseType: 'blob' }); // Solicita el archivo como un Blob
  }
}
