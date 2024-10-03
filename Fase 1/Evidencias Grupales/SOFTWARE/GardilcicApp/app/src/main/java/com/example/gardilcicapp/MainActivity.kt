package com.example.gardilcicapp

import ImportScreen
import android.net.Uri
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

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController) { nombre, apellidoPaterno ->
                usuarioNombre = nombre
                usuarioApellidoPaterno = apellidoPaterno // Guardar también el apellido paterno
            }
        }
        composable("import") {
            ImportScreen(navController = navController, usuarioNombre = usuarioNombre, usuarioApellidoPaterno = usuarioApellidoPaterno) { data ->
                excelData = data // Guardamos los datos del Excel cuando se selecciona
               // navController.navigate("inventoryScreen/${Uri.encode(data.firstOrNull()?.firstOrNull() ?: "Archivo Excel")}")
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

    }
}

