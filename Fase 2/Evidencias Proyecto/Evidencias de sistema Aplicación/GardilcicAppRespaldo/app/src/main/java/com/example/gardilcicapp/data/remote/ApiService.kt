package com.example.gardilcicapp.data.remote

// Archivo: /data/remote/ApiService.kt
import com.example.gardilcicapp.model.LoginResponse
import com.example.gardilcicapp.model.Usuario
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface ApiService {
    @GET("Acceso/ObtenerUsuario")
    suspend fun obtenerUsuario(
        @Query("correo") correo: String,
        @Query("contrasena") contrasena: String
    ): Response<LoginResponse> // Cambiado a ApiResponse
}
