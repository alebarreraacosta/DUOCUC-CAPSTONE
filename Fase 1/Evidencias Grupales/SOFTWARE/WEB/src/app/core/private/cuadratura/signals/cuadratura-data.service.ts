import { effect, Injectable, signal, WritableSignal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CuadraturaDataService {

  private dataSignal: WritableSignal<any> = signal(null);  // Signal inicialmente nulo

  // Método para establecer nuevos valores al signal
  setData(data: any) {
    this.dataSignal.set(data);  // Cambiamos el valor del signal
  }

  // Método para obtener el valor actual del signal
  getData() {
    return this.dataSignal();  // Retorna el valor actual del signal
  }

  // Método para observar los cambios de datos reactivamente (opcional)
  subscribeToData(callback: (data: any) => void) {
    effect(() => {
      const currentData = this.dataSignal();  // Obtenemos el valor actual del signal
      callback(currentData);  // Ejecutamos la callback con el valor actual
    });// Ejecuta la callback cuando el signal cambie
  }
}
