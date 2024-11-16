import { Component, OnInit } from '@angular/core';
import { DatosProductosGraficosReporte, SelectorMesAnnioResponse } from './interfaces/response';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import { ReportesService } from './services/reportes.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-reportes',
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.scss']
})
export class ReportesComponent implements OnInit{
  title:string="REPORTES CIG";
  valorSelectorSeleccionadoMesAnnio:string="";
  datosSelector:SelectorMesAnnioResponse[]=[];
  isVisible:boolean=false;
  inventarioBodega:DatosProductosGraficosReporte[]=[];
  inventarioSap:DatosProductosGraficosReporte[]=[];
  inventarioCuadrados:DatosProductosGraficosReporte[]=[];
  mesAnnio:string="";

  constructor(private spinnerService: SpinnerService,
    private alertService: AlertService,
    private  reportesService : ReportesService,
  ) {}
  ngOnInit(): void {
    
    this.spinnerService.showSpinner();

    forkJoin(
      [
        this.reportesService.cargaSelectorMesAnnio(),
      ]).subscribe(
      {
        next:(result)=>{
          this.spinnerService.hideSpinner();
          if(result && result[0]){
            this.datosSelector = result[0]; 
            this.valorSelectorSeleccionadoMesAnnio = this.datosSelector[0].mesAnnio;
          }
        },
        error:(err)=>{
          this.spinnerService.hideSpinner();
          if(err.status >=499 && err.status <=599){
            this.alertService.showInfoAlert(null, 'Error al cargar selectores', 'Aceptar', 'error');
          }
        }
      }
    )
  }

  
  seleccionarMesAnnio(event:any){
    let datoSeleccion= event.value;
    this.valorSelectorSeleccionadoMesAnnio=datoSeleccion;
    this.isVisible=false;
  }

  generarReporte(){
    this.spinnerService.showSpinner("Generando reporte...");
    this.isVisible=false;
    forkJoin([
      this.reportesService.cargaDatosProductosInventarios( this.valorSelectorSeleccionadoMesAnnio),
      this.reportesService.cargaDatosProductosSAP( this.valorSelectorSeleccionadoMesAnnio),
      this.reportesService.cargaDatosProductosCuadrados( this.valorSelectorSeleccionadoMesAnnio),
    ]).subscribe({
      next:(result)=>{
        if((result && result[0]) && result && result[1] &&  result && result[2]){
          this.inventarioBodega = result[0];
          this.inventarioSap = result[1];
          this.inventarioCuadrados= result[2];
          this.mesAnnio = this.valorSelectorSeleccionadoMesAnnio
          this.spinnerService.hideSpinner();
          this.isVisible=true;
        }else{
          this.alertService.showInfoAlert(null, 'Error al generar reporte', 'Aceptar', 'error');
        }
      },error:(err)=>{
        this.spinnerService.hideSpinner();
        if(err.status >=499 && err.status <=599){
          this.alertService.showInfoAlert(null, 'Error al generar reporte', 'Aceptar', 'error');
        }
      }
    })



  }

}
