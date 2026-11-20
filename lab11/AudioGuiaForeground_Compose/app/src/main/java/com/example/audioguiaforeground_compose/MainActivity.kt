package com.example.audioguiaforeground_compose

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.audioguiaforeground_compose.data.AppDatabase
import com.example.audioguiaforeground_compose.data.UltimaReproduccionEntity
import com.example.audioguiaforeground_compose.ui.theme.AudioGuiaForeground_ComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    var ultimaReproduccion by remember {
        mutableStateOf<UltimaReproduccionEntity?>(null)
    }

    val edificioActual = EdificiosCampus.lista[indiceActual]

    // Recuperar el último edificio desde Room
    LaunchedEffect(Unit) {

        try {
            val guardado = withContext(Dispatchers.IO) {
                AppDatabase.obtenerInstancia(context)
                    .reproduccionDao()
                    .obtenerUltimaReproduccion()
            }

            ultimaReproduccion = guardado

            if (guardado != null) {
                Log.d(
                    "AUDIO_GUIA",
                    "Room: último edificio recuperado: " +
                            guardado.nombreEdificio
                )
            }

        } catch (e: Exception) {
            Log.e(
                "AUDIO_GUIA",
                "Error leyendo el historial de Room",
                e
            )
        }
    }

    // Solicitar permiso de notificaciones
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
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
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

    // Detectar el ciclo de vida de la aplicación
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

    // Recibir cambios de estado desde el servicio
    DisposableEffect(context) {

        val receiver = object : BroadcastReceiver() {

            override fun onReceive(
                ctx: Context?,
                intent: Intent?
            ) {

                if (
                    intent?.action !=
                    AudioGuiaService.ACCION_ESTADO
                ) return

                val activo = intent.getBooleanExtra(
                    AudioGuiaService.EXTRA_ACTIVO,
                    false
                )

                val enReproduccion = intent.getBooleanExtra(
                    AudioGuiaService.EXTRA_REPRODUCIENDO,
                    false
                )

                val nombre = intent.getStringExtra(
                    AudioGuiaService.EXTRA_EDIFICIO
                )

                val indice = EdificiosCampus.lista.indexOfFirst {
                    it.nombre == nombre
                }

                if (indice >= 0) {
                    indiceActual = indice

                    if (enReproduccion) {
                        ultimaReproduccion =
                            UltimaReproduccionEntity(
                                edificioId =
                                    EdificiosCampus.lista[indice].id,
                                nombreEdificio =
                                    EdificiosCampus.lista[indice].nombre
                            )
                    }
                }

                servicioIniciado = activo
                reproduciendo = enReproduccion

                estado = intent.getStringExtra(
                    AudioGuiaService.EXTRA_MENSAJE
                ) ?: when {
                    !activo -> "Audio-guía detenida"
                    enReproduccion -> "Reproduciendo audio-guía..."
                    else -> "Audio-guía pausada"
                }

                Log.d(
                    "AUDIO_GUIA",
                    "Estado recibido en Compose: $estado"
                )
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(AudioGuiaService.ACCION_ESTADO),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    // Enviar acciones al servicio
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

        } else if (servicioIniciado) {

            context.startService(intent)
        }
    }

    // Interfaz principal
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
            text = "Edificio ${indiceActual + 1} de " +
                    EdificiosCampus.lista.size,
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

        // Botón Reproducir
        Button(
            onClick = {
                enviarAccion(
                    AudioGuiaService.ACCION_REPRODUCIR
                )

                reproduciendo = true
                servicioIniciado = true
                estado = "Reproduciendo audio-guía..."
            },
            enabled = !reproduciendo
        ) {
            Text("Reproducir / Reanudar")
        }

        Spacer(Modifier.height(10.dp))

        // Botón Pausar
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

        Spacer(Modifier.height(10.dp))

        // Botón Detener
        OutlinedButton(
            onClick = {
                enviarAccion(
                    AudioGuiaService.ACCION_DETENER
                )

                reproduciendo = false
                servicioIniciado = false
                estado = "Audio-guía detenida"
            },
            enabled = servicioIniciado
        ) {
            Text("Detener")
        }

        Spacer(Modifier.height(20.dp))

        // Botón Siguiente edificio
        Button(
            onClick = {

                indiceActual = (
                        indiceActual + 1
                        ) % EdificiosCampus.lista.size

                val siguiente =
                    EdificiosCampus.lista[indiceActual]

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

        // Continuar desde el último edificio guardado
        ultimaReproduccion?.let { guardado ->

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Última reproducción:",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = guardado.nombreEdificio,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {

                    val indice =
                        EdificiosCampus.lista.indexOfFirst {
                            it.id == guardado.edificioId
                        }

                    if (indice >= 0) {

                        indiceActual = indice

                        enviarAccion(
                            AudioGuiaService.ACCION_REPRODUCIR,
                            guardado.nombreEdificio
                        )

                        reproduciendo = true
                        servicioIniciado = true
                        estado = "Reproduciendo audio-guía..."

                        Log.d(
                            "AUDIO_GUIA",
                            "Continuando: ${guardado.nombreEdificio}"
                        )
                    }
                },
                enabled = !reproduciendo
            ) {
                Text("Continuar")
            }
        }
    }
}
