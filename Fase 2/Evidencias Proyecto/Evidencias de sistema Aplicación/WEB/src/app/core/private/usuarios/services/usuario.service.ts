import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { UsuarioActualizarRequest, UsuarioRequest } from '../interfaces/request';
import { UsuariosListaResponse, UsuariosResponse } from '../interfaces/response';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  url: string = `${environment.HOST}`;
  //url: string = `/api/`;
  constructor(private http: HttpClient) { }

  postCrearUsuario(data:UsuarioRequest){
    //TODO: RECORDAR `Acceso/CrearUsuario`
    this.url = 'http://demo7311560.mockable.io/login'
    return this.http.post('/api/Acceso/CrearUsuario',data);
  }


  updateUsuario(data:UsuarioActualizarRequest){
    return this.http.post('/api/Acceso/ActualizarUsuario',data);
  }

  deleteUsuario(idUsuarioAEliminar:number){
    return this.http.delete(`/api/Acceso/EliminarUsuario?idUsuario=${idUsuarioAEliminar}`);
  }

  getListaUsuarios():Observable<UsuariosResponse>{
    return this.http.get<UsuariosResponse>(this.url + 'Acceso/ListarUsuarios');
  }
}