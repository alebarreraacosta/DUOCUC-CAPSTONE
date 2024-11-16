package com.example.gardilcicapp

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gardilcicapp.R
import com.example.gardilcicapp.data.database.MyDatabaseHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArticleDetailsScreen(
    navController: NavHostController,
    usuarioNombre: String,
    usuarioApellidoPaterno: String,
    articleName: String,
    articleDescription: String,
    dbHelper: MyDatabaseHelper,  // Agregamos dbHelper como parámetro
    idUsuario: Int
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }

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

                Spacer(modifier = Modifier.height(50.dp))

                // Input para el nombre del artículo (no editable)
                TextField(
                    value = articleName,
                    onValueChange = {},
                    label = { Text("Nombre") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input para la descripción del artículo (no editable)
                TextField(
                    value = articleDescription,
                    onValueChange = {},
                    label = { Text("Descripción") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input para la cantidad (editable) con botones "+" y "-"
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(bottom = 8.dp)
                )

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

                Button(
                    onClick = {
                        // Inserta los datos en Inventario_Fisico
                        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                            Date()
                        )
                        dbHelper.insertInventarioFisico(
                            idInventario = 1,  // Asigna el ID de inventario según corresponda
                            idInventarioSAP = articleName,
                            cantidadFisica = quantity,
                            fecha = fechaActual,
                            idUsuario = idUsuario
                        )
                        // Navega a ProductListScreen
                        navController.navigate("productListScreen")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B)),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(55.dp)
                ) {
                    Text("Guardar", color = Color.White, fontSize = 20.sp)
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Botón Cancelar
                Button(
                    onClick = {
                        navController.popBackStack()  // Regresar a la pantalla anterior
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
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
                ) {
                    Text("No")
                }
            }
        )
    }
}
