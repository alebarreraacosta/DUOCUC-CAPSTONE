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
fun LoginScreen(navController: NavHostController, onLoginSuccess: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) } // Para mostrar el diálogo emergente de error
    var showSuccessDialog by remember { mutableStateOf(false) } // Para mostrar el diálogo de éxito
    val coroutineScope = rememberCoroutineScope()

    // Background color con el color #1b6d20
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
            // Logo más grande
            Image(
                painter = painterResource(id = R.drawable.logo_gardilcic),
                contentDescription = "Logo Gardilcic",
                modifier = Modifier.size(300.dp) // Aumenta el tamaño del logo
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Card que ahora incluye el ícono de usuario
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
                    // Ícono de usuario más grande y dentro de la Card
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

                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "Email Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Password Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button con funcionalidad de autenticación
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val usuario = loginUsuario(username, password)
                                if (usuario != null) {
                                    onLoginSuccess(usuario.Nombre, usuario.ApellidoPaterno)
                                    showSuccessDialog = true // Mostrar el diálogo de éxito
                                } else {
                                    showErrorDialog = true // Mostrar el diálogo emergente si falló el login
                                }
                            }
                            //navController.navigate("import")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300))
                    ) {
                        Text("Iniciar sesión", color = Color.White, fontSize = 20.sp)
                    }

                    // Mostrar un diálogo emergente si las credenciales son incorrectas
                    if (showErrorDialog) {
                        AlertDialog(
                            onDismissRequest = { showErrorDialog = false }, // Cerrar el diálogo si se toca fuera de él
                            title = { Text("Error de autenticación") },
                            text = { Text("El correo o la contraseña son incorrectos.") },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                                    onClick = { showErrorDialog = false } // Cerrar el diálogo
                                ) {
                                    Text("Aceptar")
                                }
                            }
                        )
                    }

                    // Mostrar un diálogo emergente si el inicio de sesión fue exitoso
                    if (showSuccessDialog) {
                        AlertDialog(
                            onDismissRequest = { showSuccessDialog = false }, // Cerrar el diálogo si se toca fuera de él
                            title = { Text("Inicio de sesión exitoso") },
                            text = { Text("Te has logueado correctamente.") },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                                    onClick = {
                                        showSuccessDialog = false
                                        navController.navigate("import") // Navegar a ImportScreen cuando se acepte el diálogo
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


// Función para llamar a la API de autenticación
suspend fun loginUsuario(correo: String, contrasena: String): Usuario? {
    val apiService = ApiClient.retrofit.create(ApiService::class.java)

    return withContext(Dispatchers.IO) {
        try {
            val response = apiService.obtenerUsuario(correo, contrasena)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                // Si la autenticación es exitosa, devolvemos el usuario
                if (apiResponse != null && apiResponse.exito && apiResponse.usuario != null) {
                    Log.d("Login", "Autenticación exitosa para el usuario: ${apiResponse.usuario.Nombre}")
                    return@withContext apiResponse.usuario // Devuelve el usuario si la autenticación es exitosa
                } else {
                    Log.d("Login", "Autenticación fallida: Usuario no encontrado o datos incorrectos.")
                    return@withContext null
                }
            } else {
                Log.d("Login", "Error en la respuesta de la API: ${response.code()} - ${response.message()}")
                return@withContext null
            }
        } catch (e: HttpException) {
            Log.e("Login", "Error HttpException: ${e.message}")
            return@withContext null
        } catch (e: Exception) {
            Log.e("Login", "Error general: ${e.message}")
            return@withContext null
        }
    }
}
