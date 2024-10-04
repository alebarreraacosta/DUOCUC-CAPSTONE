export interface UsuarioRequest {
    nombre: string;          
    correo: string;           
    contrasena: string;      
    apellidoPaterno: string;  
    apellidoMaterno: string;  
    idRol: number;            
  }
  export interface UsuarioActualizarRequest {
    Nombre: string;          
    Correo: string;           
    ApellidoPaterno: string;  
    ApellidoMaterno: string;  
    IdRol: number;      
    IdUsuario:number;      
  }