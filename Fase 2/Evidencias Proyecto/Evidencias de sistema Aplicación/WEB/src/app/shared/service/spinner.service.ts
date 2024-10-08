import { Injectable } from '@angular/core';
import { NgxSpinnerService } from 'ngx-spinner';

@Injectable({
  providedIn: 'root'
})
export class SpinnerService {
  private message = '';

  constructor(private spinner: NgxSpinnerService) {}

  showSpinner(text: string = 'Cargando...') {
    this.message = text;
    this.spinner.show();
  }

  hideSpinner() {
    this.spinner.hide();
  }

  getMessage(): string {
    return this.message;
  }
}
