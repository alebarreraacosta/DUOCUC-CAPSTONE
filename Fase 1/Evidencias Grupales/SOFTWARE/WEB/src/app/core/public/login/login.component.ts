import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent  implements OnInit  {

  title:string = 'LOGIN - GARDILCIC';
  titulo:string = 'CIG';


  constructor(private spinnerService: SpinnerService,
    private alertService: AlertService
  ) {}

  ngOnInit(): void {
  }
 
  handleFormSubmit(form: FormGroup): void {
    if (form.valid) {
      console.log('Login form submitted:');
      console.log(form.get('username')?.value);
      console.log(form.get('password')?.value);
    
      this.alertService.showInfoAlert(null, '¿Estás seguro en redirigir al home?', 'Sí, Continuar');
    
      /*
          this.modalService.watchConfirmAction().subscribe((confirmed: boolean) => {
        if (confirmed) {
          console.log("Acción confirmada");
          this.spinnerService.showSpinner();


          // Realiza la acción de confirmación
        } else {
          console.log("Acción cancelada");
          // Realiza la acción de cancelación
        }
      });  */
  
    } 
  }


 


}
