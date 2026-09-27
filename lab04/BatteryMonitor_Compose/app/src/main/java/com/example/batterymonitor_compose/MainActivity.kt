package com.example.batterymonitor_compose

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.batterymonitor_compose.ui.theme.BatteryMonitor_ComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BatteryMonitor_ComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BatteryScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

private const val ACTION_ACTUALIZAR_BATERIA = "com.example.batterymonitor_compose.ACTUALIZAR_BATERIA"

@Composable
fun BatteryScreen(modifier: Modifier = Modifier) {
    var porcentaje by remember { mutableIntStateOf(0) }
    var estaCargando by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // BroadcastReceiver del sistema (cambios automáticos)
    DisposableEffect(Unit) {
        val systemReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val nivel = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val escala = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

                if (nivel != -1 && escala != -1) {
                    porcentaje = (nivel * 100) / escala
                }
                estaCargando = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
            }
        }

        context.registerReceiver(
            systemReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            Context.RECEIVER_EXPORTED
        )
        Log.d("BatteryScreen", "System Receiver registrado")

        onDispose {
            context.unregisterReceiver(systemReceiver)
            Log.d("BatteryScreen", "System Receiver desregistrado")
        }
    }

    // BroadcastReceiver personalizado (actualización manual)
    DisposableEffect(Unit) {
        val manualReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                Log.d("BatteryScreen", "Broadcast personalizado recibido")

                val batteryStatus: Intent? = ctx?.registerReceiver(
                    null,
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                    Context.RECEIVER_EXPORTED
                )

                val nivel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val escala = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

                if (nivel != -1 && escala != -1) {
                    porcentaje = (nivel * 100) / escala
                }
                estaCargando = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL
            }
        }

        context.registerReceiver(
            manualReceiver,
            IntentFilter(ACTION_ACTUALIZAR_BATERIA),
            Context.RECEIVER_NOT_EXPORTED
        )
        Log.d("BatteryScreen", "Manual Receiver registrado")

        onDispose {
            context.unregisterReceiver(manualReceiver)
            Log.d("BatteryScreen", "Manual Receiver desregistrado")
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Batería: $porcentaje%",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (estaCargando) "Estado: Cargando ⚡" else "Estado: No cargando 🔋",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val customIntent = Intent(ACTION_ACTUALIZAR_BATERIA).apply {
                    `package` = context.packageName
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    customIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                try {
                    pendingIntent.send()
                    Log.d("BatteryScreen", "PendingIntent enviado correctamente")
                } catch (e: PendingIntent.CanceledException) {
                    Log.e("BatteryScreen", "Error al enviar PendingIntent", e)
                }
            }
        ) {
            Text("Actualizar manualmente")
        }
    }
}