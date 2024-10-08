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
      idUsuario:[''],
      nombre: ['', [Validators.required, Validators.minLength(2)]],  
      apellidoPaterno: ['', [Validators.required,Validators.minLength(2)]],  
      apellidoMaterno: ['', [Validators.required,Validators.minLength(2)]],  
      correo: ['', [Validators.required, Validators.email]], 
      contrasena: ['', [Validators.required, Validators.minLength(2)]], 
      rol: ['', [Validators.required]], 
    });
  }

}
