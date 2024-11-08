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

        public List<MesAnnioModel> ObtenerMesesInventarios()
        {
            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                DataTable tabla = conexion.EjecutarConsultaSelect("[inventario].[sp_obtener_meses_inventarios]", CommandType.StoredProcedure, null);

                List<MesAnnioModel> mesesInventarios = new List<MesAnnioModel>();
                foreach (DataRow fila in tabla.Rows)
                {
                    mesesInventarios.Add(new MesAnnioModel
                    {
                        MesAnnio = fila["mesAnnio"].ToString(),
                        Annio = fila["annio"].ToString(),
                        Mes = fila["mes"].ToString()
                    });
                }

                return mesesInventarios;
            }
        }
        public List<ArticuloModel> ObtenerArticulosPorMes(int Mes, int Annio)
        {
            List<ArticuloModel> articulos = new List<ArticuloModel>();

            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[2];
                parametros[0] = new SqlParameter("mes", Mes);
                parametros[1] = new SqlParameter("annio", Annio);

                DataTable tabla = conexion.EjecutarConsultaSelect("[inventario].[sp_obtener_articulos_por_mes]", CommandType.StoredProcedure, parametros);

                foreach (DataRow fila in tabla.Rows)
                {
                    articulos.Add(new ArticuloModel
                    {
                        CodigoProducto = fila["codigoProducto"].ToString(),
                        Descripcion = fila["descripcion"].ToString()
                    });
                }
            }

            return articulos;
        }

        public DatosGraficoModel ObtenerDatosGraficoPorMes(int mes, int annio)
        {
            DatosGraficoModel datosGrafico = new DatosGraficoModel
            {
                DatosSap = new List<ArticuloGraficoModel>(),
                DatosFisico = new List<ArticuloGraficoModel>()
            };

            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[2];
                parametros[0] = new SqlParameter("mes", mes);
                parametros[1] = new SqlParameter("annio", annio);

                DataTable tabla = conexion.EjecutarConsultaSelect("[inventario].[sp_obtener_datos_grafico_por_mes]", CommandType.StoredProcedure, parametros);

                foreach (DataRow fila in tabla.Rows)
                {
                    var articulo = new ArticuloGraficoModel
                    {
                        IdProducto = Convert.ToInt32(fila["idProducto"]),
                        CodigoProducto = fila["codigoProducto"].ToString(),
                        Cantidad = Convert.ToInt32(fila["cantidad"]),
                        Descripcion = fila["descripcion"].ToString(),
                        PrecioUnitario = Convert.ToInt32(fila["precioUnitario"]),  // Convertido a int
                        Total = Convert.ToInt32(fila["total"])                    // Convertido a int
                    };

                    if (fila["origen"].ToString() == "SAP")
                    {
                        datosGrafico.DatosSap.Add(articulo);
                    }
                    else if (fila["origen"].ToString() == "Fisico")
                    {
                        datosGrafico.DatosFisico.Add(articulo);
                    }
                }
            }

            return datosGrafico;
        }

        public List<DatosGraficoProductoModel> ObtenerDatosGraficoProducto(string codigoProducto)
        {
            List<DatosGraficoProductoModel> datosGraficoProducto = new List<DatosGraficoProductoModel>();

            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[1];
                parametros[0] = new SqlParameter("codigoProducto", codigoProducto);

                DataTable tabla = conexion.EjecutarConsultaSelect("[inventario].[sp_obtener_datos_grafico_producto]", CommandType.StoredProcedure, parametros);

                foreach (DataRow fila in tabla.Rows)
                {
                    datosGraficoProducto.Add(new DatosGraficoProductoModel
                    {
                        Mes = fila["mes"].ToString(),
                        PrecioUnitario = Convert.ToDecimal(fila["precioUnitario"]),
                        Cantidad = Convert.ToInt32(fila["cantidad"]),
                        Total = Convert.ToDecimal(fila["total"])
                    });
                }
            }

            return datosGraficoProducto;
        }
        public List<InventarioModel> ObtenerListadoInventario()
        {
            List<InventarioModel> listadoInventario = new List<InventarioModel>();

            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                DataTable tabla = conexion.EjecutarConsultaSelect("[inventario].[sp_obtener_listado_inventario]", CommandType.StoredProcedure);

                foreach (DataRow fila in tabla.Rows)
                {
                    listadoInventario.Add(new InventarioModel
                    {
                        CodigoInventario = fila["codigoInventario"].ToString(),
                        Fecha = fila["fecha"].ToString(),
                        Estado = fila["estado"].ToString()
                    });
                }
            }

            return listadoInventario;
        }

        public int InsertarCabeceraInventario(int idUsuario, DateTime fechaInicio)
        {
            int idInventario = 0;

            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[2];
                parametros[0] = new SqlParameter("id_usuario", idUsuario);
                parametros[1] = new SqlParameter("fecha_inicio", fechaInicio);

                DataTable tabla = conexion.EjecutarConsultaSelect("[inventario].[sp_insertar_cabecera_inventario]", CommandType.StoredProcedure, parametros);

                if (tabla.Rows.Count > 0)
                {
                    idInventario = Convert.ToInt32(tabla.Rows[0]["id_inventario"]);
                }
            }

            return idInventario;
        }

        public void EnviarCorreoFinalizacionInventario(int idInventario)
        {
            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[1];
                parametros[0] = new SqlParameter("id_inventario", idInventario);

                // Ejecutar el procedimiento de envío de correo
                conexion.EjecutarConsultaSelect("[inventario].[sp_envia_correo_finalizacion_inventario]", CommandType.StoredProcedure, parametros);
            }
        }






    }

    public class InventarioModel
    {
        public string CodigoInventario { get; set; }
        public string Fecha { get; set; }
        public string Estado { get; set; }
    }
    public class DatosGraficoProductoModel
    {
        public string Mes { get; set; }
        public decimal PrecioUnitario { get; set; }
        public int Cantidad { get; set; }
        public decimal Total { get; set; }
    }

    public class DatosGraficoModel
    {
        public List<ArticuloGraficoModel> DatosSap { get; set; }
        public List<ArticuloGraficoModel> DatosFisico { get; set; }
    }

    public class ArticuloGraficoModel
    {
        public int IdProducto { get; set; }
        public string CodigoProducto { get; set; }
        public int Cantidad { get; set; }
        public string Descripcion { get; set; }
        public int PrecioUnitario { get; set; }  // Cambiado a int
        public int Total { get; set; }           // Cambiado a int
    }



    public class ArticuloModel
    {
        public string CodigoProducto { get; set; }
        public string Descripcion { get; set; }
    }
    public class MesAnnioModel
    {
        public string MesAnnio { get; set; }
        public string Annio { get; set; }
        public string Mes { get; set; }
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



