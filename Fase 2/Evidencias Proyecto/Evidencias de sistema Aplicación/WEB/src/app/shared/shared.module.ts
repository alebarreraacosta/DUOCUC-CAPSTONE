import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { NgxSpinnerModule } from 'ngx-spinner';
import { PageHeaderComponent } from './components/page-header/page-header.component';
import { AngularMaterialModuleGD } from './angular-material.module';
import { FormUsuarioComponent } from './components/form-usuario/form-usuario.component';
import { PdfGeneratorComponent } from './components/PDF/pdf-grafic-generate/pdf-grafic-generate.component';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { CuadrarProductoModalComponent } from './components/modals/cuadrar-producto-modal/cuadrar-producto-modal.component';



@NgModule({
  declarations: [
    FormLoginComponent,
    FormUsuarioComponent,
    SpinnerComponent,
    PageHeaderComponent,
    PdfGeneratorComponent,
    CuadrarProductoModalComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgxSpinnerModule,
    AngularMaterialModuleGD,
    NgxExtendedPdfViewerModule
  ], 
  exports:[
    SpinnerComponent,
    FormLoginComponent,
    FormUsuarioComponent,
    NgxSpinnerModule,
    PageHeaderComponent,
    AngularMaterialModuleGD,
    PdfGeneratorComponent

  ]
})
export class SharedModule { }
