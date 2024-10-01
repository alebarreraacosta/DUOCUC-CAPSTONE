// src/app/service/alert.service.ts
import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  constructor() {}

  // Método para mostrar alertas informativas
  showInfoAlert(content: string | null, title: string = 'Información', confirmButtonText: string = 'Cerrar',icon:string): void {
    if(icon == 'error'){
      Swal.fire({
        title: title,
        html: content == null ? '':content ,
        icon: 'error',
        confirmButtonText: confirmButtonText,
        backdrop:true,
        allowOutsideClick: false, 
        allowEscapeKey: false, 
      });
    }else{
      Swal.fire({
        title: title,
        html: content == null ? '':content ,
        icon: 'info',
        confirmButtonText: confirmButtonText,
        backdrop:true,
        allowOutsideClick: false, 
        allowEscapeKey: false, 
      });
    }
    
  }

  // Método para mostrar alertas de confirmación
  showConfirmationAlert(content: string| null, title: string = 'Confirmar acción', confirmButtonText: string = 'Confirmar', cancelButtonText: string = 'Cancelar'): Promise<boolean> {
    return Swal.fire({
      title: title,
      html: content == null ? '':content  ,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: confirmButtonText,
      cancelButtonText: cancelButtonText,
      backdrop:false
    }).then((result) => {
      return result.isConfirmed;  // Retorna true si se confirmó, false si se canceló
    });
  }
}
