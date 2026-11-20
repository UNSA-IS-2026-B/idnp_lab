
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
                Surface(Modifier.fillMaxSize()) {
                    PantallaAudioGuia()
                }
            }
        }
    }
}

@Composable
fun PantallaAudioGuia() {

    val context = LocalContext.current

    var indiceActual by remember {
        mutableIntStateOf(0)
    }

    var estado by remember {
        mutableStateOf("Audio-guía detenida")
    }

    var reproduciendo by remember {
        mutableStateOf(false)
    }

    var servicioIniciado by remember {
        mutableStateOf(false)
    }

    var enSegundoPlano by remember {
        mutableStateOf(false)
    }

    val edificioActual = EdificiosCampus.lista[indiceActual]

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

    // Detectar cambios de visibilidad de la aplicación
    DisposableEffect(Unit) {
        val lifecycle = ProcessLifecycleOwner.get().lifecycle

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

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Comunicación con AudioGuiaService
    fun enviarAccion(
        accion: String,
        nombreEdificio: String = edificioActual.nombre
    ) {
        val intent = Intent(
            context,
            AudioGuiaService::class.java
        ).apply {
            action = accion
            putExtra(
                AudioGuiaService.EXTRA_EDIFICIO,
                nombreEdificio
            )
        }

        if (accion == AudioGuiaService.ACCION_REPRODUCIR) {
            ContextCompat.startForegroundService(
                context,
                intent
            )
        } else {
            if (servicioIniciado) {
                context.startService(intent)
            }
        }
    }

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

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Edificio ${indiceActual + 1} de ${EdificiosCampus.lista.size}",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = edificioActual.nombre,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = estado,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                enviarAccion(AudioGuiaService.ACCION_REPRODUCIR)
                reproduciendo = true
                servicioIniciado = true
                estado = "Reproduciendo audio-guía..."
            },
            enabled = !reproduciendo
        ) {
            Text("Reproducir / Reanudar")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                enviarAccion(AudioGuiaService.ACCION_PAUSAR)
                reproduciendo = false
                estado = "Audio-guía pausada"
            },
            enabled = reproduciendo
        ) {
            Text("Pausar")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                enviarAccion(AudioGuiaService.ACCION_DETENER)
                reproduciendo = false
                servicioIniciado = false
                estado = "Audio-guía detenida"
            }
        ) {
            Text("Detener")
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                indiceActual = (
                        indiceActual + 1
                        ) % EdificiosCampus.lista.size

                val siguiente = EdificiosCampus.lista[indiceActual]

                if (servicioIniciado) {
                    enviarAccion(
                        AudioGuiaService.ACCION_CAMBIAR_EDIFICIO,
                        siguiente.nombre
                    )
                }

                Log.d(
                    "AUDIO_GUIA",
                    "Siguiente edificio: ${siguiente.nombre}"
                )
            }
        ) {
            Text("Siguiente edificio")
        }
    }
}
