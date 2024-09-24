import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { NgxSpinnerModule } from 'ngx-spinner';
import { PageHeaderComponent } from './components/page-header/page-header.component';
import { AngularMaterialModuleGD } from './angular-material.module';



@NgModule({
  declarations: [
    FormLoginComponent,
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
    NgxSpinnerModule,
    PageHeaderComponent,
    AngularMaterialModuleGD

  ]
})
export class SharedModule { }
