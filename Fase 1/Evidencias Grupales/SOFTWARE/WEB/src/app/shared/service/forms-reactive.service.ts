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
      password: ['', [Validators.required, Validators.minLength(3)]], 
    });
  }

  crearFormularioUsuario(): FormGroup {
    return this.fb.group({
      nombre: ['', [Validators.required]],  
      apellidoPaterno: ['', [Validators.required]],  
      apellidoMaterno: ['', [Validators.required]],  
      correo: ['', [Validators.required, Validators.email]], 
      contrasena: ['', [Validators.required, Validators.minLength(3)]], 
      rol: ['', [Validators.required]], 
    });
  }

}
