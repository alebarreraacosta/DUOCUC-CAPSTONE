import { Component, OnInit } from '@angular/core';
import { MenuItem } from './interfaces/home-interface';
import { Router } from '@angular/router';
import { SessionGlobalService } from 'src/app/services/session-global.service';
import { LoginResponse } from '../../public/login/interfaces/response';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit{

  iconoSeleccionado:string="";

  menuItems: MenuItem[]= [
    {
      icon: 'home',
      descripcion: 'Principal',
      ruta:'/private/home'
    },
    {
      icon: 'assessment',
      descripcion: 'Cuadratura',
      ruta:'/private/home/cuadratura'
    },
    {
      icon: 'person',
      descripcion: 'Usuarios',
      ruta:'/private/home/usuarios'
    },
    {
      icon: 'description',
      descripcion: 'Reportes',
      ruta:'/private/home/reportes'
    },

  ]

  nombreUsuario:string="";

  constructor( 
    private router: Router, 
    private sessionGlobalService:SessionGlobalService
  ){}

  ngOnInit(): void {
    this.iconoSeleccionado = this.menuItems.filter(x => x.ruta.includes(this.router.url))[0].icon;
    let datosUser =  JSON.parse(this.sessionGlobalService.getDataUser()!) as LoginResponse ;
    this.nombreUsuario = datosUser.nombre + ' '+datosUser.apellidoPaterno + ' '+datosUser.apellidoMaterno
    
  }

  goTo(icon:string,ruta:string){
    this.iconoSeleccionado = icon;
    this.router.navigate([ruta]);
  }
  cerrarSesion(){
    this.sessionGlobalService.cleanLocalStorageSessionStorage();
    this.router.navigate(['/']);
  }
}

