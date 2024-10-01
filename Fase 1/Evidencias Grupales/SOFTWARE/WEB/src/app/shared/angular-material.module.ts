import { NgModule } from '@angular/core';
import {MatSelectModule} from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatSortModule } from '@angular/material/sort';




@NgModule({
  imports: [
    MatSelectModule,
    MatFormFieldModule,
    MatTableModule,
    MatSortModule
  ], 
  exports:[
    MatSelectModule,
    MatFormFieldModule,
    MatTableModule,
    MatInputModule,
    MatSortModule
  ]
})
export class AngularMaterialModuleGD { }
