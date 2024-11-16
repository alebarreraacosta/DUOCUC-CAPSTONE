package com.example.gardilcicapp.model


// Para representar la respuesta completa
data class LoginResponse(
    val exito: Boolean,
    val usuario: Usuario?  // Usuario puede ser null si no existe en la respuesta
)

data class Usuario(
    val IdUsuario: Int,
    val Nombre: String,
    val Correo: String,
    val ApellidoPaterno: String,
    val ApellidoMaterno: String,
    val IdRol: Int
)
