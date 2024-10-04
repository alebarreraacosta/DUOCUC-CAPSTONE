import { AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { SpinnerService } from 'src/app/shared/service/spinner.service';
import { AlertService } from 'src/app/shared/service/sweetalert.service';
import { UsuarioService } from './services/usuario.service';
import { UsuarioActualizarRequest, UsuarioRequest } from './interfaces/request';
import { UsuariosListaResponse } from './interfaces/response';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { FormsReactiveService } from 'src/app/shared/service/forms-reactive.service';
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
  formUsuario! :FormGroup;
  isHabilitarActualizar:boolean=false;
  @ViewChild(MatSort) sort!: MatSort;

  public dataMantenedorConsumo: UsuariosListaResponse[] = [];
  public dataSource: MatTableDataSource<UsuariosListaResponse>;

  constructor(private spinnerService: SpinnerService,
    private alertService: AlertService,
    private usuarioService:UsuarioService,
    private formularioReactivo : FormsReactiveService,
    private cdr: ChangeDetectorRef
  ) {
    this.dataSource = new MatTableDataSource(this.dataMantenedorConsumo);
  }

  ngOnInit(): void {
    this.formUsuario= this.formularioReactivo.crearFormularioUsuario();
    this.spinnerService.showSpinner();
    this.listarUsuario();
  }

  private listarUsuario(){
    this.usuarioService.getListaUsuarios().subscribe({
      next:(result)=>{
        this.spinnerService.hideSpinner();
        if(result){
          this.dataSource.data = result.usuarios
        }
      },
      error:(err)=>{
        this.spinnerService.hideSpinner();
        if(err.status>= 400 && err.status>= 599 ){
          this.alertService.showInfoAlert('Ha ocurrido un error, al cargar lista de usuarios',"Error carga de usuarios",'Aceptar','error');
        }

      }
    })
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
          this.listarUsuario();
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
    this.dataSource.filter = filterValue;
  }

  onUpdate(element: UsuariosListaResponse) {
    this.formUsuario = this.formularioReactivo.crearFormularioUsuario();
    this.isHabilitarActualizar = true;
    this.cdr.detectChanges();
    this.formUsuario.patchValue({
      nombre:element.Nombre,
      apellidoPaterno: element.ApellidoPaterno,
      apellidoMaterno: element.ApellidoMaterno,
      contrasena: "************",
      rol: element.IdRol.toString(),
      correo: element.Correo,
      idUsuario: element.IdUsuario
    });
  }

  actualizarUsuario(form:FormGroup){
    let data = {
      Nombre: form.get('nombre')?.value,
      Correo: form.get('correo')?.value,
      ApellidoPaterno:form.get('apellidoPaterno')?.value,
      ApellidoMaterno:form.get('apellidoMaterno')?.value,
      IdRol: Number(form.get('rol')?.value),
      IdUsuario: Number(form.get('idUsuario')?.value),
    } as UsuarioActualizarRequest;
    this.alertService.showConfirmationAlert('¿Estás seguro que quieres actualizar los datos de este usuario?','Actualizar usuario','Actualizar','Cancelar').then(respuesta=>{
      if(respuesta){
        this.spinnerService.showSpinner("Actualizando datos usuario...");
        this.usuarioService.updateUsuario(data).subscribe({
          next:(result)=>{
            this.isHabilitarActualizar=false;
            this.spinnerService.hideSpinner();
             if(result){
              this.alertService.showInfoAlert("Usuario modificado exitosamente","Actualización usuario","Aceptar","info");
              this.listarUsuario();
             }else{
              this.alertService.showInfoAlert("No se pudo actualizar usuario","Actualización usuario","Aceptar","error");
             }
          },
          error:(err)=>{
            this.spinnerService.hideSpinner();
            this.alertService.showInfoAlert("Ha ocurrido un error intente nuevamente","Error Actualización","Aceptar","error");
          }
        })
      }
    })
  }

  onDelete(element: UsuariosListaResponse) {
    this.alertService.showConfirmationAlert('¿Estás seguro que quieres eliminar este usuario?','Eliminar usuario','Eliminar','Cancelar').then(respuesta=>{
      const {IdUsuario,edit, ...data} = element
      if(respuesta){
        this.spinnerService.showSpinner("Eliminando usuario...");
        this.usuarioService.deleteUsuario(element.IdUsuario).subscribe({
          next:(result)=>{
            this.spinnerService.hideSpinner();
             if(result){
              this.alertService.showInfoAlert("Usuario eliminado exitosamente","Eliminación Usuario","Aceptar","info");
              this.listarUsuario();
             }else{
              this.alertService.showInfoAlert("No se pudo eliminar usuario","Eliminación Usuario","Aceptar","error");
             }
          },
          error:(err)=>{
            this.spinnerService.hideSpinner();
            this.alertService.showInfoAlert("Ha ocurrido un error intente nuevamente","Error Eliminación","Aceptar","error");
          }
        })
      }

    })
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }

  cancelar(){
    this.formUsuario.get('contrasena')?.enable();
    this.isHabilitarActualizar = false;
  }
  
}
