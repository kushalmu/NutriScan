package com.example.nutriscan.ui.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptured: (Bitmap) -> Unit,
    imageCaptureRef: (ImageCapture?) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(Unit) {
        imageCaptureRef(imageCapture)
        try {
            val cameraProvider = context.getCameraProvider()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("CameraPreview", "Use case binding failed", exc)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            imageCaptureRef(null)
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize()
    )
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            try {
                continuation.resume(cameraProvider.get())
            } catch (e: Exception) {
                // If the device has no camera or initialization fails, this prevents the app from crashing.
            }
        }, ContextCompat.getMainExecutor(this))
    }
}

fun ImageCapture.takePicture(context: Context, executor: Executor, onImageCaptured: (Bitmap) -> Unit, onError: (Exception) -> Unit) {
    val photoFile = java.io.File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                try {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    
                    // Note: CameraX OutputFileOptions handles rotation automatically in most cases,
                    // but we can ensure it's loaded properly.
                    if (bitmap != null) {
                        onImageCaptured(bitmap)
                    } else {
                        onError(Exception("Failed to decode image"))
                    }
                    photoFile.delete() // Cleanup cache
                } catch (e: Exception) {
                    onError(e)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}
