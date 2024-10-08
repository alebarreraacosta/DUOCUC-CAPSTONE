export interface LoginResponse{

    exito:boolean;
    mensaje?:string;
    usuario?:Usuario;
  }

export interface Usuario{
  IdUsuario: number;
  Nombre: string;
  Correo:  string;
  ApellidoPaterno:  string;
  ApellidoMaterno: string;
  IdRol:number;
}