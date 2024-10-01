export interface UsuariosListaResponse{
    nombre:string;
    apellidoPaterno:string;
    apellidoMaterno:string;
    correo:string;
    idRol:number;
    idUsuario:number;
    edit?:boolean
}