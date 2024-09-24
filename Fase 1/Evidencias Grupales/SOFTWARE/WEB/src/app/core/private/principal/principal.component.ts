import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import { PrincipalService } from './services/principal.service';
import { SelectorMesAnnioResponse } from './interfaces/response';

@Component({
  selector: 'app-principal',
  templateUrl: './principal.component.html',
  styleUrls: ['./principal.component.scss']
})
export class PrincipalComponent implements OnInit {
  constructor(private spinnerService: SpinnerService,
    private alertService: AlertService,
    private principalService : PrincipalService,
    private router : Router,
  ) {}

  title = 'Dashboard'
  datosSelector:SelectorMesAnnioResponse[]=[];

  ngOnInit(): void {
    this.spinnerService.showSpinner();
    this.principalService.cargaSelectorMesAnnio().subscribe(
      {
        next:(result)=>{
          this.spinnerService.hideSpinner();
          if(result){
            this.datosSelector = result;
          }
        },
        error:()=>{}
      })
  }



}
