import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { InventarioResponse } from './interfaces/request';
import { CuadraturaService } from './services/cuadratura.service';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import { CuadraturaDataService } from './signals/cuadratura-data.service';
import { MatPaginator } from '@angular/material/paginator';
import { localePaginator } from './utils/customFunctions';
import { CuadraturaDataSessionService } from './services/cuadratura-session.service';

export interface InvoiceData {
  codigo: string;
  fecha: Date;
  estado: string;
}


@Component({
  selector: 'app-cuadratura',
  templateUrl: './cuadratura.component.html',
  styleUrls: ['./cuadratura.component.scss']
})
export class CuadraturaComponent implements OnInit{
  title:string= "Cuadratura";

  displayedColumns: string[] = ['codigo', 'fecha', 'estado', 'acciones'];
  filterValue: string = '';
  @ViewChild(MatPaginator) paginator!: MatPaginator;


  public dataIntentarios: InventarioResponse[] = [];
  public dataSource: MatTableDataSource<InventarioResponse>;

  constructor(
    private router:Router, 
    private cuadraturaService : CuadraturaService,
    private spinnerService: SpinnerService,
    private alertService: AlertService,
    private  cuadraturaDataService:CuadraturaDataService,
    private cuadraturaSessionService:CuadraturaDataSessionService
  ){

    this.dataSource = new MatTableDataSource(this.dataIntentarios);
  }
  
  ngOnInit(): void {
    this.spinnerService.showSpinner('Cargando los inventarios...');
    this.cuadraturaService.getListaInventarios().subscribe({
      next:(result)=>{
        if(result){
          this.spinnerService.hideSpinner();
          this.dataSource.data = result;
          this.actualizarPaginado();
        }
      },error:(err)=>{
        this.spinnerService.hideSpinner();
        if(err.status>= 400 && err.status>= 599 ){
          this.alertService.showInfoAlert('Ha ocurrido un error, al cargar lista de inventarios',"Error carga de inventarios",'Aceptar','error');
        }
      }
    })
  }

  goToDetalle(datos:any){
    this.cuadraturaSessionService.setDataInventario({
      codigo:datos.codigo,
      fecha:datos.fecha,
      estado: datos.estado
    });
    this.router.navigate(['private/home/cuadratura-detalle']);
  }

  applyFilter(filterValue: any) {
    this.dataSource.filter = filterValue.target.value.trim().toLowerCase(); 

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }


    this.dataSource.filterPredicate = (data, filter) => {
      const dataStr = data.codigo.toLowerCase() + data.fecha.toLowerCase() + data.estado.toLowerCase();
      return dataStr.includes(filter);
    };
  }

  private actualizarPaginado(){
    if (!this.dataSource.paginator) {
      this.dataSource.paginator = this.paginator;
    }
    setTimeout(() => { this.dataSource.paginator = this.paginator;
      localePaginator(this.paginator);
    }, 0);
    if (this.paginator) {
      this.paginator.firstPage();
    }

  }
}
