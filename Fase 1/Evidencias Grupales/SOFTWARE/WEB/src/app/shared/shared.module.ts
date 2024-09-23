import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { ReactiveFormsModule } from '@angular/forms';



@NgModule({
  declarations: [
    FormLoginComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule
  ], 
  exports:[FormLoginComponent]
})
export class SharedModule { }
