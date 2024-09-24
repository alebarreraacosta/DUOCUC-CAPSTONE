import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatSelectModule} from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';



@NgModule({
  declarations: [
   
  ],
  imports: [
    MatSelectModule,
    MatFormFieldModule,
  ], 
  exports:[
    MatSelectModule,
    MatFormFieldModule,
  ]
})
export class AngularMaterialModuleGD { }
