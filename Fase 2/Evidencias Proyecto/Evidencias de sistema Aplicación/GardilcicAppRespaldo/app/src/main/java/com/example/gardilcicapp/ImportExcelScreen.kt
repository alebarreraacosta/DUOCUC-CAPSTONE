import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import kotlinx.coroutines.launch
import com.example.gardilcicapp.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gardilcicapp.data.database.MyDatabaseHelper
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImportScreen(
    navController: NavHostController,
    usuarioNombre: String,
    usuarioApellidoPaterno: String,
    onExcelDataRead: (List<List<String>>) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isFileSelected by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var excelData by remember { mutableStateOf<List<List<String>>?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }  // Para manejar la animación de carga
    var showSuccessDialog by remember { mutableStateOf(false) }  // Para mostrar éxito o error
    var successMessage by remember { mutableStateOf("") }  // Mensaje para mostrar en el diálogo

    val context = LocalContext.current
    val dbHelper = MyDatabaseHelper(context)

    // Obtener la fecha actual
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // File picker launcher
    val excelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        uri?.let {
            // Obtener el nombre completo del archivo
            selectedFileName = getFileNameFromUri(context, it) ?: "Archivo Excel"
            isFileSelected = true
            excelData = readExcelFileFromUri(context, it)

            // Mostrar animación de carga mientras se procesan los datos
            isLoading = true

            // Procesar datos del Excel en la base de datos
            scope.launch {
                try {
                    dbHelper.truncateInventarioSAP()

                    excelData?.drop(1)?.forEach { rowData -> // drop(1) para ignorar la cabecera
                        if (rowData.size >= 8) {
                            val idInventario = rowData[0].toIntOrNull() ?: 0
                            val numeroArticulo = rowData[1]
                            val descripcionArticulo = rowData[2]
                            val unidadMedida = rowData[3]
                            val stockAlmacen = rowData[4].toIntOrNull() ?: 0
                            val precioUnitario = rowData[5].toFloatOrNull() ?: 0.0f
                            val saldoAlmacen = rowData[6].toIntOrNull() ?: 0
                            val codigoBarras = rowData[7]
                            val codigoAlmacen = rowData[8]

                            val result = dbHelper.insertInventarioSAP(
                                idInventario = idInventario,
                                numeroArticulo = numeroArticulo,
                                descripcionArticulo = descripcionArticulo,
                                unidadMedida = unidadMedida,
                                stockAlmacen = stockAlmacen,
                                precioUnitario = precioUnitario,
                                saldoAlmacen = saldoAlmacen,
                                codigoBarras = codigoBarras,
                                codigoAlmacen = codigoAlmacen,
                                fechaCarga = currentDate
                            )

                            if (result == -1L) {
                                throw Exception("Error al insertar los datos")
                            }
                        }
                    }
                    // Si todo fue exitoso
                    successMessage = "Datos insertados correctamente"
                } catch (e: Exception) {
                    successMessage = "Error al insertar datos: ${e.message}"
                } finally {
                    isLoading = false  // Ocultar el indicador de carga
                    showSuccessDialog = true  // Mostrar el mensaje de éxito o error
                }
            }
            onExcelDataRead(excelData ?: emptyList())
        } ?: run {
            Toast.makeText(context, "Error al seleccionar el archivo", Toast.LENGTH_SHORT).show()
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

                Spacer(modifier = Modifier.height(300.dp))

                Image(
                    painter = painterResource(id = R.drawable.icons_xls),
                    contentDescription = "Icono Excel",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                // Botón para seleccionar archivo Excel
                Button(
                    onClick = { excelPickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(0.8f).height(55.dp)
                ) {
                    Text("Seleccionar archivo Excel", color = Color.White, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Comenzar Inventario
                Button(
                    onClick = {
                        if (selectedFileName.isNotEmpty()) {
                            navController.navigate("inventoryScreen/${Uri.encode(selectedFileName)}")
                        }
                    },
                    enabled = isFileSelected,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth(0.8f).height(55.dp)
                ) {
                    Text("Comenzar inventario", color = Color.White, fontSize = 20.sp)
                }

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Mostrar Inventario")
                }

                // Mostrar el diálogo solo si showDialog es true
                if (showDialog) {
                    showInventoryDataDialog(context, dbHelper) {
                        showDialog = false
                    }
                }

                // Mostrar animación de carga si isLoading es true
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF7300),
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    )

    // Mostrar el éxito o error en un diálogo emergente
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Resultado de la operación") },
            text = { Text(successMessage) },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7300)),) {
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
                Button(
                    onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}



// Función para obtener el nombre del archivo desde su URI
fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var fileName: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            fileName = it.getString(it.getColumnIndexOrThrow("_display_name"))
        }
    }
    return fileName
}

fun readExcelFileFromUri(context: Context, uri: Uri): List<List<String>> {
    val data = mutableListOf<List<String>>()

    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val workbook = WorkbookFactory.create(inputStream)

        // Selecciona la hoja "FORMATO_CARGA_MANUAL"
        val sheet = workbook.getSheet("Hoja1")
        if (sheet == null) {
            Log.e("ExcelError", "No se encontró la hoja FORMATO_CARGA_MANUAL")
            return emptyList()
        }

        // Procesa las filas de la hoja seleccionada
        for (row in sheet) {
            val rowData = mutableListOf<String>()
            for (cell in row) {
                when (cell.cellType) {
                    org.apache.poi.ss.usermodel.CellType.STRING -> {
                        rowData.add(cell.stringCellValue)
                    }
                    org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                        if (cell.numericCellValue % 1 == 0.0) {
                            // Si es un número entero
                            rowData.add(cell.numericCellValue.toInt().toString())
                        } else {
                            // Si es un número decimal
                            rowData.add(cell.numericCellValue.toString())
                        }
                    }
                    else -> {
                        rowData.add("Unknown")
                    }
                }
            }
            data.add(rowData)
        }
        inputStream?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return data
}



@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun showInventoryDataDialog(
    context: Context,
    dbHelper: MyDatabaseHelper,
    onDismissRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var inventoryText by remember { mutableStateOf("") }

    // Consulta los datos de la tabla Inventario_SAP
    scope.launch {
        val inventarios = dbHelper.getAllInventarios()

        // Construir el mensaje que se mostrará en el AlertDialog
        inventoryText = inventarios.joinToString(separator = "\n") { inventario ->
            "ID: ${inventario["id_inventario_sap"]}, " +
                    "Artículo: ${inventario["numero_articulo"]}, " +
                    "Descripción: ${inventario["descripcion_articulo"]}, " +
                    "Stock: ${inventario["stock_almacen"]}, " +
                    "Precio: ${inventario["precio_unitario"]}, " +
                    "Saldo: ${inventario["saldo_almacen"]}, " +
                    "Codigo: ${inventario["codigo_barras"]}"

        }

        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismissRequest()
            },
            title = {
                Text(text = "Datos del Inventario")
            },
            text = {
                Text(text = inventoryText)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDismissRequest()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
