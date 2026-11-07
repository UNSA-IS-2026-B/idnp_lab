
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.sincronizacionservicio_compose.ui.theme.SincronizacionServicio_ComposeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("PRUEBA_LAB10", "MainActivity se ha iniciado correctamente")

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

    // Recibir los resultados enviados por el Service
    DisposableEffect(context) {

        val receiver = object : BroadcastReceiver() {

            override fun onReceive(
                ctx: Context?,
                intent: Intent?
            ) {
                Log.d("SYNC", "3. Aviso recibido en la pantalla")

                val cantidad = intent?.getIntExtra(
                    SincronizacionService.EXTRA_EDIFICIOS,
                    0
                ) ?: 0

                resultado =
                    "Sincronización completa: $cantidad edificios actualizados"

                sincronizando = false
            }
        }

        val filtro = IntentFilter(
            SincronizacionService.ACCION_SYNC_LISTA
        )

        ContextCompat.registerReceiver(
            context,
            receiver,
            filtro,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    // Interfaz gráfica
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
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                resultado = null
                sincronizando = true

                val intent = Intent(
                    context,
                    SincronizacionService::class.java
                )

                context.startService(intent)
            },
            enabled = !sincronizando
        ) {
            Text("Sincronizar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (sincronizando) {
            CircularProgressIndicator()

            Spacer(modifier = Modifier.height(12.dp))

            Text("Sincronizando datos...")
        }

        resultado?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
