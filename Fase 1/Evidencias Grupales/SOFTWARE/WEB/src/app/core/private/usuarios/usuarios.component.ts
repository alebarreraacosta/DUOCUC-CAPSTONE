import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import { UsuarioService } from './services/usuario.service';
import { UsuarioRequest } from './interfaces/request';
import { UsuariosListaResponse } from './interfaces/response';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
export interface StockData {
  codigoArticulo: string;
  descripcion: string;
  unidadMedida: string;
  stockSap: number;
  stockBodega: number;
}
@Component({
  selector: 'app-usuarios',
  templateUrl: './usuarios.component.html',
  styleUrls: ['./usuarios.component.scss']
})
export class UsuariosComponent implements OnInit, AfterViewInit{
  title:string="Usuario"
  displayedColumns: string[] = ['nombre', 'correo', 'apellidoPaterno', 'apellidoMaterno', 'idRol', 'acciones'];

  public dataMantenedorConsumo: UsuariosListaResponse[] = [];
  public dataSource: MatTableDataSource<UsuariosListaResponse>;

  constructor(private spinnerService: SpinnerService,
    private alertService: AlertService,
    private usuarioService:UsuarioService
  ) {
    this.dataSource = new MatTableDataSource(this.dataMantenedorConsumo);

  }

  ngOnInit(): void {
    
    this.dataSource.data = [
      {
        idUsuario: 1,
        nombre: "Alejandro",
        correo: "alejandrobarrera@gmail.com",
        apellidoPaterno: "Barrera",
        apellidoMaterno: "Acosta",
        idRol: 1
      },  {
        idUsuario: 2,
        nombre: "Ignacio",
        correo: "Vasquezignacio@gmail.com",
        apellidoPaterno: "Vasquez",
        apellidoMaterno: "Vasquez",
        idRol: 1
      }
    ]

  }

  submit(form: FormGroup){
    let data = {
      nombre: form.get('nombre')?.value,
      correo: form.get('correo')?.value,
      contrasena: form.get('contrasena')?.value,
      apellidoPaterno:form.get('apellidoPaterno')?.value,
      apellidoMaterno:form.get('apellidoMaterno')?.value,
      idRol: Number(form.get('rol')?.value),
    } as UsuarioRequest;

    this.spinnerService.showSpinner("Registrando Usuario...");
    this.usuarioService.postCrearUsuario(data).subscribe(
      {
        next:(result)=>{
          this.spinnerService.hideSpinner();
          this.alertService.showInfoAlert(null, 'Usuario registrado exitosamente.', 'Aceptar', 'info');
          form.reset();
        },
        error:()=>{
          this.spinnerService.hideSpinner();
          this.alertService.showInfoAlert(null, 'Usuario no se pudo registar.', 'Aceptar', 'error');
        }
      }
    )

  }


  applyFilter(event: Event, column: string) {
    const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
  
    // Configurar un filterPredicate personalizado
    this.dataSource.filterPredicate = (data: UsuariosListaResponse, filter: string) => {
      // Verificar que data[column] no sea undefined o null
      const dataStr = data[column as keyof UsuariosListaResponse] 
        ? data[column as keyof UsuariosListaResponse]!.toString().toLowerCase() 
        : '';
      
      return dataStr.includes(filter);
    };
  
    // Aplicar el filtro
    this.dataSource.filter = filterValue;
  }

  onUpdate(element: UsuariosListaResponse) {
    
    
  }

  onEdit(element: UsuariosListaResponse) {
    console.log('Editar para:', element.nombre);
  }

  @ViewChild(MatSort) sort!: MatSort;

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }

  
}
