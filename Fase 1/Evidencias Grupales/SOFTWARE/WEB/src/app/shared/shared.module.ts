import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { NgxSpinnerModule } from 'ngx-spinner';
import { PageHeaderComponent } from './components/page-header/page-header.component';
import { AngularMaterialModuleGD } from './angular-material.module';
import { FormUsuarioComponent } from './components/form-usuario/form-usuario.component';



@NgModule({
  declarations: [
    FormLoginComponent,
    FormUsuarioComponent,
    SpinnerComponent,
    PageHeaderComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgxSpinnerModule,
    AngularMaterialModuleGD
  ], 
  exports:[
    SpinnerComponent,
    FormLoginComponent,
    FormUsuarioComponent,
    NgxSpinnerModule,
    PageHeaderComponent,
    AngularMaterialModuleGD

  ]
})
export class SharedModule { }
