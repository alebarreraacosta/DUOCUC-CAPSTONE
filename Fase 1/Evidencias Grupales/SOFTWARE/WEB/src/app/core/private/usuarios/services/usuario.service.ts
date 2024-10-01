import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { UsuarioRequest } from '../interfaces/request';

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


  updateUsuario(data:UsuarioRequest){
    return this.http.put('/api/Acceso/ActualizarUsuario',data);
  }

  deleteUsuario(idUsuarioAEliminar:number){
    return this.http.delete(`/api/Acceso/EliminarUsuario?idUsuario=${idUsuarioAEliminar}`);
  }
}