import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormBuilder, FormControl, Validators } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class FormsReactiveService {

  constructor(public fb: FormBuilder) {}

  crearFormularioLogin(): FormGroup {
    return this.fb.group({
      username: ['', [Validators.required, Validators.email]],  
      password: ['', [Validators.required, Validators.minLength(6)]], 
    });
  }
}
