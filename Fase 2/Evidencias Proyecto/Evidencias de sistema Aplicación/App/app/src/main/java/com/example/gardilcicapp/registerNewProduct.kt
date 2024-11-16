package com.example.gardilcicapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.example.gardilcicapp.data.database.MyDatabaseHelper


@Composable
fun RegisterProductScreen(
    navController: NavHostController,
    usuarioNombre: String,
    usuarioApellidoPaterno: String,
    dbHelper: MyDatabaseHelper,
    idUsuario: Int,
    barcode: String // Parámetro para el código de barras
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }

    // Estado para el código de barras, inicializado con el valor recibido
    var codigoBarras by remember { mutableStateOf("") }

    // Asegúrate de actualizar `codigoBarras` cuando `barcode` cambie
    LaunchedEffect(barcode) {
        codigoBarras = barcode
        Log.d("RegisterProductScreen", "Código de barras recibido en LaunchedEffect: $codigoBarras")
    }

    // Función para manejar el registro del producto no encontrado
    fun registerProduct() {
        val idInventario = dbHelper.getMaxInventarioId()

        // Agregar un Log para verificar el valor de codigoBarras antes de insertar
        Log.d("RegisterProductScreen", "Valor de codigoBarras antes de insertar: $codigoBarras")

        val result = dbHelper.insertItemNoEncontrado(
            idInventario = idInventario,
            numeroArticulo = productName,
            descripcion = productDescription,
            cantidadContada = quantity,
            idUsuario = idUsuario,
            codigoBarras = codigoBarras // Guardar el código de barras
        )

        if (result != -1L) {
            resultMessage = "Registro exitoso"
            isSuccess = true
        } else {
            resultMessage = "Error al registrar el producto"
            isSuccess = false
        }
        showResultDialog = true
    }

    // Log para verificar después de LaunchedEffect
    Log.d("RegisterProductScreen", "Código de barras recibido después de LaunchedEffect: $codigoBarras")

    TextField(
        value = codigoBarras,
        onValueChange = { codigoBarras = it },
        label = { Text("Código de Barras") },
        modifier = Modifier.fillMaxWidth(0.9f),
        enabled = false // Para que solo se muestre, no editable
    )

    Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(50.dp))


                TextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = productDescription,
                    onValueChange = { productDescription = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de cantidad con botones "+" y "-"
                Text("Cantidad", modifier = Modifier.align(Alignment.CenterHorizontally))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.LightGray, RoundedCornerShape(50))
                        .padding(8.dp)
                ) {
                    IconButton(onClick = {
                        if (quantity > 1) quantity--
                    }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Disminuir cantidad")
                    }

                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    IconButton(onClick = { quantity++ }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Aumentar cantidad")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Registrar
                Button(
                    onClick = { registerProduct() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B)),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(55.dp)
                ) {
                    Text("Registrar", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Cancelar
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(55.dp)
                ) {
                    Text("Cancelar", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    )

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
                Button(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // Diálogo de resultado de registro
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text(resultMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showResultDialog = false
                        if (isSuccess) {
                            navController.navigate("inventoryOptionsScreen") // Navega a optionsScreen si es exitoso
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            }
        )
    }
}
