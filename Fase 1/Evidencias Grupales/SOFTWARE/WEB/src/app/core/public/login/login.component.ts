import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent  implements OnInit  {

  title:string = 'LOGIN - GARDILCIC';
  titulo:string = 'CIG';


  constructor() {}

  ngOnInit(): void {
    console.log('ngOnInit en LoginComponent');
  }
 
  handleFormSubmit(form: FormGroup): void {
    if (form.valid) {
      console.log('Login form submitted:');
      console.log(form.get('username')?.value);
      console.log(form.get('password')?.value);
      // Aquí puedes agregar la lógica para enviar los datos a un servidor o API
    } else {
      console.log('Login form is invalid');
    }
  }


 


}
