using System.Security.Cryptography;
using System.Text;
using System.Data;
using System.Data.SqlClient;
using System;
using System.Collections.Generic;
using System.IO;
using System.Web;
using Microsoft.SharePoint.Client;
using System.Security;

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

        public bool InsertarInventarioSap(List<InventarioSapModel> datosInventario)
        {
            try
            {
                using (Conectar conexion = new Conectar())
                {
                    conexion.Abrir();

                    foreach (var item in datosInventario)
                    {
                        SqlParameter[] parametros = new SqlParameter[9]; // Aumenta el tamaño del array a 9
                        parametros[0] = new SqlParameter("numero_articulo", item.NumeroArticulo);
                        parametros[1] = new SqlParameter("descripcion_articulo", item.DescripcionArticulo);
                        parametros[2] = new SqlParameter("unidad_medida", item.UnidadMedida);
                        parametros[3] = new SqlParameter("stock_almacen", item.StockAlmacen);
                        parametros[4] = new SqlParameter("precio_unitario", item.PrecioUnitario);
                        parametros[5] = new SqlParameter("saldo_almacen", item.SaldoAlmacen);
                        parametros[6] = new SqlParameter("codigo_barras", item.CodigoBarras);
                        parametros[7] = new SqlParameter("codigo_almacen", item.CodigoAlmacen); // Nuevo parámetro
                        parametros[8] = new SqlParameter("fecha_carga", item.FechaCarga);       // Nuevo parámetro

                        conexion.EjecutarConsultaNoSelect("[Inventario].[sp_insertar_inventario_sap]", CommandType.StoredProcedure, parametros);
                    }
                }

                return true; // Inserción exitosa
            }
            catch (Exception)
            {
                return false; // Manejo de error
            }
        }

        public bool InsertarInventarioFisico(List<InventarioFisicoModel> datosInventario)
        {
            try
            {
                using (Conectar conexion = new Conectar())
                {
                    conexion.Abrir();

                    foreach (var item in datosInventario)
                    {
                        SqlParameter[] parametros = new SqlParameter[4];
                        parametros[0] = new SqlParameter("cantidad_fisica", item.CantidadFisica);
                        parametros[1] = new SqlParameter("fecha", item.Fecha);
                        parametros[2] = new SqlParameter("id_usuario", item.IdUsuario);
                        parametros[3] = new SqlParameter("numero_articulo", item.NumeroArticulo);

                        // Ejecutar el procedimiento almacenado
                        conexion.EjecutarConsultaNoSelect("[Inventario].[sp_insertar_inventario_fisico]", CommandType.StoredProcedure, parametros);
                    }
                }

                return true; // Inserción exitosa
            }
            catch (Exception)
            {
                return false; // Error en la inserción
            }
        }


        public void EnviarCorreoFinalizacionInventario()
        {
            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                // Ejecutar el procedimiento de envío de correo
                conexion.EjecutarConsultaSelect("[inventario].[sp_envia_correo_finalizacion_inventario]", CommandType.StoredProcedure, null);
            }
        }


        public List<CuadraturaDiferenciasCompletaModel> ObtenerCuadraturaDiferenciasCompleta(string codigoInventario)
        {
            List<CuadraturaDiferenciasCompletaModel> diferencias = new List<CuadraturaDiferenciasCompletaModel>();

            // Extraer el id_inventario del código (ejemplo: INV-004 => id_inventario = 4)
            int idInventario = int.Parse(codigoInventario.Split('-')[1]);

            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                // Crear y configurar los parámetros
                SqlParameter[] parametros = new SqlParameter[1];
                parametros[0] = new SqlParameter("@id_inventario", idInventario);

                // Ejecutar el procedimiento almacenado para obtener las diferencias
                DataTable tabla = conexion.EjecutarConsultaSelect("[Inventario].[sp_obtener_cuadratura_diferencias_completa]", CommandType.StoredProcedure, parametros);

                foreach (DataRow fila in tabla.Rows)
                {
                    diferencias.Add(new CuadraturaDiferenciasCompletaModel
                    {
                        CodArticulo = fila["codArticulo"].ToString(),
                        Descripcion = fila["descripcion"].ToString(),
                        Unidad = fila["unidad"].ToString(),
                        StockSAP = Convert.ToInt32(fila["stockSAP"]),
                        StockBodega = Convert.ToInt32(fila["stockBodega"]),
                        PrecioUnitario = Convert.ToInt32(fila["precioUnitario"]),
                        PrecioTotal = Convert.ToInt32(fila["precioTotal"]),
                        IsCuadrado = Convert.ToBoolean(fila["isCuadrado"])
                    });
                }
            }

            return diferencias;
        }


        public bool InsertarItemNoEncontrado(List<ItemNoEncontradoModel> itemsNoEncontrados)
        {
            try
            {
                using (Conectar conexion = new Conectar())
                {
                    conexion.Abrir();

                    foreach (var item in itemsNoEncontrados)
                    {
                        SqlParameter[] parametros = new SqlParameter[7];
                        parametros[0] = new SqlParameter("numero_articulo", item.NumeroArticulo);
                        parametros[1] = new SqlParameter("codigo_barras", item.CodigoBarras);
                        parametros[2] = new SqlParameter("descripcion", item.Descripcion);
                        parametros[3] = new SqlParameter("cantidad_contada", item.CantidadContada);
                        parametros[4] = new SqlParameter("codigo_almacen", item.CodigoAlmacen);
                        parametros[5] = new SqlParameter("fecha", item.Fecha);
                        parametros[6] = new SqlParameter("id_usuario", item.IdUsuario);

                        conexion.EjecutarConsultaNoSelect("[Inventario].[sp_insertar_item_no_encontrado]", CommandType.StoredProcedure, parametros);
                    }
                }

                return true; // Inserción exitosa
            }
            catch (Exception)
            {
                return false;
            }
        }

        public CuadraturaProductoModel ObtenerProductoCuadrado(string codigoInventario, string codigoProducto)
        {
            // Extraer el id_inventario del código (ejemplo: INV-004 => id_inventario = 4)
            int idInventario = int.Parse(codigoInventario.Split('-')[1]);

            CuadraturaProductoModel producto = null;

            using (Conectar conexion = new Conectar())
            {
                conexion.Abrir();

                // Configurar parámetros
                SqlParameter[] parametros = new SqlParameter[2];
                parametros[0] = new SqlParameter("id_inventario", idInventario);
                parametros[1] = new SqlParameter("codigo_producto", codigoProducto);

                // Ejecutar el procedimiento almacenado para obtener el producto cuadrado
                DataTable tabla = conexion.EjecutarConsultaSelect("[Inventario].[sp_obtener_datos_producto_cuadrado]", CommandType.StoredProcedure, parametros);

                if (tabla.Rows.Count > 0)
                {
                    DataRow fila = tabla.Rows[0];
                    producto = new CuadraturaProductoModel
                    {
                        codigoInventario = codigoInventario,
                        codigoProducto = codigoProducto,
                        cantidadACuadrar = Convert.ToInt32(fila["cantidadACuadrar"]),  // Asegurarse de que coincida con el nombre en el SP
                        descripcion = fila["descripcion"].ToString(),
                        pdfBase64 = fila["pdfBase64"] != DBNull.Value ? fila["pdfBase64"].ToString() : null
                    };
                }
            }

            return producto;
        }

        public bool CuadrarProducto(string codigoInventario, string codigoProducto, int cantidadACuadrar, string descripcion, HttpPostedFileBase archivo)
        {
            // Extraer el id de inventario del código (ejemplo: "INV-006" => idInventario = 6)
            int idInventario = int.Parse(codigoInventario.Split('-')[1]);

            if (archivo == null || archivo.ContentLength == 0)
            {
                throw new ArgumentException("El archivo proporcionado no es válido.");
            }

            try
            {
                using (var conexion = new Conectar())
                {
                    conexion.Abrir();

                    // Subir el archivo a SharePoint
                    Stream archivoStream = archivo.InputStream;
                    string nombreArchivo = Path.GetFileName(archivo.FileName);
                    string carpetaDestino = "/sites/InformaticaGardilcic/Shared Documents/PDF_PRUEBAS_CIG_IGNACIO";


                    string rutaArchivoSharePoint = SubirArchivoStream(archivoStream, nombreArchivo, carpetaDestino);

                    // Agregar prefijo completo de URL para la ruta
                    rutaArchivoSharePoint = $"https://grupogardilcic.sharepoint.com{rutaArchivoSharePoint}";

                    // Ejecutar el procedimiento almacenado con la ruta del archivo
                    SqlParameter[] parametros = new SqlParameter[5];
                    parametros[0] = new SqlParameter("@id_inventario", idInventario);
                    parametros[1] = new SqlParameter("@codigo_producto", codigoProducto);
                    parametros[2] = new SqlParameter("@cantidad_a_cuadrar", cantidadACuadrar);
                    parametros[3] = new SqlParameter("@descripcion", descripcion ?? (object)DBNull.Value);
                    parametros[4] = new SqlParameter("@pdf_base64", rutaArchivoSharePoint);

                    conexion.EjecutarConsultaSelect("[Inventario].[sp_cuadrar_producto]", CommandType.StoredProcedure, parametros);

                    return true;
                }
            }
            catch (Exception ex)
            {
                throw new Exception("Error al cuadrar el producto: " + ex.Message);
            }
        }

        private const string SharePointSiteUrl = "";
        private const string SharePointUser = "";
        private const string SharePointPassword = "";

        // Subir archivo a SharePoint y guardar la ruta en la base de datos
        public string SubirArchivoStreamYGuardarRuta(int idDiferencia, Stream archivoStream, string nombreArchivo, string carpetaDestino)
        {
            try
            {
                // Subir archivo a SharePoint
                string urlArchivoSharePoint = SubirArchivoStream(archivoStream, nombreArchivo, carpetaDestino);

                // Guardar la ruta en la base de datos
                GuardarRutaEnBaseDeDatos(idDiferencia, urlArchivoSharePoint);

                return urlArchivoSharePoint;
            }
            catch (Exception ex)
            {
                throw new Exception("Error al subir archivo y guardar ruta: " + ex.Message);
            }
        }

        // Subir un archivo a SharePoint desde un Stream
        private string SubirArchivoStream(Stream archivoStream, string nombreArchivo, string carpetaDestino)
        {
            if (archivoStream == null || archivoStream.Length == 0)
            {
                throw new ArgumentException("El archivo proporcionado no es válido.");
            }

            if (string.IsNullOrEmpty(carpetaDestino))
            {
                throw new ArgumentException("La ruta 'carpetaDestino' no puede estar vacía.");
            }

            try
            {
                using (var cliente = new ClientContext(SharePointSiteUrl))
                {
                    // Configurar credenciales de SharePoint
                    SecureString contrasenaSegura = new SecureString();
                    foreach (char c in SharePointPassword) { contrasenaSegura.AppendChar(c); }
                    cliente.Credentials = new SharePointOnlineCredentials(SharePointUser, contrasenaSegura);

                    // Validar que la carpeta destino exista
                    var carpeta = cliente.Web.GetFolderByServerRelativeUrl(carpetaDestino);
                    cliente.Load(carpeta);
                    cliente.ExecuteQuery();

                    // Subir archivo al SharePoint desde el Stream
                    FileCreationInformation archivoNuevo = new FileCreationInformation
                    {
                        ContentStream = archivoStream,
                        Url = nombreArchivo,
                        Overwrite = true
                    };

                    var archivoSubido = carpeta.Files.Add(archivoNuevo);
                    cliente.Load(archivoSubido);
                    cliente.ExecuteQuery();

                    return $"{carpetaDestino}/{nombreArchivo}";
                }
            }
            catch (ServerException ex) when (ex.Message.Contains("does not exist"))
            {
                throw new Exception($"La carpeta especificada '{carpetaDestino}' no existe en SharePoint.");
            }
            catch (Exception ex)
            {
                throw new Exception("Error al subir archivo a SharePoint: " + ex.Message);
            }
        }

        // Guardar la ruta del archivo en la base de datos
        private void GuardarRutaEnBaseDeDatos(int idDiferencia, string rutaArchivo)
        {
            using (var conexion = new Conectar())
            {
                conexion.Abrir();

                SqlParameter[] parametros = new SqlParameter[2];
                parametros[0] = new SqlParameter("idDiferencia", idDiferencia);
                parametros[1] = new SqlParameter("rutaArchivo", rutaArchivo);

                conexion.EjecutarConsultaNoSelect("[Inventario].[sp_guardar_ruta_archivo]", CommandType.StoredProcedure, parametros);
            }
        }

        public string ObtenerRutaArchivo(int idDiferencia)
        {
            try
            {
                using (var conexion = new Conectar())
                {
                    conexion.Abrir();

                    SqlParameter[] parametros = new SqlParameter[1];
                    parametros[0] = new SqlParameter("@idDiferencia", idDiferencia);

                    DataTable resultado = conexion.EjecutarConsultaSelect("[Inventario].[sp_obtener_ruta_archivo]", CommandType.StoredProcedure, parametros);

                    if (resultado.Rows.Count > 0)
                    {
                        return resultado.Rows[0]["rutaArchivo"].ToString();
                    }

                    return null; // Si no se encuentra la ruta
                }
            }
            catch (Exception ex)
            {
                throw new Exception($"Error al obtener la ruta del archivo: {ex.Message}");
            }
        }

        // Descargar archivo desde SharePoint
        public Stream ObtenerStreamArchivoSharePoint(string rutaSharePoint)
        {
            try
            {
                using (var cliente = new ClientContext(SharePointSiteUrl))
                {
                    SecureString contrasenaSegura = new SecureString();
                    foreach (char c in SharePointPassword) { contrasenaSegura.AppendChar(c); }
                    cliente.Credentials = new SharePointOnlineCredentials(SharePointUser, contrasenaSegura);

                    // Obtener el archivo como stream desde SharePoint
                    FileInformation fileInformation = Microsoft.SharePoint.Client.File.OpenBinaryDirect(cliente, rutaSharePoint);

                    // Crear un MemoryStream para retornar el archivo
                    MemoryStream memoryStream = new MemoryStream();
                    fileInformation.Stream.CopyTo(memoryStream);
                    memoryStream.Position = 0; // Resetear el puntero al inicio
                    return memoryStream;
                }
            }
            catch (Exception ex)
            {
                throw new Exception($"Error al obtener el archivo desde SharePoint: {ex.Message}");
            }
        }







    }

    public class CuadrarProductoRequest
    {
        public string CodigoInventario { get; set; }
        public string CodigoProducto { get; set; }
        public int CantidadACuadrar { get; set; }
        public string Descripcion { get; set; }
        public string PdfBase64 { get; set; }
    }


    public class CuadraturaProductoModel
    {
        public string codigoInventario { get; set; }
        public string codigoProducto { get; set; }
        public int cantidadACuadrar { get; set; }
        public string descripcion { get; set; }
        public string pdfBase64 { get; set; }
    }


    public class ItemNoEncontradoModel
    {
        public string NumeroArticulo { get; set; }
        public string CodigoBarras { get; set; }
        public string Descripcion { get; set; }
        public int CantidadContada { get; set; }
        public string CodigoAlmacen { get; set; }
        public DateTime Fecha { get; set; }
        public int IdUsuario { get; set; }
    }



    public class InventarioFisicoModel
    {
        public int CantidadFisica { get; set; }
        public DateTime Fecha { get; set; }
        public int IdUsuario { get; set; }
        public string NumeroArticulo { get; set; }
    }


    public class InventarioSapModel
    {
        public string NumeroArticulo { get; set; }
        public string DescripcionArticulo { get; set; }
        public string UnidadMedida { get; set; }
        public int StockAlmacen { get; set; }
        public decimal PrecioUnitario { get; set; }
        public decimal SaldoAlmacen { get; set; }
        public string CodigoBarras { get; set; }
        public string CodigoAlmacen { get; set; }  // Campo adicional
        public DateTime FechaCarga { get; set; }   // Campo adicional
    }

    public class CuadraturaDiferenciasCompletaModel
    {
        public string CodArticulo { get; set; }
        public string Descripcion { get; set; }
        public string Unidad { get; set; }
        public int StockSAP { get; set; }
        public int StockBodega { get; set; }
        public int PrecioUnitario { get; set; }
        public int PrecioTotal { get; set; }
        public bool IsCuadrado { get; set; }
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



