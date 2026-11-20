
package com.example.audioguiaforeground_compose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.audioguiaforeground_compose.ui.theme.AudioGuiaForeground_ComposeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AudioGuiaForeground_ComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PantallaAudioGuia()
                }
            }
        }
    }
}

@Composable
fun PantallaAudioGuia() {

    val context = LocalContext.current

    var estado by remember {
        mutableStateOf("Audio-guía detenida")
    }

    var reproduciendo by remember {
        mutableStateOf(false)
    }

    var enSegundoPlano by remember {
        mutableStateOf(false)
    }

    // Permiso de notificaciones
    val permisoNotificaciones =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->

            Log.d(
                "AUDIO_GUIA",
                "Permiso de notificaciones: $concedido"
            )
        }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoNotificaciones.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    // Detectar primer y segundo plano
    DisposableEffect(Unit) {

        val observer = LifecycleEventObserver { _, event ->

            when (event) {

                Lifecycle.Event.ON_START -> {
                    enSegundoPlano = false

                    Log.d(
                        "AUDIO_GUIA",
                        "Aplicación en primer plano"
                    )
                }

                Lifecycle.Event.ON_STOP -> {
                    enSegundoPlano = true

                    Log.d(
                        "AUDIO_GUIA",
                        "Aplicación en segundo plano"
                    )
                }

                else -> Unit
            }
        }

        val lifecycle = ProcessLifecycleOwner.get().lifecycle

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Enviar acciones al Foreground Service
    fun enviarAccion(accion: String) {

        val intent = Intent(
            context,
            AudioGuiaService::class.java
        ).apply {
            action = accion
        }

        if (accion == AudioGuiaService.ACCION_REPRODUCIR) {

            ContextCompat.startForegroundService(
                context,
                intent
            )

        } else {

            context.startService(intent)
        }
    }

    // Interfaz Jetpack Compose
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Audio-guía del Campus",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Biblioteca Central",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = estado,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Reproducir o reanudar
        Button(
            onClick = {

                enviarAccion(
                    AudioGuiaService.ACCION_REPRODUCIR
                )

                reproduciendo = true
                estado = "Reproduciendo audio-guía..."
            },
            enabled = !reproduciendo
        ) {
            Text("Reproducir / Reanudar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pausar reproducción
        OutlinedButton(
            onClick = {

                enviarAccion(
                    AudioGuiaService.ACCION_PAUSAR
                )

                reproduciendo = false
                estado = "Audio-guía pausada"
            },
            enabled = reproduciendo
        ) {
            Text("Pausar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Detener reproducción
        OutlinedButton(
            onClick = {

                enviarAccion(
                    AudioGuiaService.ACCION_DETENER
                )

                reproduciendo = false
                estado = "Audio-guía detenida"
            }
        ) {
            Text("Detener")
        }
    }
}
