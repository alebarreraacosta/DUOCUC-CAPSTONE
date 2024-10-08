import android.content.Context
import android.net.Uri
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
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

@Composable
fun ImportScreen(
    navController: NavHostController,
    usuarioNombre: String, // Recibir el nombre del usuario
    usuarioApellidoPaterno: String, // Recibir el apellido paterno del usuario
    onExcelDataRead: (List<List<String>>) -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isFileSelected by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") } // Nombre completo del archivo, incluyendo la extensión
    var excelData by remember { mutableStateOf<List<List<String>>?>(null) }

    val context = LocalContext.current

    // File picker launcher
    val excelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        uri?.let {
            // Obtener el nombre completo del archivo (incluyendo la extensión)
            selectedFileName = getFileNameFromUri(context, it) ?: "Archivo Excel"
            isFileSelected = true
            excelData = readExcelFileFromUri(context, it)

            // Aquí pasamos los datos leídos al MainActivity
            onExcelDataRead(excelData ?: emptyList()) // Guardar los datos pero no cambiar de pantalla todavía.
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
                            text = "$usuarioNombre $usuarioApellidoPaterno", // Mostrar nombre completo
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
                            // Navegamos a la pantalla de inventario con los datos leídos y el nombre del archivo
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
            }
        }
    )

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

// Función para leer el archivo Excel
fun readExcelFileFromUri(context: Context, uri: Uri): List<List<String>> {
    val data = mutableListOf<List<String>>()

    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (row in sheet) {
            val rowData = mutableListOf<String>()
            for (cell in row) {
                when (cell.cellType) {
                    org.apache.poi.ss.usermodel.CellType.STRING -> {
                        rowData.add(cell.stringCellValue)
                    }
                    org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                        rowData.add(cell.numericCellValue.toString())
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
