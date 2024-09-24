import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { SelectorMesAnnioResponse } from '../interfaces/response';
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
}
