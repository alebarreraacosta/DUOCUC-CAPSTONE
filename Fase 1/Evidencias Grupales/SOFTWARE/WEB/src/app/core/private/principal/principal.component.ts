import {  Component, OnInit } from '@angular/core';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import { PrincipalService } from './services/principal.service';
import { GraficoMesAnnioResponse, GraficoProductoResponse, SelectorMesAnnioResponse, SelectorProductoResponse } from './interfaces/response';
import { Chart,registerables } from 'chart.js';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-principal',
  templateUrl: './principal.component.html',
  styleUrls: ['./principal.component.scss']
})
export class PrincipalComponent implements OnInit {
  constructor(private spinnerService: SpinnerService,
    private alertService: AlertService,
    private principalService : PrincipalService,
  ) {}
  title = 'Dashboard';
  datosSelector:SelectorMesAnnioResponse[]=[];
  selectorProductos:SelectorProductoResponse[]=[];
  loading:boolean = true;
  mensajeSinDatos:boolean=false;
  nombreGraficoMesAnnio:string='mesAnnio';
  nombreGraficoMesBodega:string='mesBodega';
  nombreGraficoPorProducto:string='porProducto';
  graficoMesAnnio:any;
  graficoMesBodega:any;
  graficoPorProducto:any;
  descripcionSeleccionada:string="";
  isHabilitarDescripcion :boolean=false;

  valorSelectorSeleccionadoMesAnnio:string="";
  valorSelectorSeleccionadoProducto:string="";

  ngOnInit(): void {
    Chart.register(...registerables);
    this.spinnerService.showSpinner();

    forkJoin(
      [this.principalService.cargaSelectorMesAnnio(),
       this.principalService.cargaSelectorProductos()
      ]).subscribe(
      {
        next:(result)=>{
          this.spinnerService.hideSpinner();
          if(result && result[0]){
            this.datosSelector = result[0]; 
            this.seleccionarMesAnnio({value:this.datosSelector[0]});
            this.valorSelectorSeleccionadoMesAnnio = this.datosSelector[0].mesAnnio;
          }
          if(result && result[1]){
            this.selectorProductos = result[1];
            this.seleccionarProducto({value:this.selectorProductos[0].codigoProducto});
            this.valorSelectorSeleccionadoProducto = this.selectorProductos[0].codigoProducto;
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


    cargarGrafico(datos:any) {
      const ctx = document.getElementById(this.nombreGraficoMesAnnio) as HTMLCanvasElement;
      if(this.graficoMesAnnio != undefined){
        this.graficoMesAnnio.destroy();
      }
      this.graficoMesAnnio = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: datos.labels,
          datasets: [{
            label: 'Productos',
            data: datos.data,
            backgroundColor: 'rgba(54, 162, 235, 0.8)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true
            }
          },
          plugins: {
            tooltip: {
              callbacks: {
                label: function(context) {
                  const label = 'Descripción';
                  const description = datos.descripcion[context.dataIndex]; 
                  const cantidadDescrip = 'Cantidad';
                  const cantidad = context.raw;
                  return [`${label}:${description}`, `${cantidadDescrip}:${cantidad}`];
                }
              }
            },
            title: {
              display: true,
              text: 'Inventario SAP',
              font: {
                size: 18
              }
            },
            legend: {
              display: true,
              position: 'top'
            }
          }
        }
      });
    }

    cargarGraficoBodega(datos:any) {
      const ctx = document.getElementById(this.nombreGraficoMesBodega) as HTMLCanvasElement;
      if(this.graficoMesBodega != undefined){
        this.graficoMesBodega.destroy();
      }
      this.graficoMesBodega = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: datos.labels,
          datasets: [{
            label: 'Productos',
            data: datos.data,
            backgroundColor: 'rgba(54, 162, 235, 0.8)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true
            }
          },
          plugins: {
            tooltip: {
              callbacks: {
                label: function(context) {
                  const label = 'Descripción';
                  const description = datos.descripcion[context.dataIndex]; 
                  const cantidadDescrip = 'Cantidad';
                  const cantidad = context.raw;
                  return [`${label}:${description}`, `${cantidadDescrip}:${cantidad}`];
                }
              }
            },
            title: {
              display: true,
              text: 'Inventario Bodega',
              font: {
                size: 18
              }
            },
            legend: {
              display: true,
              position: 'top'
            }
          }
        }
      });
    }


    cargarGraficoProducto(datos:any) {
      const ctx = document.getElementById(this.nombreGraficoPorProducto) as HTMLCanvasElement;
      if(this.graficoPorProducto != undefined){
        this.graficoPorProducto.destroy();
      }
      this.graficoPorProducto = new Chart(ctx, {
        type: 'line',
        data: {
          labels: datos.labels,
          datasets: [{
            label: 'Productos',
            data: datos.data,
            backgroundColor: 'rgba(54, 162, 235, 0.8)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true
            }
          },
          plugins: {
            tooltip: {
              callbacks: {
                label: function(context) {
                  const precioUnitarioDescrip = 'Precio unitario';
                  const cantidadDescrip = 'Cantidad';
                  const totalDescrip = 'Total'
                  const total = datos.descripcion[context.dataIndex].total; 
                  const precioUnitario = datos.descripcion[context.dataIndex].precioUnitario; 
                  const cantidad = context.raw;
                  return [`${precioUnitarioDescrip}:${precioUnitario}`, `${cantidadDescrip}:${cantidad}`,`${totalDescrip}:${total}`];
                }
              }
            },
            title: {
              display: true,
              text: 'Inventario Bodega',
              font: {
                size: 18
              }
            },
            legend: {
              display: true,
              position: 'top'
            }
          }
        }
      });
    }

    seleccionarMesAnnio(event:any){
      let datoSeleccion= event.value;
      this.spinnerService.showSpinner("Cargando gráficos...");
      forkJoin([this.principalService.obtenerDatosMesAnnio(datoSeleccion)]).subscribe(
        {
          next:(result)=>{
            this.spinnerService.hideSpinner();
            if(result && result[0]){
              this.mensajeSinDatos= (result[0] == null);
              let data = this.destructuraDataParaGraficoMesAnnio(result[0]);
              this.cargarGrafico(data);
              this.cargarGraficoBodega(data);
            }else{
              this.mensajeSinDatos= !(result && result[0]);
              this.limpiarGraficos(this.nombreGraficoMesAnnio);
              this.limpiarGraficos(this.nombreGraficoMesBodega);
            }
          },
          error:(err)=>{}
        })
    

    }

    seleccionarProducto(event:any){
      let datoSeleccion= event.value;
      this.spinnerService.showSpinner("Cargando gráficos...");
      this.descripcionSeleccionada = this.selectorProductos.find(x=>x.codigoProducto ==datoSeleccion )?.descripcion!;
      this.isHabilitarDescripcion=true;

      this.principalService.obtenerDatosPorProducto( this.descripcionSeleccionada).subscribe(
        {
          next:(result)=>{
            if(result){
              this.spinnerService.hideSpinner();
              let data = this.destructuraDataParaGraficoProducto(result);
              this.cargarGraficoProducto(data);
            }
          },error:()=>{

          }
        }
      )

     
    }
    private destructuraDataParaGraficoMesAnnio(datos:GraficoMesAnnioResponse[]): {'labels':string[],'data':number[], 'descripcion':string[]}{
      let labels = datos.map(x => x.codigoProducto);
      let data = datos.map(x => x.cantidad); 
      let descripción = datos.map(x => x.descripcion); 
      return {"labels": labels,"data":data,"descripcion":descripción}
    }

    private destructuraDataParaGraficoProducto(datos:GraficoProductoResponse[]): {'labels':string[],'data':number[], 'descripcion':{"precioUnitario":number,"total":number}[]}{
      console.log(datos);
      let labels = datos.map(x => x.mes);
      let data = datos.map(x => x.cantidad); 
      let descripción = datos.map(x => {
        return { 
          precioUnitario: x.precioUnitario, 
          total: x.total 
        }
      }); 
      return {"labels": labels,"data":data,"descripcion":descripción}
    }

    private limpiarGraficos(grafico:string){
      const ctx = document.getElementById(grafico) as HTMLCanvasElement;
      ctx.style.display = 'none'; 
    }
    

}
