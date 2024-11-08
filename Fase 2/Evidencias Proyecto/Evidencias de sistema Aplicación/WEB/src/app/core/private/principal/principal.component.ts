import {  Component, OnInit } from '@angular/core';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import { PrincipalService } from './services/principal.service';
import { DatoGrafico, DatoGraficoProducto, GraficoMesAnnioResponse, GraficoProductoResponse, SelectorMesAnnioInventarioResponse, SelectorMesAnnioResponse, SelectorProductoPorMesResponse, SelectorProductoResponse } from './interfaces/response';
import { Chart,registerables } from 'chart.js';
import { firstValueFrom, forkJoin } from 'rxjs';
import { transformValoresAPesos } from 'src/app/utils/utils-functions';

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
  datosSelector:SelectorMesAnnioInventarioResponse[]=[];
  selectorProductos:SelectorProductoPorMesResponse[]=[];
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

  async ngOnInit(){
    Chart.register(...registerables);
    this.spinnerService.showSpinner();

    try
    {
      let datosSelectorMesAnnio = await firstValueFrom(this.principalService.cargaSelectorMesAnnioPrincipal());
      if(datosSelectorMesAnnio){
        this.datosSelector = datosSelectorMesAnnio.mesesInventarios; 
        this.valorSelectorSeleccionadoMesAnnio = this.datosSelector[0].MesAnnio;
        let selectorCodigoArticulo = await firstValueFrom( this.principalService.cargaSelectorProductosPorMes(this.datosSelector[0].Mes,this.datosSelector[0].Annio));
        this.seleccionarMesAnnioGraficos(this.datosSelector[0].Mes,this.datosSelector[0].Annio);

        this.selectorProductos = selectorCodigoArticulo.articulos;
        this.valorSelectorSeleccionadoProducto = this.selectorProductos[0].CodigoProducto;
        this.seleccionarProductoPorID(this.selectorProductos[0].CodigoProducto);
        this.descripcionSeleccionada = this.selectorProductos[0].Descripcion!;
        this.isHabilitarDescripcion=true;
      }
      this.spinnerService.hideSpinner();
    }catch(err){
      this.spinnerService.hideSpinner();
      this.alertService.showInfoAlert(null, 'Error al cargar selectores', 'Aceptar', 'error');
    }
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
          maintainAspectRatio: false, 
          scales: {
            y: {
              title: {
                display: true,
                text: 'CANTIDADES'
              }
            },
            x: {
              title: {
                display: true,
                text: 'ID PRODUCTOS'
              }
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
          }],
        },
        options: {
          maintainAspectRatio: false, 
          scales: {
            y: {
              title: {
                display: true,
                text: 'CANTIDADES'
              }
            },
            x: {
              title: {
                display: true,
                text: 'ID PRODUCTOS'
              }
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
                  return [`${label}: ${description}`, `${cantidadDescrip}: ${cantidad}`];
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
          maintainAspectRatio: false, 
          scales: {
            y: {
              title: {
                display: true,
                text: 'CANTIDADES'
              }
            },
            x: {
              title: {
                display: true,
                text: 'MESES'
              }
            }
          },
          plugins: {
            tooltip: {
              callbacks: {
                label: function(context) {
                  const precioUnitarioDescrip = 'Precio unitario';
                  const cantidadDescrip = 'Cantidad';
                  const totalDescrip = 'Total'
                  const total = transformValoresAPesos(datos.descripcion[context.dataIndex].total); 
                  const precioUnitario = transformValoresAPesos(datos.descripcion[context.dataIndex].precioUnitario); 
                  const cantidad = context.raw;
                  return [`${precioUnitarioDescrip}: ${precioUnitario}`, `${cantidadDescrip}: ${cantidad}`,`${totalDescrip}: ${total}`];
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

    async seleccionarMesAnnioGraficos(mes:string, annio:string){
      this.spinnerService.showSpinner("Cargando gráficos...");
      try{
        let datosGraficos = await firstValueFrom(this.principalService.obtenerDatosMesAnnioGraficosSapFisico(mes,annio));
      
        if(datosGraficos){
          let filtroDatosSap = datosGraficos.datosGrafico.DatosSap.slice(0,10)
          let dataSap = this.destructuraDataParaGraficoMesAnnioSAPFISICO(filtroDatosSap);
          let filtroDatosFisico = datosGraficos.datosGrafico.DatosFisico.slice(0,10)
          let dataFisica = this.destructuraDataParaGraficoMesAnnioSAPFISICO(filtroDatosFisico);
          this.cargarGrafico(dataSap);
          this.cargarGraficoBodega(dataFisica);
        }else{
          this.mensajeSinDatos= false;
          this.limpiarGraficos(this.nombreGraficoMesAnnio);
          this.limpiarGraficos(this.nombreGraficoMesBodega);
        }
        this.spinnerService.hideSpinner();
      }catch(err){
        this.spinnerService.hideSpinner();
        this.alertService.showInfoAlert(null, 'Error al cargar graficos SAP Y FISICO', 'Aceptar', 'error');
      }
    }

    async seleccionarMesAnnio(event:any){
      let datoSeleccion= event.value;
      let datosMesAnnio = this.datosSelector.find(item=>item.MesAnnio == datoSeleccion);
      this.seleccionarMesAnnioGraficos(datosMesAnnio?.Mes!,datosMesAnnio?.Annio!);

      try {
        this.isHabilitarDescripcion=false
        let selectorCodigoArticulo = await firstValueFrom( this.principalService.cargaSelectorProductosPorMes(datosMesAnnio?.Mes!,datosMesAnnio?.Annio!));
        this.selectorProductos = selectorCodigoArticulo.articulos;
        this.valorSelectorSeleccionadoProducto = this.selectorProductos[0].CodigoProducto;
        this.seleccionarProductoPorID(this.selectorProductos[0].CodigoProducto);
        this.descripcionSeleccionada = this.selectorProductos[0].Descripcion!;
        this.isHabilitarDescripcion=true;
      } catch (error) {
        this.alertService.showInfoAlert(null, 'Error al cargar codigos de articulos', 'Aceptar', 'error');
      }

    }


    async seleccionarProductoPorID(idProducto:string){
      this.spinnerService.showSpinner("Cargando gráficos...");
      try{
        let datosGraficoProductoSeleccionado = await firstValueFrom(this.principalService.obtenerDatosPorProductoPorMesGrafico(idProducto));

        if(datosGraficoProductoSeleccionado){
          let data = this.destructuraDataParaGraficoProductoSeleccionado(datosGraficoProductoSeleccionado.datosGraficoProducto);
          this.cargarGraficoProducto(data);
          this.spinnerService.hideSpinner();
        }
      }catch(error){
        this.spinnerService.hideSpinner();
        this.alertService.showInfoAlert(null, 'Error al cargar grafico Producto seleccionado', 'Aceptar', 'error');
      }

     
    }

    seleccionarProducto(event:any){
      let datoSeleccion= event.value;
      let datosProductoSeleccionado =  this.selectorProductos.find(item=> item.CodigoProducto == datoSeleccion);
      this.descripcionSeleccionada = datosProductoSeleccionado?.Descripcion!;
      this.seleccionarProductoPorID(datosProductoSeleccionado?.CodigoProducto!);
    }
 
    private destructuraDataParaGraficoMesAnnioSAPFISICO(datos:DatoGrafico[]): {'labels':string[],'data':number[], 'descripcion':string[]}{
      let labels = datos.map(x => x.CodigoProducto);
      let data = datos.map(x => x.Cantidad); 
      let descripción = datos.map(x => x.Descripcion); 
      return {"labels": labels,"data":data,"descripcion":descripción}
    }

    private destructuraDataParaGraficoProductoSeleccionado(datos:DatoGraficoProducto[]): {'labels':string[],'data':number[], 'descripcion':{"precioUnitario":number,"total":number}[]}{
      let labels = datos.map(x => x.Mes);
      let data = datos.map(x => x.Cantidad); 
      let descripción = datos.map(x => {
        return { 
          precioUnitario: x.PrecioUnitario, 
          total: x.Total 
        }
      }); 
      return {"labels": labels,"data":data,"descripcion":descripción}
    }

    private limpiarGraficos(grafico:string){
      const ctx = document.getElementById(grafico) as HTMLCanvasElement;
      ctx.style.display = 'none'; 
    }
    

}
