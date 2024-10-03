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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.example.gardilcicapp.R



@Composable
fun inventoryScreen(
    navController: NavHostController,
    fileName: String?,
    excelData: List<List<String>>?,
    usuarioNombre: String,  // Recibir el nombre del usuario
    usuarioApellidoPaterno: String  // Recibir el apellido paterno del usuario
) {
    // Asegurarse de que el nombre del archivo se muestra correctamente
    val displayFileName = fileName ?: "No se adjuntó archivo" // Usar directamente fileName que ya incluye la extensión

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showExcelDataDialog by remember { mutableStateOf(false) } // Estado para mostrar ventana emergente

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

                    // Información del usuario
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
                            text = "$usuarioNombre $usuarioApellidoPaterno", // Mostrar nombre completo
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de cerrar sesión
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
            // Contenido principal con barra superior
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Barra superior con ícono de menú
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFF006400))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo_gardilcic),
                        contentDescription = "Logo Gardilcic",
                        modifier = Modifier.size(190.dp)
                    )

                    // Ícono del menú
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

                Spacer(modifier = Modifier.height(300.dp))

                // Ícono de Excel y el nombre del archivo
                Image(
                    painter = painterResource(id = R.drawable.icons_xls),
                    contentDescription = "Icono Excel",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar el nombre completo del archivo con extensión
                Text(text = displayFileName, color = Color.Gray, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(50.dp))

                Spacer(modifier = Modifier.height(10.dp))

                // Botón "Comenzar Inventario" con color #FF7300
                Button(
                    onClick = {
                        showExcelDataDialog = true // Mostrar el diálogo con el mensaje de estado de la lectura del Excel
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                ) {
                    Text("Comenzar Inventario", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botón de Cancelar que regresa a la pantalla ImportScreen
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF04124)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                ) {
                    Text("Cancelar", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    )

    // Mostrar mensaje de estado del archivo Excel en una ventana emergente
    if (showExcelDataDialog) {
        AlertDialog(
            onDismissRequest = { showExcelDataDialog = false },
            title = { Text(text = "Estado del archivo Excel") },
            text = {
                if (excelData.isNullOrEmpty()) {
                    Text("Datos no leídos correctamente")
                } else {
                    Text("Archivo Excel leído correctamente")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExcelDataDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300))
                ) {
                    Text("Aceptar", color = Color.White)
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
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
                ) {
                    Text("No")
                }
            }
        )
    }
}


// Función para extraer el nombre del archivo desde la ruta completa
fun getFileNameFromPath(filePath: String): String {
    return filePath.substringAfterLast('/')
}
