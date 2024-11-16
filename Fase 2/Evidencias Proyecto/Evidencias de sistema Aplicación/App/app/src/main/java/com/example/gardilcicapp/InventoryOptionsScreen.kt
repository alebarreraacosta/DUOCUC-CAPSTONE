package com.example.gardilcicapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gardilcicapp.data.database.MyDatabaseHelper
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch

@Composable
fun InventoryOptionsScreen(
    navController: NavHostController,
    usuarioNombre: String,  // Recibir el nombre del usuario
    usuarioApellidoPaterno: String,  // Recibir el apellido paterno del usuario
    dbHelper: MyDatabaseHelper  // Pasar el helper de base de datos para consultas
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var showBarcodesDialog by remember { mutableStateOf(false) }
    var barcodes by remember { mutableStateOf(listOf<String>()) }

    // Inicializa el lanzador de la cámara para tomar una foto y escanear el código de barras
    val barcodeScannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            scanBarcodeFromCamera(bitmap, context, dbHelper) { result ->
                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "No se capturó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Seleccionador de imágenes
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri // Guardar la URI seleccionada
            scanBarcodeFromImage(context, uri, dbHelper) { result ->
                resultMessage = result
                showResultDialog = true
            }
        } else {
            Toast.makeText(context, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador para solicitar permisos de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            //barcodeScannerLauncher.launch(null)
            navController.navigate("BarcodeScannerScreen")
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Contenido de la pantalla
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

                Spacer(modifier = Modifier.height(250.dp))

                // Botón "Escanear producto con imagen"
                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                ) {
                    Text("Escanear producto con imagen", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Botón "Escanear producto con cámara"
                Button(
                    onClick = {
                        // Verificar si ya tiene permiso de la cámara
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                ) {
                    Text("Escanear producto con cámara", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(50.dp))
                // boton de busqueda por escritura
                Button(
                    onClick = {
                        navController.navigate("searchByText")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                ) {
                    Text("Búsqueda por escritura", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(50.dp))

                Button(
                    onClick = {
                        barcodes = dbHelper.getItemsNoEncontrados() // Llama a la función para obtener los datos de la tabla
                        showBarcodesDialog = true // Muestra el diálogo con los datos
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                ) {
                    Text("Mostrar Items No Encontrados", color = Color.White, fontSize = 20.sp)
                }


            }
        }
    )

    // Mostrar el resultado en un diálogo
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text(text = "Resultado del escaneo") },
            text = { Text(resultMessage) },
            confirmButton = {
                Button(
                    onClick = { showResultDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            }
        )
    }

    // Diálogo para mostrar los códigos de barras
    if (showBarcodesDialog) {
        AlertDialog(
            onDismissRequest = { showBarcodesDialog = false },
            title = { Text(text = "Códigos de Barras") },
            text = {
                Column {
                    barcodes.forEach { barcode ->
                        Text(text = barcode)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showBarcodesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300))
                ) {
                    Text("Cerrar", color = Color.White)
                }
            }
        )
    }

    // Di
    // Diálogo para mostrar los códigos de barras
    if (showBarcodesDialog) {
        AlertDialog(
            onDismissRequest = { showBarcodesDialog = false },
            title = { Text(text = "Códigos de Barras") },
            text = {
                Column {
                    barcodes.forEach { barcode ->
                        Text(text = barcode)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showBarcodesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300))
                ) {
                    Text("Cerrar", color = Color.White)
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
    // Diálogo para mostrar los items no encontrados
    if (showBarcodesDialog) {
        AlertDialog(
            onDismissRequest = { showBarcodesDialog = false },
            title = { Text(text = "Items No Encontrados") },
            text = {
                Column {
                    if (barcodes.isNotEmpty()) {
                        barcodes.forEach { item ->
                            Text(text = item)
                            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre cada ítem
                        }
                    } else {
                        Text("No se encontraron items en la tabla.")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showBarcodesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300))
                ) {
                    Text("Cerrar", color = Color.White)
                }
            }
        )
    }

}


// Función para procesar el Bitmap capturado por la cámara y detectar el código de barras
fun scanBarcodeFromCamera(bitmap: Bitmap, context: Context, dbHelper: MyDatabaseHelper, onResult: (String) -> Unit) {
    val inputImage = InputImage.fromBitmap(bitmap, 0)
    val scanner = BarcodeScanning.getClient()

    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            val barcode = barcodes.firstOrNull()?.rawValue
            if (barcode != null) {
                Log.d("Database", "Código escaneado: $barcode")
                // Consultar si el código existe en la base de datos
                val exists = dbHelper.checkBarcodeExists(barcode.trim())
                if (exists) {
                    onResult("Código de barras encontrado: $barcode")
                } else {
                    onResult("El código de barras no existe en la base de datos")
                }
            } else {
                onResult("No se detectó ningún código de barras")
            }
        }
        .addOnFailureListener {
            onResult("Error al escanear el código de barras")
        }
}

// Método en la clase `MyDatabaseHelper` para obtener los códigos de barras
fun MyDatabaseHelper.getBarcodes(): List<String> {
    val db = this.readableDatabase
    val barcodes = mutableListOf<String>()
    val query = "SELECT codigo_barras FROM inventario_sap WHERE codigo_barras IS NOT NULL AND codigo_barras != ''"
    val cursor = db.rawQuery(query, null)

    if (cursor.moveToFirst()) {
        do {
            val barcode = cursor.getString(cursor.getColumnIndexOrThrow("codigo_barras"))
            barcodes.add(barcode)
        } while (cursor.moveToNext())
    }

    cursor.close()
    return barcodes
}



@Composable
fun ScanProductScreen(
    navController: NavHostController,
    onScanResult: (String) -> Unit, // Función para manejar el resultado del escaneo
    dbHelper: MyDatabaseHelper // Pasar el helper de base de datos para consultar
) {
    val context = LocalContext.current

    val barcodeScannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // Procesar la imagen para escanear el código de barras
            scanBarcodeFromBitmap(bitmap) { scannedCode ->
                if (scannedCode != null) {
                    // Validar el código escaneado en la base de datos
                    val isValidProduct = dbHelper.checkBarcodeExists(scannedCode)
                    if (isValidProduct) {
                        onScanResult(scannedCode)
                        Toast.makeText(context, "Producto reconocido: $scannedCode", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Producto no reconocido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No se detectó ningún código de barras", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Escanear Producto", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { barcodeScannerLauncher.launch(null) }) {
            Text("Iniciar escaneo")
        }
    }
}

// Función para procesar el Bitmap y detectar el código de barras
fun scanBarcodeFromBitmap(bitmap: Bitmap, onResult: (String?) -> Unit) {
    val inputImage = InputImage.fromBitmap(bitmap, 0)
    val scanner = BarcodeScanning.getClient()

    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            // Procesar los códigos de barras
            val barcode = barcodes.firstOrNull()
            onResult(barcode?.displayValue)
        }
        .addOnFailureListener {
            onResult(null)
        }
}


@Composable
fun RequestCameraPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
}









// Función para escanear el código de barras desde una imagen y consultar en la BD
fun scanBarcodeFromImage(context: Context, imageUri: Uri, dbHelper: MyDatabaseHelper, onResult: (String) -> Unit) {
    val inputStream = context.contentResolver.openInputStream(imageUri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    val barcodeScanner = BarcodeScanning.getClient()
    val image = InputImage.fromBitmap(bitmap, 0)

    barcodeScanner.process(image)
        .addOnSuccessListener { barcodes ->
            val barcode = barcodes.firstOrNull()?.rawValue
            if (barcode != null) {
                Log.d("Database", "Código escaneado: $barcode")
                // Consultar si el código existe en la base de datos
                val exists = dbHelper.checkBarcodeExists(barcode.trim())  // Usar trim() para eliminar espacios en blanco

                if (exists) {
                    onResult("Código de barras encontrado: $barcode")
                } else {
                    onResult("El código de barras no existe en la base de datos")
                }
            } else {
                onResult("No se detectó ningún código de barras")
            }
        }
        .addOnFailureListener {
            onResult("Error al escanear el código de barras")
        }
}

fun launchCamera(barcodeScannerLauncher: ManagedActivityResultLauncher<Uri?, Bitmap?>, context: Context) {
    try {
        barcodeScannerLauncher.launch(null)
    } catch (e: Exception) {
        Toast.makeText(context, "Error al intentar abrir la cámara", Toast.LENGTH_SHORT).show()
    }
}








// Método en la clase `MyDatabaseHelper` para obtener los códigos de barras
/*fun MyDatabaseHelper.getBarcodes(): List<String> {
    val db = this.readableDatabase
    val barcodes = mutableListOf<String>()
    val query = "SELECT codigo_barras FROM inventario_sap WHERE codigo_barras IS NOT NULL AND codigo_barras != ''"
    val cursor = db.rawQuery(query, null)

    if (cursor.moveToFirst()) {
        do {
            val barcode = cursor.getString(cursor.getColumnIndexOrThrow("codigo_barras"))
            barcodes.add(barcode)
        } while (cursor.moveToNext())
    }

    cursor.close()
    return barcodes
}*/




