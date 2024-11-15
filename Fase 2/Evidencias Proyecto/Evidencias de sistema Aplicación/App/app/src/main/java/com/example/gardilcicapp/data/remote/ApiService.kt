package com.example.gardilcicapp.data.remote

// Archivo: /data/remote/ApiService.kt
import com.example.gardilcicapp.model.LoginResponse
import com.example.gardilcicapp.model.Usuario
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


data class InventoryRequest(
    val idUsuario: Int,
    val fechaInicio: String
)

data class InventoryResponse(
    val exito: Boolean,
    val mensaje: String,
    val idInventario: Int
)


data class InventarioSapItem(
    val NumeroArticulo: String,
    val DescripcionArticulo: String,
    val UnidadMedida: String,
    val StockAlmacen: Int,
    val PrecioUnitario: Float,
    val SaldoAlmacen: Int,
    val CodigoBarras: String,
    val CodigoAlmacen: String,
    val FechaCarga: String
)

data class InventarioSapResponse(
    val exito: Boolean,
    val mensaje: String
)


data class InventarioFisicoItem(
    val CantidadFisica: Int,
    val Fecha: String,
    val IdUsuario: Int,
    val NumeroArticulo: String
)

data class InventarioFisicoResponse(
    val exito: Boolean,
    val mensaje: String
)


data class ItemNoEncontradoRequest(
    val NumeroArticulo: String,
    val CodigoBarras: String,
    val Descripcion: String,
    val CantidadContada: Int,
    val CodigoAlmacen: String,
    val Fecha: String,
    val IdUsuario: Int
)

data class ItemNoEncontradoResponse(
    val exito: Boolean,
    val mensaje: String
)

data class CorreoFinalizacionResponse(
    val exito: Boolean,
    val mensaje: String
)




interface ApiService {
    @GET("Acceso/ObtenerUsuario")
    suspend fun obtenerUsuario(
        @Query("correo") correo: String,
        @Query("contrasena") contrasena: String
    ): Response<LoginResponse> // Cambiado a ApiResponse

    // Método para insertar cabecera de inventario
    @POST("Acceso/InsertarCabeceraInventario")
    suspend fun insertarCabeceraInventario(
        @Body request: InventoryRequest
    ): Response<InventoryResponse>

    // Método para cargar inventario SAP
    @POST("Acceso/CargarInventarioSap")
    suspend fun cargarInventarioSap(
        @Body items: List<InventarioSapItem>
    ): Response<InventarioSapResponse>

    @POST("Acceso/CargarInventarioFisico")
    suspend fun cargarInventarioFisico(
        @Body items: List<InventarioFisicoItem>
    ): Response<InventarioFisicoResponse>


    @POST("Acceso/InsertarItemNoEncontrado")
    suspend fun insertarItemNoEncontrado(
        @Body items: List<ItemNoEncontradoRequest>
    ): Response<ItemNoEncontradoResponse>

    @POST("Acceso/EnviarCorreoFinalizacionInventario")
    suspend fun enviarCorreoFinalizacionInventario(): Response<CorreoFinalizacionResponse>
}
