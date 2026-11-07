
package com.example.sincronizacionservicio_compose

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SincronizacionService : Service() {

    companion object {
        const val ACCION_SYNC_LISTA =
            "com.example.sincronizacionservicio_compose.ACCION_SYNC_LISTA"
        const val EXTRA_EDIFICIOS = "edificios_actualizados"
    }

    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        Log.d("SYNC", "1. Servicio iniciado")

        serviceScope.launch {
            // Simular sincronización de datos
            delay(3000)

            val edificiosActualizados = (1..10).random()

            // Comunicar resultado a la aplicación
            val resultado = Intent(ACCION_SYNC_LISTA).apply {
                setPackage(packageName)
                putExtra(EXTRA_EDIFICIOS, edificiosActualizados)
            }

            sendBroadcast(resultado)

            Log.d(
                "SYNC",
                "2. Aviso enviado: $edificiosActualizados edificios"
            )

            stopSelfResult(startId)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        Log.d("SYNC", "4. Servicio finalizado")
        super.onDestroy()
    }
}
