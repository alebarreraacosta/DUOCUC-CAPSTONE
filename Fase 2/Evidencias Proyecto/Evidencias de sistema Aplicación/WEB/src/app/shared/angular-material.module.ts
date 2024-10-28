import { NgModule } from '@angular/core';
import {MatSelectModule} from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import {MatPaginatorModule} from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';




@NgModule({
  imports: [
    MatSelectModule,
    MatFormFieldModule,
    MatTableModule,
    MatSortModule,
    MatInputModule,
    MatIconModule, 
    MatPaginatorModule,
    MatButtonModule,
    MatDialogModule,
    FormsModule
  ], 
  exports:[
    MatSelectModule,
    MatFormFieldModule,
    MatTableModule,
    MatSortModule,
    MatInputModule,
    MatIconModule,
    MatPaginatorModule,
    MatButtonModule,
    MatDialogModule,
    FormsModule
  ]
})
export class AngularMaterialModuleGD { }
