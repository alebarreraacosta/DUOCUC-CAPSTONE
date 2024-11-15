package com.example.gardilcicapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gardilcicapp.data.database.MyDatabaseHelper
import com.example.gardilcicapp.data.remote.ApiClient
import com.example.gardilcicapp.data.remote.ApiService
import com.example.gardilcicapp.data.remote.InventoryRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavHostController,
    usuarioNombre: String,
    usuarioApellidoPaterno: String,
    dbHelper: MyDatabaseHelper
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    val productList = remember { dbHelper.getInventarioFisico() } // Obtén los productos de la base de datos
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }



    val apiService = ApiClient.retrofit.create(ApiService::class.java)

    // Función para finalizar inventario
    fun finalizarInventario() {
        scope.launch {
            try {
                // Obtener el primer registro de la tabla inventario
                val inventario = dbHelper.getLatestInventario()
                if (inventario != null) {
                    // Extraer id_usuario y fecha_inicio de la tabla inventario
                    val idUsuario = inventario[MyDatabaseHelper.COLUMN_ID_USUARIO_NEW] as? Int ?: run {
                        successMessage = "ID de usuario no encontrado en la tabla Inventario."
                        showSuccessDialog = true
                        return@launch
                    }
                    val fechaInicio = inventario[MyDatabaseHelper.COLUMN_FECHA_INICIO] as? String ?: run {
                        successMessage = "Fecha de inicio no encontrada en la tabla Inventario."
                        showSuccessDialog = true
                        return@launch
                    }
                    Log.d("FinalizarInventario", "Datos enviados: ID Usuario: $idUsuario, Fecha Inicio: $fechaInicio")
                    val inventario = dbHelper.getLatestInventario()
                    if (inventario == null) {
                        Log.d("Inventario", "No se encontraron registros en la tabla Inventario local.")
                    }
                    Log.d("FinalizarInventario", "ID Usuario: $idUsuario, Fecha Inicio: $fechaInicio")

                    // Crear la solicitud para la API
                    val request = InventoryRequest(idUsuario = idUsuario, fechaInicio = fechaInicio)
                    Log.d("FinalizarInventario", "Enviando solicitud a la API con datos: $request")
                    Log.d("FinalizarInventario", "ID de usuario: $idUsuario, Fecha de inicio: $fechaInicio")

                    // Llamada a la API
                    val response = apiService.insertarCabeceraInventario(request)

                    if (response.isSuccessful && response.body()?.exito == true) {
                        successMessage = "Inventario finalizado con éxito: ${response.body()?.mensaje}"
                        // Aquí puedes incluir lógica adicional si es necesario
                    } else {
                        successMessage = "Error al finalizar inventario: ${response.body()?.mensaje ?: "Respuesta fallida"}"
                    }
                    showSuccessDialog = true
                } else {
                    successMessage = "Inventario no encontrado en la base de datos."
                    showSuccessDialog = true
                    return@launch
                }
                // metodo insertar inventario_sap
                val inventarioSapData = dbHelper.getInventarioSapData()
                val sapResponse = apiService.cargarInventarioSap(inventarioSapData)

                if (sapResponse.isSuccessful && sapResponse.body()?.exito == true) {
                    successMessage = "Inventario SAP cargado correctamente: ${sapResponse.body()?.mensaje}"
                } else {
                    successMessage = "Error al cargar Inventario SAP: ${sapResponse.body()?.mensaje ?: "Error desconocido"}"
                }

                //Enviar detalles del inventario físico
                val inventarioFisicoData = dbHelper.getInventarioFisicoData()
                val fisicoResponse = apiService.cargarInventarioFisico(inventarioFisicoData)

                if (fisicoResponse.isSuccessful && fisicoResponse.body()?.exito == true) {
                    successMessage = "Inventario físico: ${fisicoResponse.body()?.mensaje}"
                } else {
                    successMessage = "Error al cargar Inventario físico: ${fisicoResponse.body()?.mensaje ?: "Error desconocido"}"
                }
                // Enviar detalles de los ítems no encontrados
                val itemsNoEncontradosData = dbHelper.getItemsNoEncontradosData()
                val noEncontradosResponse = apiService.insertarItemNoEncontrado(itemsNoEncontradosData)

                if (noEncontradosResponse.isSuccessful && noEncontradosResponse.body()?.exito == true) {
                    successMessage = "Ítems no encontrados cargados correctamente: ${noEncontradosResponse.body()?.mensaje}"
                } else {
                    successMessage = "Error al cargar ítems no encontrados: ${noEncontradosResponse.body()?.mensaje ?: "Error desconocido"}"
                }

                // Enviar correo de finalización
                val correoResponse = apiService.enviarCorreoFinalizacionInventario()

                if (correoResponse.isSuccessful && correoResponse.body()?.exito == true) {
                    successMessage = "Correo enviado: ${correoResponse.body()?.mensaje}"
                } else {
                    successMessage = "Error al enviar correo de finalización: ${correoResponse.body()?.mensaje ?: "Error desconocido"}"
                }


                showSuccessDialog = true

            } catch (e: Exception) {
                Log.e("FinalizarInventario", "Error en la llamada a la API: ${e.message}", e)
                successMessage = "Error al finalizar inventario: ${e.message}"
                showSuccessDialog = true
            }
        }
    }




    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Menú lateral
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .background(Color(0xFF006400))
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Usuario",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$usuarioNombre $usuarioApellidoPaterno",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar Sesión", color = Color.White)
                    }
                }
            }
        },
        content = {
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Barra superior
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFF006400))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_gardilcic),
                        contentDescription = "Logo Gardilcic",
                        modifier = Modifier.size(190.dp)
                    )

                    IconButton(onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Productos ingresados",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006400)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Encabezado de la tabla
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray, RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Código", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Cantidad", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Contenido de la tabla con los productos
                productList.forEach { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(product[MyDatabaseHelper.COLUMN_ID_INVENTARIO_SAP].toString(), fontSize = 16.sp)
                        Text(product[MyDatabaseHelper.COLUMN_CANTIDAD_FISICA].toString(), fontSize = 16.sp)
                    }
                    // Divider entre filas
                    Divider(thickness = 1.dp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Botones de la parte inferior
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showCancelDialog = true }, // Mostrar diálogo de confirmación
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar operación", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { navController.navigate("inventoryOptionsScreen") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Continuar inventario", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { finalizarInventario() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Finalizar inventario", color = Color.White)
                    }
                }
            }
        }
    )

    // Diálogo de confirmación para cancelar operación
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("¿Estás seguro de que deseas cancelar la operación?") },
            text = { Text("Esta acción eliminará todos los registros previos.") },
            confirmButton = {
                Button(
                    onClick = {
                        dbHelper.truncateInventarioFisico() // Ejecutar el truncate
                        dbHelper.truncateItemsNoEncontrados()
                        dbHelper.truncateInventario()
                        dbHelper.truncateInventarioSAP()
                        showCancelDialog = false
                        navController.navigate("import") // Redirigir a ImportScreen
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B))
                ) {
                    Text("Sí", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCancelDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("No", color = Color.White)
                }
            }
        )
    }


    // Diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Cerrar sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        navController.navigate("login")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Finalización del Inventario") },
            text = { Text(successMessage) },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}










