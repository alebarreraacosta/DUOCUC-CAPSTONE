import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { NgxSpinnerModule } from 'ngx-spinner';
import { PageHeaderComponent } from './components/page-header/page-header.component';



@NgModule({
  declarations: [
    FormLoginComponent,
    SpinnerComponent,
    PageHeaderComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgxSpinnerModule
  ], 
  exports:[
    SpinnerComponent,
    FormLoginComponent,
    NgxSpinnerModule,
    PageHeaderComponent

  ]
})
export class SharedModule { }
