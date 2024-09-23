import { Component, OnInit } from '@angular/core';
import { MenuItem } from './interfaces/home-interface';
import { Router } from '@angular/router';

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

  constructor( private router: Router){}

  ngOnInit(): void {
    this.iconoSeleccionado = this.menuItems.filter(x => x.ruta.includes(this.router.url))[0].icon;
  }

  goTo(icon:string,ruta:string){
    this.iconoSeleccionado = icon;
    this.router.navigate([ruta]);
  }
}

