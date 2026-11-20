
package com.example.audioguiaforeground_compose

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioGuiaService : Service() {

    companion object {
        const val ACCION_REPRODUCIR =
            "com.example.audioguiaforeground_compose.REPRODUCIR"
        const val ACCION_PAUSAR =
            "com.example.audioguiaforeground_compose.PAUSAR"
        const val ACCION_DETENER =
            "com.example.audioguiaforeground_compose.DETENER"

        private const val ID_CANAL = "canal_audioguia"
        private const val ID_NOTIFICACION = 1
        private const val TAG = "AUDIO_GUIA"
    }

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var job: Job? = null
    private var segundos = 0
    private var enPrimerPlano = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        val canal = NotificationChannel(
            ID_CANAL,
            "Audio-guía",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificaciones del reproductor de audio-guía"
        }

        val manager = getSystemService(
            NotificationManager::class.java
        )

        manager.createNotificationChannel(canal)

        Log.d(TAG, "Canal de notificaciones creado")
    }

    private fun construirNotificacion(
        titulo: String,
        estado: String
    ): Notification {
        return NotificationCompat.Builder(this, ID_CANAL)
            .setContentTitle(titulo)
            .setContentText(estado)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACCION_REPRODUCIR -> {
                Log.d(TAG, "Reproducción iniciada o reanudada")

                val notificacion = construirNotificacion(
                    "Biblioteca Central",
                    "Reproduciendo audio-guía"
                )

                startForeground(
                    ID_NOTIFICACION,
                    notificacion
                )

                enPrimerPlano = true
                iniciarAvance()
            }

            ACCION_PAUSAR -> {
                Log.d(TAG, "Reproducción pausada")
                job?.cancel()
                job = null

                if (enPrimerPlano) {
                    val notificacion = construirNotificacion(
                        "Biblioteca Central",
                        "Audio-guía pausada"
                    )

                    getSystemService(
                        NotificationManager::class.java
                    ).notify(ID_NOTIFICACION, notificacion)
                }
            }

            ACCION_DETENER -> {
                Log.d(TAG, "Reproducción detenida")

                job?.cancel()
                job = null
                segundos = 0

                stopForeground(STOP_FOREGROUND_REMOVE)
                enPrimerPlano = false
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun iniciarAvance() {
        if (job?.isActive == true) return

        job = serviceScope.launch {
            while (true) {
                delay(1000)
                segundos++

                Log.d(
                    TAG,
                    "Reproducción simulada: $segundos segundos"
                )
            }
        }
    }

    override fun onDestroy() {
        job?.cancel()
        serviceScope.cancel()

        Log.d(TAG, "Servicio finalizado")
        super.onDestroy()
    }
}
