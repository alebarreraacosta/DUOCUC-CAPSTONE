export interface UsuariosListaResponse{
    IdUsuario: number;
    Nombre: string;
    Correo: string;
    ApellidoPaterno: string;
    ApellidoMaterno: string;
    IdRol: number;
    edit?:boolean
}

export interface UsuariosResponse{
    exito: boolean
    usuarios: UsuariosListaResponse[]
}