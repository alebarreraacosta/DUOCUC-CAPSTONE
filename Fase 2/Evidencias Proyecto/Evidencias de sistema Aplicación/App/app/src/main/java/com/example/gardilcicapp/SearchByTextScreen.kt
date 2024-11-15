package com.example.gardilcicapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import kotlinx.coroutines.launch
import com.example.gardilcicapp.R
import com.example.gardilcicapp.data.database.MyDatabaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByTextScreen(
    navController: NavHostController,
    usuarioNombre: String,
    usuarioApellidoPaterno: String,
    dbHelper: MyDatabaseHelper
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var nullable = null
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Pair<String, String>?>(null) }
    // Función para buscar artículos
    fun searchArticles(query: String) {
        searchResults = if (query.isNotEmpty()) {
            dbHelper.searchArticles(query)
        } else {
            emptyList()
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

                Spacer(modifier = Modifier.height(30.dp))

                // Input de búsqueda con icono de lupa y botón de agregar al lado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            searchArticles(it) // Buscar cada vez que cambia el texto
                        },
                        label = { Text("Código o Descripción") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar", tint = Color.Gray)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(end = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Gray,
                            containerColor = Color.LightGray
                        )
                    )

                    IconButton(
                        onClick = { showAddProductDialog = true }, // Mostrar el diálogo al presionar el botón "+"
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF006400), CircleShape),
                        content = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Mostrar resultados de la búsqueda
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text("Código", color = Color(0xFF006400), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Descripción", color = Color(0xFF006400), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    searchResults.forEachIndexed { index, (codigo, descripcion) ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                selectedProduct = Pair(codigo, descripcion)
                                showConfirmDialog = true
                            }
                        ) {
                            Text(codigo, color = Color.Black)
                            Text(descripcion, color = Color.Black)
                        }
                        // Añadir un Divider entre cada fila de resultado
                        if (index < searchResults.size - 1) {
                            Divider(thickness = 1.dp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    )

    // Diálogo de confirmación al seleccionar un producto
    if (showConfirmDialog && selectedProduct != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("¿Desea agregar este producto?") },
            text = {
                Text("Producto seleccionado: ${selectedProduct?.first}, Descripción: ${selectedProduct?.second}")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        navController.navigate("articleDetails/${selectedProduct!!.first}/${selectedProduct!!.second}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B))
                ) {
                    Text("Sí", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("No", color = Color.White)
                }
            }
        )
    }

    // Mostrar el diálogo de agregar producto no registrado
    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Agregar un producto no registrado") },
            text = {
                Text("Recuerde que este producto no será inventariado.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddProductDialog = false
                        navController.navigate("registerNewProduct/$nullable") // Navegar a la pantalla registerNewProduct
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            }
        )
    }

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
}
