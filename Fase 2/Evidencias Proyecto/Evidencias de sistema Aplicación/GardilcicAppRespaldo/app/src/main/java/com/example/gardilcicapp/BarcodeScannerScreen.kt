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

@Composable
fun BarcodeScannerScreen(navController: NavHostController, dbHelper: MyDatabaseHelper) {

    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = BarcodeScanning.getClient()

    // Estado para el diálogo
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // Función para mostrar el diálogo
    fun showResultDialog(message: String) {
        dialogMessage = message
        showDialog = true
    }

    // Mostrar resultados del escaneo
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(text = "Resultado del Escaneo")
            },
            text = {
                Text(text = dialogMessage)
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.popBackStack()  // Vuelve a la pantalla anterior después de confirmar
                }) {
                    Text("Aceptar")
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

                // Configurar el Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Configurar el Analizador de Imágenes
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, { imageProxy ->
                            processImageProxy(
                                imageProxy,
                                barcodeScanner,
                                context,
                                dbHelper
                            ) { exists, message ->
                                // En lugar de usar Toast, muestra un diálogo
                                showResultDialog(message)
                            }
                        })
                    }

                // Seleccionar la cámara trasera
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
    onResult: (Boolean, String) -> Unit  // Callback para manejar el resultado de la validación
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    if (rawValue != null) {
                        // Aquí se maneja el codigo de barras encontrado
                        Log.d("Barcode", "Código de barras detectado: $rawValue")

                        // Validar si el código de barras existe en la base de datos
                        val exists = dbHelper.checkBarcodeExists(rawValue.trim()) // Método de consulta
                        if (exists) {
                            onResult(true, "Código de barras encontrado: $rawValue")
                        } else {
                            onResult(false, "El código no se encuentra en la base de datos")
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Barcode", "Error al procesar el código de barras", it)
                onResult(false, "Error al procesar el código de barras")
            }
            .addOnCompleteListener {
                imageProxy.close() 
            }
    } else {
        imageProxy.close()
    }
}
