
package com.example.sincronizacionservicio_compose

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.sincronizacionservicio_compose.ui.theme.SincronizacionServicio_ComposeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SincronizacionServicio_ComposeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaSincronizacion()
                }
            }
        }
    }
}

@Composable
fun PantallaSincronizacion() {

    val context = LocalContext.current

    var resultado by remember {
        mutableStateOf<String?>(null)
    }

    var sincronizando by remember {
        mutableStateOf(false)
    }

    var cancelando by remember {
        mutableStateOf(false)
    }

    var hayError by remember {
        mutableStateOf(false)
    }

    // Receiver 1: éxito y cancelación
    DisposableEffect(context) {

        val resultadoReceiver = object : BroadcastReceiver() {

            override fun onReceive(
                ctx: Context?,
                intent: Intent?
            ) {

                when (intent?.action) {

                    SincronizacionService.ACCION_SYNC_LISTA -> {

                        val cantidad = intent.getIntExtra(
                            SincronizacionService.EXTRA_EDIFICIOS,
                            0
                        )

                        resultado =
                            "Sincronización completa: $cantidad edificios actualizados"

                        sincronizando = false
                        cancelando = false
                        hayError = false

                        Log.d(
                            "SYNC",
                            "Resultado recibido en Compose"
                        )
                    }

                    SincronizacionService.ACCION_CANCELADA -> {

                        resultado =
                            "Sincronización cancelada por el usuario"

                        sincronizando = false
                        cancelando = false
                        hayError = false

                        Log.d(
                            "SYNC",
                            "Cancelación recibida en Compose"
                        )
                    }
                }
            }
        }

        val filtroResultado = IntentFilter().apply {
            addAction(SincronizacionService.ACCION_SYNC_LISTA)
            addAction(SincronizacionService.ACCION_CANCELADA)
        }

        ContextCompat.registerReceiver(
            context,
            resultadoReceiver,
            filtroResultado,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(resultadoReceiver)
        }
    }

    // Receiver 2: errores de sincronización
    DisposableEffect(context) {

        val errorReceiver = object : BroadcastReceiver() {

            override fun onReceive(
                ctx: Context?,
                intent: Intent?
            ) {

                if (
                    intent?.action ==
                    SincronizacionService.ACCION_SYNC_ERROR
                ) {

                    val mensaje = intent.getStringExtra(
                        SincronizacionService.EXTRA_ERROR
                    ) ?: "Error desconocido"

                    resultado = "Error de sincronización: $mensaje"

                    sincronizando = false
                    cancelando = false
                    hayError = true

                    Log.d(
                        "SYNC",
                        "Error recibido en Compose: $mensaje"
                    )
                }
            }
        }

        val filtroError = IntentFilter(
            SincronizacionService.ACCION_SYNC_ERROR
        )

        ContextCompat.registerReceiver(
            context,
            errorReceiver,
            filtroError,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(errorReceiver)
        }
    }

    // Iniciar el Service
    fun iniciarSincronizacion(simularError: Boolean) {

        resultado = null
        sincronizando = true
        cancelando = false
        hayError = false

        val intent = Intent(
            context,
            SincronizacionService::class.java
        ).apply {
            putExtra(
                SincronizacionService.EXTRA_SIMULAR_ERROR,
                simularError
            )
        }

        context.startService(intent)
    }

    // Interfaz Compose
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Sincronización de Edificios",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                iniciarSincronizacion(false)
            },
            enabled = !sincronizando
        ) {
            Text("Sincronizar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                iniciarSincronizacion(true)
            },
            enabled = !sincronizando
        ) {
            Text("Simular error")
        }

        if (sincronizando) {

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {

                    cancelando = true

                    val intent = Intent(
                        context,
                        SincronizacionService::class.java
                    ).apply {
                        action = SincronizacionService.ACCION_DETENER
                    }

                    context.startService(intent)
                },
                enabled = !cancelando
            ) {
                Text(
                    if (cancelando)
                        "Cancelando..."
                    else
                        "Detener sincronización"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (sincronizando) {

            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (cancelando)
                    "Cancelando sincronización..."
                else
                    "Sincronizando datos..."
            )
        }

        resultado?.let { mensaje ->

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge,
                color = if (hayError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
