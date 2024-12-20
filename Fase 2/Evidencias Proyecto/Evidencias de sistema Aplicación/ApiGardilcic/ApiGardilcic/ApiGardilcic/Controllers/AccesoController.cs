﻿using ApiGardilcic.Models;
using System.Net;
using System;
using System.Web.Mvc;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.IO;

namespace ApiGardilcic.Controllers
{
    public class AccesoController : Controller
    {
        Acceso acceso = new Acceso();
        InventarioModel inventarioModel = new InventarioModel();

        // Método GET para obtener y validar usuario
        public JsonResult ObtenerUsuario(string correo, string contrasena)
        {
            try
            {
                var resultado = acceso.ValidarUsuario(correo, contrasena);

                if (resultado != null)
                {
                    // Devolver 200 OK cuando la autenticación es exitosa
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, usuario = resultado }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver 401 Unauthorized cuando las credenciales son incorrectas
                    Response.StatusCode = (int)HttpStatusCode.Unauthorized;
                    return Json(new { exito = false, mensaje = "Correo o contrasena incorrectos" }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Devolver 500 Internal Server Error cuando ocurre un error en el servidor
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        // Método POST para crear un nuevo usuario
        public JsonResult CrearUsuario(string nombre, string correo, string contrasena, string apellidoPaterno, string apellidoMaterno, int idRol)
        {
            try
            {
                // Intentar crear el usuario
                bool creado = acceso.CrearUsuario(nombre, correo, contrasena, apellidoPaterno, apellidoMaterno, idRol);

                if (creado)
                {
                    // Devolver éxito si se creó correctamente
                    return Json(new { exito = true, mensaje = "Usuario creado correctamente" }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver error si no se pudo crear
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "Error al crear el usuario" }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Devolver error interno si ocurrió una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        // Método PUT para actualizar un usuario
        public JsonResult ActualizarUsuario(int IdUsuario, string Nombre, string Correo, string ApellidoPaterno, string ApellidoMaterno, int IdRol)
        {
            try
            {
                // Intentar actualizar el usuario usando los parámetros directamente
                bool actualizado = acceso.ActualizarUsuario(IdUsuario, Nombre, Correo, ApellidoPaterno, ApellidoMaterno, IdRol);

                if (actualizado)
                {
                    // Devolver éxito si se actualizó correctamente
                    Response.StatusCode = (int)HttpStatusCode.OK; // 200 OK
                    return Json(new { exito = true, mensaje = "Usuario actualizado correctamente" }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver error si no se pudo actualizar
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "Error al actualizar el usuario" }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Devolver error interno si ocurrió una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        // Método DELETE para eliminar un usuario
        public JsonResult EliminarUsuario(int IdUsuario)
        {
            try
            {
                // Intentar eliminar el usuario
                bool eliminado = acceso.EliminarUsuario(IdUsuario);

                if (eliminado)
                {
                    // Devolver éxito si se eliminó correctamente
                    Response.StatusCode = (int)HttpStatusCode.OK; // 200 OK
                    return Json(new { exito = true, mensaje = "Usuario eliminado correctamente" }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver error si no se pudo eliminar
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "Error al eliminar el usuario" }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Devolver error interno si ocurrió una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        // Método GET para listar todos los usuarios
        public JsonResult ListarUsuarios()
        {
            try
            {
                // Obtener todos los usuarios desde el modelo
                List<UsuarioModel> listaUsuarios = acceso.ObtenerUsuarios();

                if (listaUsuarios.Count > 0)
                {
                    // Devolver 200 OK con la lista de usuarios
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, usuarios = listaUsuarios }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver 404 Not Found si no se encontraron usuarios
                    Response.StatusCode = (int)HttpStatusCode.NotFound;
                    return Json(new { exito = false, mensaje = "No se encontraron usuarios." }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Devolver 500 Internal Server Error si ocurrió una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        // Método para obtener la lista de meses y años de los inventarios realizados
        public JsonResult ObtenerMesesInventarios()
        {
            try
            {
                var mesesInventarios = acceso.ObtenerMesesInventarios();

                if (mesesInventarios != null && mesesInventarios.Any())
                {
                    // Devolver éxito si se obtuvo correctamente la lista
                    Response.StatusCode = (int)HttpStatusCode.OK; // 200 OK
                    return Json(new { exito = true, mesesInventarios }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver error si no se encontraron datos
                    Response.StatusCode = (int)HttpStatusCode.NotFound;
                    return Json(new { exito = false, mensaje = "No se encontraron meses de inventarios." }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Devolver error interno si ocurrió una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        // Método para obtener artículos por mes y año
        public JsonResult ObtenerArticulosPorMes(int Mes, int Annio)
        {
            try
            {
                var resultado = acceso.ObtenerArticulosPorMes(Mes, Annio);

                if (resultado != null && resultado.Count > 0)
                {
                    // Devolver 200 OK con los datos obtenidos
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, articulos = resultado }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver 404 Not Found si no hay resultados
                    Response.StatusCode = (int)HttpStatusCode.NotFound;
                    return Json(new { exito = false, mensaje = "No se encontraron artículos para el mes y año especificados" }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Devolver 500 Internal Server Error si ocurre un error
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        // Método para obtener datos del gráfico según el mes y año, diferenciando SAP y Físico
        
        public JsonResult ObtenerDatosGraficoPorMesAnnio(int mes, int annio)
        {
            try
            {
                var datosGrafico = acceso.ObtenerDatosGraficoPorMes(mes, annio);

                // Devolver 200 OK con los datos en la estructura solicitada
                Response.StatusCode = (int)HttpStatusCode.OK;
                return Json(new { exito = true, datosGrafico }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                // Devolver 500 Internal Server Error si ocurre un error
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        // Método para obtener los datos de un producto específico para el gráfico de variación mensual
        public JsonResult ObtenerDatosGraficoProductoSeleccionado(string idProducto)
        {
            try
            {
                // Llamar al método del modelo para obtener los datos del producto seleccionado
                var datosGraficoProducto = acceso.ObtenerDatosGraficoProducto(idProducto);

                // Validar si se encontraron datos para el producto
                if (datosGraficoProducto == null || datosGraficoProducto.Count == 0)
                {
                    // Devolver 404 Not Found si no se encuentran datos para el producto especificado
                    Response.StatusCode = (int)HttpStatusCode.NotFound;
                    return Json(new { exito = false, mensaje = "No se encontraron datos para el producto especificado." }, JsonRequestBehavior.AllowGet);
                }

                // Devolver 200 OK con los datos en la estructura solicitada
                Response.StatusCode = (int)HttpStatusCode.OK;
                return Json(new { exito = true, datosGraficoProducto }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                // Devolver 500 Internal Server Error si ocurre un error en el servidor
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        public JsonResult ObtenerListadoInventario()
        {
            try
            {
                // Llamar al método del modelo para obtener el listado de inventarios
                var inventarios = acceso.ObtenerListadoInventario();

                // Verificar si se obtuvieron inventarios
                if (inventarios == null || !inventarios.Any())
                {
                    // Devolver 404 Not Found si no se encontraron inventarios
                    Response.StatusCode = (int)HttpStatusCode.NotFound;
                    return Json(new { exito = false, mensaje = "No se encontraron inventarios" }, JsonRequestBehavior.AllowGet);
                }

                // Devolver 200 OK con los datos de inventario
                Response.StatusCode = (int)HttpStatusCode.OK;
                return Json(new { exito = true, listadoInventario = inventarios }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                // Devolver 500 Internal Server Error si ocurre un error
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        public JsonResult InsertarCabeceraInventario(int idUsuario, DateTime fechaInicio)
        {
            try
            {
                // Llamar al procedimiento almacenado para insertar la cabecera
                int idInventario = acceso.InsertarCabeceraInventario(idUsuario, fechaInicio);

                // Validar si se insertó correctamente la cabecera
                if (idInventario > 0)
                {
                    // Devolver 200 OK con el ID del inventario creado
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, mensaje = "Cabecera del inventario insertada correctamente.", idInventario }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Error en la inserción
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "Error al insertar la cabecera del inventario." }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Devolver 500 Internal Server Error si ocurre un error
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }

        public JsonResult CargarInventarioSap(List<InventarioSapModel> datosInventario)
        {
            try
            {
                // Llama al método InsertarInventarioSap en el modelo Acceso
                bool resultado = acceso.InsertarInventarioSap(datosInventario);

                if (resultado)
                {
                    // Envía un código 200 OK si la inserción fue exitosa
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, mensaje = "Inventario SAP cargado correctamente" }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Envía un código 400 Bad Request si hubo un error en la inserción
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "Error al cargar el inventario SAP" }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Envía un código 500 Internal Server Error si ocurre una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = "Error interno: " + ex.Message }, JsonRequestBehavior.AllowGet);
            }
        }




        public JsonResult ObtenerCuadraturaDiferenciasCompleta(string codigoInventario)
        {
            try
            {
                // Obtener las diferencias completas del inventario especificado por el código
                var diferencias = acceso.ObtenerCuadraturaDiferenciasCompleta(codigoInventario);

                // Verificar si se encontraron diferencias
                if (diferencias.Count > 0)
                {
                    // Devolver éxito con código 200 OK si hay datos
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, datos = diferencias }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver solo el código 204 No Content si no hay datos
                    Response.StatusCode = (int)HttpStatusCode.NoContent;
                    return null;

                }
            }
            catch (Exception ex)
            {
                // Devolver error interno con código 500 Internal Server Error si ocurre una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }


        public JsonResult CargarInventarioFisico(List<InventarioFisicoModel> datosInventario)
        {
            try
            {
                // Llama al método del modelo para insertar los datos en Inventario Físico
                bool resultado = acceso.InsertarInventarioFisico(datosInventario);

                if (resultado)
                {
                    // Enviar un código 200 OK si la inserción fue exitosa
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, mensaje = "Inventario físico cargado correctamente" }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Enviar un código 400 Bad Request si hubo un error en la inserción
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "Error al cargar el inventario físico" }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                // Enviar un código 500 Internal Server Error si ocurre una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = "Error interno: " + ex.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public JsonResult EnviarCorreoFinalizacionInventario()
        {
            try
            {
                // Llamar al método del modelo para enviar el correo
                acceso.EnviarCorreoFinalizacionInventario();

                // Enviar un código 200 OK si el correo se envió correctamente
                Response.StatusCode = (int)HttpStatusCode.OK;
                return Json(new { exito = true, mensaje = "Correo de finalización de inventario enviado correctamente" }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                // Enviar un código 500 Internal Server Error si ocurrió un error
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = "Error interno: " + ex.Message }, JsonRequestBehavior.AllowGet);
            }
        }


        public JsonResult InsertarItemNoEncontrado(List<ItemNoEncontradoModel> itemsNoEncontrados)
        {
            try
            {
                bool resultado = acceso.InsertarItemNoEncontrado(itemsNoEncontrados);

                if (resultado)
                {
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, mensaje = "Ítems no encontrados insertados correctamente." }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "Error al insertar ítems no encontrados." }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = "Error interno: " + ex.Message }, JsonRequestBehavior.AllowGet);
            }
        }

        public JsonResult VerProductoCuadrado(string codigoInventario, string codigoProducto)
        {
            try
            {
                // Obtener los detalles del producto cuadrado
                var producto = acceso.ObtenerProductoCuadrado(codigoInventario, codigoProducto);

                if (producto != null)
                {
                    // Devolver éxito con código 200 OK si se encuentra el producto
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, datos = producto }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    // Devolver código 204 No Content si no se encuentra el producto
                    Response.StatusCode = (int)HttpStatusCode.NoContent;
                    return null;
                }
            }
            catch (Exception ex)
            {
                // Devolver error interno con código 500 Internal Server Error si ocurre una excepción
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }




        public JsonResult CuadrarProducto(string codigoInventario, string codigoProducto, int cantidadACuadrar, string descripcion, HttpPostedFileBase archivo)
        {
            try
            {
                if (archivo == null || archivo.ContentLength == 0)
                {
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "No se proporcionó un archivo válido." }, JsonRequestBehavior.AllowGet);
                }

                // Llamar al método del modelo para cuadrar el producto
                bool exito = acceso.CuadrarProducto(codigoInventario, codigoProducto, cantidadACuadrar, descripcion, archivo);

                if (exito)
                {
                    Response.StatusCode = (int)HttpStatusCode.OK;
                    return Json(new { exito = true, mensaje = "Producto cuadrado correctamente." }, JsonRequestBehavior.AllowGet);
                }
                else
                {
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "No se pudo cuadrar el producto." }, JsonRequestBehavior.AllowGet);
                }
            }
            catch (Exception ex)
            {
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = "Error interno: " + ex.Message }, JsonRequestBehavior.AllowGet);
            }
        }









        public JsonResult SubirArchivoSharePoint(int idDiferencia, HttpPostedFileBase archivo)
        {
            try
            {
                if (archivo == null || archivo.ContentLength == 0)
                {
                    Response.StatusCode = (int)HttpStatusCode.BadRequest;
                    return Json(new { exito = false, mensaje = "No se proporcionó un archivo válido." }, JsonRequestBehavior.AllowGet);
                }

                // Obtener el Stream del archivo
                Stream archivoStream = archivo.InputStream;
                string nombreArchivo = Path.GetFileName(archivo.FileName);

                // Definir la carpeta destino en SharePoint
                string carpetaDestino = "/sites/InformaticaGardilcic/Shared Documents/PDF_PRUEBAS_CIG_IGNACIO";

                // Subir archivo a SharePoint y guardar la ruta
                string rutaSharePoint = acceso.SubirArchivoStreamYGuardarRuta(idDiferencia, archivoStream, nombreArchivo, carpetaDestino);

                Response.StatusCode = (int)HttpStatusCode.OK;
                return Json(new { exito = true, mensaje = "Archivo subido y registrado correctamente.", rutaSharePoint }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception ex)
            {
                Response.StatusCode = (int)HttpStatusCode.InternalServerError;
                return Json(new { exito = false, mensaje = $"Error interno: {ex.Message}" }, JsonRequestBehavior.AllowGet);
            }
        }
        public ActionResult DescargarArchivoSharePoint(int idDiferencia)
        {
            try
            {
                // Obtener la ruta del archivo desde la base de datos
                string rutaArchivoSharePoint = acceso.ObtenerRutaArchivo(idDiferencia);

                if (string.IsNullOrEmpty(rutaArchivoSharePoint))
                {
                    return new HttpStatusCodeResult(404, "No se encontró una ruta asociada al ID especificado.");
                }

                // Descargar el archivo como un stream desde SharePoint
                Stream archivoStream = acceso.ObtenerStreamArchivoSharePoint(rutaArchivoSharePoint);
                string nombreArchivo = Path.GetFileName(rutaArchivoSharePoint);

                // Retornar el archivo como respuesta para descargarlo
                return File(archivoStream, "application/octet-stream", nombreArchivo);
            }
            catch (Exception ex)
            {
                return new HttpStatusCodeResult(500, $"Error al descargar archivo: {ex.Message}");
            }
        }
















    }
}
