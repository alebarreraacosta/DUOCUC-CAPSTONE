using Microsoft.SharePoint.Client;
using System;
using System.IO;
using System.Security;
using System.Web;

public class SharePointHelper
{
    public string SubirArchivoSharePoint(string rutaArchivoLocal, string carpetaDestino)
    {
        string sitioSharePoint = "https://grupogardilcic.sharepoint.com/sites/InformaticaGardilcic";
        string usuario = "notificaciones@gardilcic.cl";
        string contrasena = "Ccfrfon1";

        try
        {
            using (var cliente = new ClientContext(sitioSharePoint))
            {
                // Configurar credenciales
                SecureString contrasenaSegura = new SecureString();
                foreach (char c in contrasena) { contrasenaSegura.AppendChar(c); }
                cliente.Credentials = new SharePointOnlineCredentials(usuario, contrasenaSegura);

                // Abrir archivo
                using (var stream = new FileStream(rutaArchivoLocal, FileMode.Open))
                {
                    var nombreArchivo = Path.GetFileName(rutaArchivoLocal);
                    var carpeta = cliente.Web.GetFolderByServerRelativeUrl(carpetaDestino);

                    // Crear archivo en SharePoint
                    FileCreationInformation archivoNuevo = new FileCreationInformation
                    {
                        ContentStream = stream,
                        Url = nombreArchivo,
                        Overwrite = true
                    };

                    var archivoSubido = carpeta.Files.Add(archivoNuevo);
                    cliente.Load(archivoSubido);
                    cliente.ExecuteQuery();

                    return carpetaDestino + "/" + nombreArchivo; // Retornar la URL del archivo
                }
            }
        }
        catch (Exception ex)
        {
            throw new Exception("Error al subir el archivo a SharePoint: " + ex.Message);
        }
    }
}
