
package com.example.audioguiaforeground_compose

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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

        const val ACCION_CAMBIAR_EDIFICIO =
            "com.example.audioguiaforeground_compose.CAMBIAR_EDIFICIO"

        const val EXTRA_EDIFICIO = "nombre_edificio"

        private const val ID_CANAL = "canal_audioguia"
        private const val ID_NOTIFICACION = 1
        private const val TAG = "AUDIO_GUIA"
    }

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var job: Job? = null
    private var segundos = 0
    private var servicioActivo = false
    private var reproduciendo = false

    private var edificioActual =
        EdificiosCampus.lista.first().nombre

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        val canal = NotificationChannel(
            ID_CANAL,
            "Audio-guía",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Reproducción de audio-guías del campus"
        }

        getSystemService(
            NotificationManager::class.java
        ).createNotificationChannel(canal)

        Log.d(TAG, "Canal de notificaciones creado")
    }

    // Crear una acción segura hacia este mismo servicio
    private fun crearPendingIntent(
        accion: String,
        requestCode: Int
    ): PendingIntent {

        val intent = Intent(
            this,
            AudioGuiaService::class.java
        ).apply {
            action = accion
        }

        return PendingIntent.getService(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    // Construir notificación con controles
    private fun construirNotificacion(): Notification {

        val estado = if (reproduciendo) {
            "Reproduciendo audio-guía"
        } else {
            "Audio-guía pausada"
        }

        val abrirAplicacion = PendingIntent.getActivity(
            this,
            10,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(
            this,
            ID_CANAL
        )
            .setContentTitle(edificioActual)
            .setContentText(estado)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(abrirAplicacion)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        if (reproduciendo) {

            builder.addAction(
                android.R.drawable.ic_media_pause,
                "Pausar",
                crearPendingIntent(
                    ACCION_PAUSAR,
                    1
                )
            )

        } else {

            builder.addAction(
                android.R.drawable.ic_media_play,
                "Reanudar",
                crearPendingIntent(
                    ACCION_REPRODUCIR,
                    2
                )
            )
        }

        builder.addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Detener",
            crearPendingIntent(
                ACCION_DETENER,
                3
            )
        )

        return builder.build()
    }

    private fun actualizarNotificacion() {
        if (!servicioActivo) return

        getSystemService(
            NotificationManager::class.java
        ).notify(
            ID_NOTIFICACION,
            construirNotificacion()
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACCION_REPRODUCIR -> {

                edificioActual = intent.getStringExtra(
                    EXTRA_EDIFICIO
                ) ?: edificioActual

                reproduciendo = true

                startForeground(
                    ID_NOTIFICACION,
                    construirNotificacion()
                )

                servicioActivo = true
                iniciarAvance()

                Log.d(
                    TAG,
                    "Reproduciendo: $edificioActual"
                )
            }

            ACCION_PAUSAR -> {

                if (servicioActivo) {

                    job?.cancel()
                    job = null

                    reproduciendo = false
                    actualizarNotificacion()

                    Log.d(
                        TAG,
                        "Reproducción pausada desde la app o notificación"
                    )
                }
            }

            ACCION_CAMBIAR_EDIFICIO -> {

                if (servicioActivo) {

                    edificioActual = intent.getStringExtra(
                        EXTRA_EDIFICIO
                    ) ?: edificioActual

                    segundos = 0
                    actualizarNotificacion()

                    Log.d(
                        TAG,
                        "Edificio cambiado: $edificioActual"
                    )
                }
            }

            ACCION_DETENER -> {

                job?.cancel()
                job = null

                segundos = 0
                reproduciendo = false
                servicioActivo = false

                stopForeground(
                    STOP_FOREGROUND_REMOVE
                )

                Log.d(
                    TAG,
                    "Reproducción detenida desde la app o notificación"
                )

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
                    "Reproducción simulada de $edificioActual: $segundos s"
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
