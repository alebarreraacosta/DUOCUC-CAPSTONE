import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-cuadrar-producto-modal',
  templateUrl: './cuadrar-producto-modal.component.html',
  styleUrls: ['./cuadrar-producto-modal.component.scss']
})
export class CuadrarProductoModalComponent {
  cantidad:number = 0;
  descripcionEvidencia = '';
  archivoAdjunto: File | null = null;

  constructor(
    public dialogRef: MatDialogRef<CuadrarProductoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  onFileSelected(event: any): void {
    this.archivoAdjunto = event.target.files[0];
  }

  onConfirmar(): void {
    this.dialogRef.close({
      cantidad: this.cantidad,
      descripcionEvidencia: this.descripcionEvidencia,
      archivoAdjunto: this.archivoAdjunto,
    });
  }

  onCancelar(): void {
    this.dialogRef.close();
  }


  increment() {
    this.cantidad++;
  }

  decrement() {
    if (this.cantidad > 0) {
      this.cantidad--;
    }
  }
}
