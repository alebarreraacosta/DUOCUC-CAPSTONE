using System.Security.Cryptography;
using System.Text;
using System.Data;
using System.Data.SqlClient;
using System;
using System.Collections.Generic;

namespace ApiGardilcic.Models
{
    public class Acceso
    {
        // Método para validar usuario con correo y contrasena
        public UsuarioModel ValidarUsuario(string correo, string contrasena)
        {
            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[1];
                parametros[0] = new SqlParameter("correo", correo);

                DataTable tabla = conexion.EjecutarConsultaSelect("[acceso].[sp_obtener_usuario]", CommandType.StoredProcedure, parametros);

                if (tabla.Rows.Count == 0)
                {
                    return null; // Usuario no encontrado
                }

                DataRow fila = tabla.Rows[0];

                // Obtener la contrasena almacenada y limpiarla de saltos de línea o espacios
                string contrasenaAlmacenada = fila["CONTRASENA"].ToString().Trim();

                // Encriptar la contrasena proporcionada para compararla
                string contrasenaEncriptada = EncriptarContrasena(contrasena);

                // Comparar la contrasena encriptada con la almacenada
                if (contrasenaAlmacenada == contrasenaEncriptada)
                {
                    // Si coinciden, retornar la información del usuario
                    return new UsuarioModel
                    {
                        IdUsuario = Convert.ToInt32(fila["ID"]),
                        Nombre = fila["NOMBRE"].ToString(),
                        Correo = fila["CORREO"].ToString(),
                        ApellidoPaterno = fila["APELLIDO_PATERNO"].ToString(),
                        ApellidoMaterno = fila["APELLIDO_MATERNO"].ToString(),
                        IdRol = Convert.ToInt32(fila["ID_ROL"])
                    };
                }
                else
                {
                    return null; // Contrasena incorrecta
                }
            }
        }

        // Método para crear un nuevo usuario
        // Método para crear un nuevo usuario, ahora con parámetros individuales
        public bool CrearUsuario(string nombre, string correo, string contrasena, string apellidoPaterno, string apellidoMaterno, int idRol)
        {
            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[6];
                parametros[0] = new SqlParameter("nombre", nombre);
                parametros[1] = new SqlParameter("correo", correo);
                parametros[2] = new SqlParameter("contrasena", EncriptarContrasena(contrasena)); // Encriptar la contraseña
                parametros[3] = new SqlParameter("apellido_paterno", apellidoPaterno);
                parametros[4] = new SqlParameter("apellido_materno", apellidoMaterno);
                parametros[5] = new SqlParameter("id_rol", idRol); // Incluir rol del usuario

                int filasAfectadas = conexion.EjecutarConsultaNoSelect("acceso.sp_crear_usuario", CommandType.StoredProcedure, parametros);

                return filasAfectadas > 0; // Si se insertó el usuario, retornar true
            }
        }

        public bool ActualizarUsuario(int idUsuario, string nombre, string correo, string apellidoPaterno, string apellidoMaterno, int idRol)
        {
            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[6];
                parametros[0] = new SqlParameter("id_usuario", idUsuario);
                parametros[1] = new SqlParameter("nombre", nombre);
                parametros[2] = new SqlParameter("correo", correo);
                parametros[3] = new SqlParameter("apellido_paterno", apellidoPaterno);
                parametros[4] = new SqlParameter("apellido_materno", apellidoMaterno);
                parametros[5] = new SqlParameter("id_rol", idRol);

                int filasAfectadas = conexion.EjecutarConsultaNoSelect("[acceso].[sp_actualizar_usuario]", CommandType.StoredProcedure, parametros);

                return filasAfectadas > 0; // Si se actualizó el usuario, retornar true
            }
        }


        // Método para eliminar un usuario
        public bool EliminarUsuario(int idUsuario)
        {
            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[1];
                parametros[0] = new SqlParameter("id_usuario", idUsuario);

                int filasAfectadas = conexion.EjecutarConsultaNoSelect("[acceso].[sp_eliminar_usuario]", CommandType.StoredProcedure, parametros);

                return filasAfectadas > 0; // Si se eliminó el usuario, retornar true
            }
        }

        // Método para encriptar la contrasena
        private string EncriptarContrasena(string contrasena)
        {
            using (SHA256 sha256Hash = SHA256.Create())
            {
                byte[] bytes = sha256Hash.ComputeHash(Encoding.UTF8.GetBytes(contrasena));

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < bytes.Length; i++)
                {
                    builder.Append(bytes[i].ToString("x2"));
                }
                return builder.ToString();
            }
        }

        // Método para listar todos los usuarios
        public List<UsuarioModel> ObtenerUsuarios()
        {
            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                DataTable tabla = conexion.EjecutarConsultaSelect("[acceso].[sp_listar_usuarios]", CommandType.StoredProcedure, null);

                List<UsuarioModel> listaUsuarios = new List<UsuarioModel>();

                foreach (DataRow fila in tabla.Rows)
                {
                    UsuarioModel usuario = new UsuarioModel
                    {
                        IdUsuario = Convert.ToInt32(fila["ID"]),
                        Nombre = fila["NOMBRE"].ToString(),
                        Correo = fila["CORREO"].ToString(),
                        ApellidoPaterno = fila["APELLIDO_PATERNO"].ToString(),
                        ApellidoMaterno = fila["APELLIDO_MATERNO"].ToString(),
                        IdRol = Convert.ToInt32(fila["ID_ROL"])
                    };
                    listaUsuarios.Add(usuario);
                }

                return listaUsuarios;
            }
        }
    }

    // Clase para retornar la información del usuario
    public class UsuarioModel
    {
        public int IdUsuario { get; set; }
        public string Nombre { get; set; }
        public string Correo { get; set; }
        public string ApellidoPaterno { get; set; }
        public string ApellidoMaterno { get; set; }
        public int IdRol { get; set; }
    }
}



