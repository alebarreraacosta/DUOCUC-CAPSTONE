package com.example.gardilcicapp


import android.content.Context
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.viewinterop.AndroidView
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import java.util.concurrent.Executors
import androidx.navigation.NavHostController
import com.example.gardilcicapp.data.database.MyDatabaseHelper
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Composable
fun BarcodeScannerScreen(navController: NavHostController, dbHelper: MyDatabaseHelper) {

    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = BarcodeScanning.getClient()
    var scannedBarcode by remember { mutableStateOf("") }
    // Estados para el manejo de diálogos
    var showResultDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showRegisterDialogAfterResult by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var scannedArticleName by remember { mutableStateOf("") }
    var scannedArticleDescription by remember { mutableStateOf("") }

    // Función para actualizar el estado con el código de barras
    fun handleBarcodeResult(rawValue: String) {
        scannedBarcode = rawValue
        showRegisterDialog = true
    }

    // Función para mostrar el diálogo de resultados
    fun showResult(message: String, openRegisterDialog: Boolean = false) {
        dialogMessage = message
        showResultDialog = true
        showRegisterDialogAfterResult = openRegisterDialog
    }

    // Mostrar el diálogo de resultados del escaneo
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text(text = "Resultado del Escaneo") },
            text = { Text(text = dialogMessage) },
            confirmButton = {
                Button(onClick = {
                    showResultDialog = false
                    // Si se ha indicado que debe abrirse el diálogo de registro después
                    if (showRegisterDialogAfterResult) {
                        showRegisterDialog = true
                    } else {
                        navController.popBackStack()
                    }
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Mostrar el diálogo de registro si el código no existe en la base de datos

    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = { Text(text = "¿Desea registrar este producto?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (scannedBarcode.isNotBlank()) {
                            Log.d("BarcodeScannerScreen", "Código de barras a pasar: $scannedBarcode")
                            showRegisterDialog = false
                            navController.navigate("registerNewProduct/$scannedBarcode")
                        } else {
                            Log.e("BarcodeScannerScreen", "scannedBarcode está vacío, no se navegará")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A86B))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showRegisterDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }




    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            // Inicializar CameraX
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxy(
                                imageProxy,
                                barcodeScanner,
                                context,
                                dbHelper
                            ) { exists, message, articleName, articleDescription, detectedBarcode ->
                                if (exists) {
                                    scannedArticleName = articleName
                                    scannedArticleDescription = articleDescription
                                    navController.navigate("articleDetails/$articleName/$articleDescription")
                                } else {
                                    // Actualiza `scannedBarcode` directamente aquí
                                    scannedBarcode = detectedBarcode
                                    showResult(message, openRegisterDialog = true)
                                }
                            }
                        }
                    }


                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        ctx as LifecycleOwner, cameraSelector, preview, imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e("CameraX", "Error al iniciar la cámara", exc)
                }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalGetImage::class)
fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: BarcodeScanner,
    context: Context,
    dbHelper: MyDatabaseHelper,
    onResult: (Boolean, String, String, String, String) -> Unit  // Añade `scannedBarcode` al callback
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    if (rawValue != null) {
                        Log.d("processImageProxy", "Código de barras detectado: $rawValue")

                        // Verifica si el código de barras existe y actualiza `scannedBarcode`
                        val exists = dbHelper.checkBarcodeExists(rawValue.trim())
                        if (exists) {
                            val article = dbHelper.getArticleDetailsByBarcode(rawValue.trim())
                            if (article != null) {
                                onResult(true, "El artículo se encuentra en la base de datos", article.name, article.description, rawValue)
                            } else {
                                onResult(false, "El artículo no tiene detalles disponibles.", "", "", rawValue)
                            }
                        } else {
                            onResult(false, "El código no se encuentra en la base de datos", "", "", rawValue)
                        }
                    } else {
                        Log.e("processImageProxy", "No se detectó ningún código de barras")
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Barcode", "Error al procesar el código de barras", it)
                onResult(false, "Error al procesar el código de barras", "", "", "")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
