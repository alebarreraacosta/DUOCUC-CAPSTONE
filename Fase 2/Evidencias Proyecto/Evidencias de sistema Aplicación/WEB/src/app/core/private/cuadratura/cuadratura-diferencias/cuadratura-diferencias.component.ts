import { Component, OnInit, ViewChild } from '@angular/core';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { CuadraturaService } from '../services/cuadratura.service';
import { DetalleInventarioResponse } from '../interfaces/request';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { localePaginator } from '../utils/customFunctions';
import { CuadraturaDataSessionService } from '../services/cuadratura-session.service';
import { MatDialog } from '@angular/material/dialog';
import { CuadrarProductoModalComponent } from 'src/app/shared/components/modals/cuadrar-producto-modal/cuadrar-producto-modal.component';



@Component({
  selector: 'app-cuadratura-diferencias',
  templateUrl: './cuadratura-diferencias.component.html',
  styleUrls: ['./cuadratura-diferencias.component.scss']
})
export class CuadraturaDiferenciasComponent implements OnInit {
  displayedColumns: string[] = ['codigo', 'descripcion', 'unidad-medida', 'stock-sap','stock-bodega','precio-unitario','precio-total', 'acciones'];
 
  title:string="Cuadratura Inventario";

  codigoInventario:string="";
  fecha:string="";
  estado:string="";

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  public dataDetalleIntentarios: DetalleInventarioResponse[] = [];
  public dataSource: MatTableDataSource<DetalleInventarioResponse>;

  constructor(
    private cuadraturaService : CuadraturaService,
    private spinnerService: SpinnerService,
    private alertService: AlertService,
    private cuadraturaSession:CuadraturaDataSessionService,
    private dialogModal:MatDialog
  ){
    this.dataSource = new MatTableDataSource(this.dataDetalleIntentarios);
  }

  ngOnInit(): void {
    this.codigoInventario = JSON.parse(this.cuadraturaSession.getDataInventario()!).codigo;
    this.fecha = JSON.parse(this.cuadraturaSession.getDataInventario()!).fecha;
    this.estado = JSON.parse(this.cuadraturaSession.getDataInventario()!).estado;
    this.spinnerService.showSpinner('Cargando los detalle inventario ' +  this.codigoInventario+' ...');
    this.cuadraturaService.getListaDetalleInventario(this.codigoInventario).subscribe(
      {
        next:(result)=>{
          if(result){
            this.spinnerService.hideSpinner();
            this.dataSource.data = result;
            this.dataSource.filterPredicate = this.createFilter();
            this.actualizarPaginado();
          }
        },
        error:(err)=>{
          this.spinnerService.hideSpinner();
          if(err.status>= 400 && err.status>= 599 ){
            this.alertService.showInfoAlert('Ha ocurrido un error, al cargar detalle inventario',"Error carga de detalle inventario",'Aceptar','error');
          }
        }
      }
    )
  }

  applyFilter(event:any){
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }

  }

  createFilter(): (data: any, filter: string) => boolean {
    return (data, filter): boolean => {
      const searchTerm = filter.toLowerCase();
      // Aplicar filtro solo a las columnas relevantes (excluyendo "Acciones")
      return (
        data.codArticulo.toLowerCase().includes(searchTerm) ||
        data.descripcion.toLowerCase().includes(searchTerm) ||
        data.unidad.toLowerCase().includes(searchTerm) ||
        data.stockSAP.toString().includes(searchTerm) ||
        data.stockBodega.toString().includes(searchTerm) ||
        data.precioUnitario.toString().includes(searchTerm) ||
        data.precioTotal.toString().includes(searchTerm)
      );
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
  
  cuadrar(producto:DetalleInventarioResponse){
    const dialogRef = this.dialogModal.open(CuadrarProductoModalComponent, {
      width: '500px', 
      maxWidth: 'none',
      data: {
        inventario: this.codigoInventario,
        stockSap: producto.stockSAP,
        stockBodega: producto.stockBodega,
      },
      disableClose:true
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Producto cuadrado:', result);
      }
    });
  }
}
