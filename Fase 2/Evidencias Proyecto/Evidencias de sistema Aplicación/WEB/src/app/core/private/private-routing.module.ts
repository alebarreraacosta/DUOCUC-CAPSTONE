import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { CuadraturaComponent } from './cuadratura/cuadratura.component';
import { UsuariosComponent } from './usuarios/usuarios.component';
import { ReportesComponent } from './reportes/reportes.component';
import { CommonModule } from '@angular/common';
import { AuthGuard } from '../public/guard/auth.guard';
import { PrincipalComponent } from './principal/principal.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { PipeTransformsModule } from './pipe/pipe-transformers.module';
import { CuadraturaDiferenciasComponent } from './cuadratura/cuadratura-diferencias/cuadratura-diferencias.component';
const routes: Routes = [

  {
    path: 'home',
    component: HomeComponent,
    children: [
      {
        path: 'principal',
        component: PrincipalComponent
      },
      {
        path: 'cuadratura',
        component: CuadraturaComponent
      },
      {
        path: 'cuadratura-detalle',
        component: CuadraturaDiferenciasComponent
      },
      {
        path: 'usuarios',
        component: UsuariosComponent
      },
      {
        path: 'reportes',
        component: ReportesComponent
      },
    ], 
    canMatch:[AuthGuard]
  }
]

@NgModule({
  declarations:[
    HomeComponent,
    UsuariosComponent,
    CuadraturaComponent,
    CuadraturaDiferenciasComponent, 
    ReportesComponent
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    SharedModule,
    PipeTransformsModule
  ],
  exports: [
    RouterModule,
    
  ]
})
export class PrivateRoutingModule { }
