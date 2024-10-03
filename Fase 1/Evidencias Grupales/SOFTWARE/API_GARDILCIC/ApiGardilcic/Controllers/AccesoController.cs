using ApiGardilcic.Models;
using System.Net;
using System;
using System.Web.Mvc;

namespace ApiGardilcic.Controllers
{
    public class AccesoController : Controller
    {
        Acceso acceso = new Acceso();

        // Método para obtener y validar usuario (simulación de login)
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

        // Método para crear un nuevo usuario
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

        // Método para actualizar un usuario
        public JsonResult ActualizarUsuario(int idUsuario, string nombre, string correo, string apellidoPaterno, string apellidoMaterno, int idRol)
        {
            try
            {
                // Intentar actualizar el usuario usando los parámetros directamente
                bool actualizado = acceso.ActualizarUsuario(idUsuario, nombre, correo, apellidoPaterno, apellidoMaterno, idRol);

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

        // Método para eliminar un usuario
        public JsonResult EliminarUsuario(int idUsuario)
        {
            try
            {
                // Intentar eliminar el usuario
                bool eliminado = acceso.EliminarUsuario(idUsuario);

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
    }
}

