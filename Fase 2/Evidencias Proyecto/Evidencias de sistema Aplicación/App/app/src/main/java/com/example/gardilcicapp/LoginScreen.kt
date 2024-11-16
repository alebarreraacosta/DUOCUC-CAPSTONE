package com.example.gardilcicapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gardilcicapp.R
import com.example.gardilcicapp.data.remote.ApiClient
import com.example.gardilcicapp.data.remote.ApiService
import com.example.gardilcicapp.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Composable
fun LoginScreen(
    navController: NavHostController,
    onLoginSuccess: (Int, String, String) -> Unit // Acepta IdUsuario, Nombre y ApellidoPaterno
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Fondo verde
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1b6d20)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_gardilcic),
                contentDescription = "Logo Gardilcic",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card de login
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(400.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ícono de usuario
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.size(120.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "User Icon",
                            tint = Color.Black,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de usuario
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "Email Icon",
                                tint = Color.Black
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Password Icon",
                                tint = Color.Black
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de login
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val usuario = loginUsuario(username, password)
                                if (usuario != null) {
                                    // Pasa IdUsuario, Nombre y ApellidoPaterno al callback
                                    onLoginSuccess(
                                        usuario.IdUsuario,
                                        usuario.Nombre,
                                        usuario.ApellidoPaterno
                                    )
                                    
                                    showSuccessDialog = true
                                } else {
                                    showErrorDialog = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300))
                    ) {
                        Text("Iniciar sesión", color = Color.White, fontSize = 20.sp)
                    }

                    // Diálogo de error
                    if (showErrorDialog) {
                        AlertDialog(
                            onDismissRequest = { showErrorDialog = false },
                            title = { Text("Error de autenticación") },
                            text = { Text("El usuario o la contraseña son incorrectos.") },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                                    onClick = { showErrorDialog = false }
                                ) {
                                    Text("Aceptar")
                                }
                            }
                        )
                    }

                    // Diálogo de éxito
                    if (showSuccessDialog) {
                        AlertDialog(
                            onDismissRequest = { showSuccessDialog = false },
                            title = { Text("Inicio de sesión exitoso") },
                            text = { Text("Te has logueado correctamente.") },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                                    onClick = {
                                        showSuccessDialog = true
                                        navController.navigate("import")
                                    }
                                ) {
                                    Text("Aceptar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// Función para autenticar al usuario
suspend fun loginUsuario(correo: String, contrasena: String): Usuario? {
    val apiService = ApiClient.retrofit.create(ApiService::class.java)
    return withContext(Dispatchers.IO) {
        try {
            val response = apiService.obtenerUsuario(correo, contrasena)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.exito && apiResponse.usuario != null) {
                    Log.d("Login", "Autenticación exitosa para el usuario: ${apiResponse.usuario.Nombre}")
                    return@withContext apiResponse.usuario
                }
            }
        } catch (e: Exception) {
            Log.e("Login", "Error: ${e.message}")
        }
        return@withContext null
    }
}
