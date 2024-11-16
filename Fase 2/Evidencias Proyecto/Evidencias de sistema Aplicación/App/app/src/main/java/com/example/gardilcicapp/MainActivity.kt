package com.example.gardilcicapp

import ImportScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gardilcicapp.ui.screens.LoginScreen
import com.example.gardilcicapp.ui.theme.GardilcicAppTheme
import inventoryScreen
import com.example.gardilcicapp.data.database.MyDatabaseHelper

// Importar la nueva pantalla

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GardilcicAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    var excelData by remember { mutableStateOf<List<List<String>>?>(null) }
    var usuarioNombre by remember { mutableStateOf("") }
    var usuarioApellidoPaterno by remember { mutableStateOf("") }
    var idUsuario by remember { mutableStateOf(0) } // Cambiar de String a Int si `IdUsuario` es un entero

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController) { userId, nombre, apellidoPaterno ->
                idUsuario = userId // Guardar el IdUsuario
                usuarioNombre = nombre // Guardar el nombre
                usuarioApellidoPaterno = apellidoPaterno // Guardar el apellido paterno
            }
        }
        composable("import") {
            ImportScreen(navController = navController, usuarioNombre = usuarioNombre, usuarioApellidoPaterno = usuarioApellidoPaterno, idUsuario = idUsuario) { data ->
                excelData = data // Guardamos los datos del Excel cuando se selecciona
            }
        }
        composable(
            route = "inventoryScreen/{fileName}",
            arguments = listOf(navArgument("fileName") { type = NavType.StringType })
        ) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName")
            inventoryScreen(
                navController = navController,
                fileName = fileName,
                excelData = excelData,
                usuarioNombre = usuarioNombre, // Pasar el nombre del usuario
                usuarioApellidoPaterno = usuarioApellidoPaterno // Pasar el apellido paterno del usuario
            )
        }

        // Agregamos la nueva pantalla InventoryOptionsScreen
        composable("inventoryOptionsScreen") {
            InventoryOptionsScreen(
                navController = navController,
                usuarioNombre = usuarioNombre,
                usuarioApellidoPaterno = usuarioApellidoPaterno,
                dbHelper = MyDatabaseHelper(LocalContext.current)
            )
        }

        composable("scanProduct") {
            ScanProductScreen(
                navController = navController,
                dbHelper = MyDatabaseHelper(LocalContext.current), // Asegúrate de pasar el contexto
                onScanResult = { scannedCode ->


                }
            )
        }

        composable("BarcodeScannerScreen") {
            BarcodeScannerScreen(navController = navController, dbHelper = MyDatabaseHelper(LocalContext.current))
        }

        composable("articleDetails/{articleName}/{articleDescription}") { backStackEntry ->
            val articleName = backStackEntry.arguments?.getString("articleName") ?: ""
            val articleDescription = backStackEntry.arguments?.getString("articleDescription") ?: ""

            ArticleDetailsScreen(
                navController = navController,
                articleName = articleName,
                articleDescription = articleDescription,
                usuarioNombre = usuarioNombre,
                usuarioApellidoPaterno = usuarioApellidoPaterno,
                dbHelper = MyDatabaseHelper(LocalContext.current),
                idUsuario = idUsuario
            )
        }

        /*composable("registerNewProduct") {
            RegisterProductScreen(
                navController = navController,
                usuarioNombre = usuarioNombre,
                usuarioApellidoPaterno = usuarioApellidoPaterno,
                dbHelper = MyDatabaseHelper(LocalContext.current),
                1
            )
        }*/

        composable("registerNewProduct/{barcode}") { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            RegisterProductScreen(
                navController = navController,
                usuarioNombre = usuarioNombre,
                usuarioApellidoPaterno = usuarioApellidoPaterno,
                dbHelper = MyDatabaseHelper(LocalContext.current),
                idUsuario = idUsuario,
                barcode = barcode
            )
        }




        composable("searchByText") {
            SearchByTextScreen(
                navController = navController,
                usuarioNombre = usuarioNombre,
                usuarioApellidoPaterno = usuarioApellidoPaterno,
                dbHelper = MyDatabaseHelper(LocalContext.current)
            )
        }

        composable("productListScreen") {
            ProductListScreen(
                navController = navController,
                usuarioNombre = usuarioNombre,
                usuarioApellidoPaterno = usuarioApellidoPaterno,
                dbHelper = MyDatabaseHelper(LocalContext.current)
            )
        }




    }
}


