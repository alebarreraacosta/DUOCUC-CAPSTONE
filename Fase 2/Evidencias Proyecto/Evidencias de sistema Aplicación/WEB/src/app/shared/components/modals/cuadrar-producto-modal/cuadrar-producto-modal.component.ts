import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CuadraturaService } from 'src/app/core/private/cuadratura/services/cuadratura.service';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';

@Component({
  selector: 'app-cuadrar-producto-modal',
  templateUrl: './cuadrar-producto-modal.component.html',
  styleUrls: ['./cuadrar-producto-modal.component.scss']
})
export class CuadrarProductoModalComponent implements OnInit {
  cantidad:number = 0;
  descripcionEvidencia = '';
  archivoAdjunto: File | null = null;


  constructor(
    public dialogRef: MatDialogRef<CuadrarProductoModalComponent>,
    private cuadraturaService:CuadraturaService,
    private spinnerService: SpinnerService,
    private alertService: AlertService,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}



  ngOnInit(): void {
    console.log(this.data);
    
    if(this.data?.descripcionEvidencia){
      this.descripcionEvidencia= this.data?.descripcionEvidencia;
    }

    if(this.data?.cantidadCuadrada){
      this.cantidad= this.data?.cantidadCuadrada;
    }
  }

  onFileSelected(event: any): void {
    this.archivoAdjunto = event.target.files[0];
  }

  download(){
    if (!this.data?.pdfBase64) {
      console.error('No se ha proporcionado una ruta válida para el archivo.');
      return;
    }
    const link = document.createElement('a');
    link.href = this.data.pdfBase64; 
    link.download = 'valeConsumo.pdf'; 
    link.style.display = 'none';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  onConfirmar(): void {
    const formData = new FormData();
    formData.append('codigoInventario', this.data.inventario || '');
    formData.append('codigoProducto', this.data.codProducto || '');
    formData.append('cantidadACuadrar', this.cantidad.toString());
    formData.append('descripcion', this.descripcionEvidencia);
    formData.append('archivo', this.archivoAdjunto!);
    this.spinnerService.showSpinner();
    this.cuadraturaService.cuadrarProducto(formData).subscribe(
      {
        next:(result)=>{
          this.spinnerService.hideSpinner();
          this.alertService.showConfirmationEventoAlert(null, 'Producto cuadrado exitosamente', 'Aceptar', 'success').then(result=>{
            this.dialogRef.close({recargar:true});
          });
        }
        ,error:(err)=>{
          this.spinnerService.hideSpinner();
          this.alertService.showInfoAlert(null, 'Error al cargar selectores', 'Aceptar', 'error');
          this.dialogRef.close({});
        }  
    })
  }

  onCancelar(): void {
    this.dialogRef.close();
  }


  increment() {
    this.cantidad++;
  }

  decrement() {
  
    this.cantidad--;
    
  }
}
