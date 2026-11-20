package com.example.audioguiaforeground_compose

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.audioguiaforeground_compose.data.AppDatabase
import com.example.audioguiaforeground_compose.data.UltimaReproduccionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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

        const val ACCION_ESTADO =
            "com.example.audioguiaforeground_compose.ESTADO"

        const val EXTRA_EDIFICIO = "nombre_edificio"
        const val EXTRA_REPRODUCIENDO = "reproduciendo"
        const val EXTRA_ACTIVO = "servicio_activo"
        const val EXTRA_MENSAJE = "mensaje_estado"

        private const val ID_CANAL = "canal_audioguia"
        private const val ID_NOTIFICACION = 1
        private const val TAG = "AUDIO_GUIA"
    }

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var mediaPlayer: MediaPlayer? = null
    private var servicioActivo = false
    private var reproduciendo = false

    private var edificioActual =
        EdificiosCampus.lista.first().nombre

    private var mensajeEstado = "Audio-guía detenida"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        val canal = NotificationChannel(
            ID_CANAL,
            "Audio-guía",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Audio-guías del campus"
        }

        getSystemService(
            NotificationManager::class.java
        ).createNotificationChannel(canal)

        Log.d(TAG, "Canal de notificaciones creado")
    }

    private fun crearPendingIntent(
        accion: String,
        codigo: Int
    ): PendingIntent {

        val intent = Intent(
            this,
            AudioGuiaService::class.java
        ).apply {
            action = accion
        }

        return PendingIntent.getService(
            this,
            codigo,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun construirNotificacion(): Notification {

        val abrirApp = PendingIntent.getActivity(
            this,
            10,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(
            this,
            ID_CANAL
        )
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(edificioActual)
            .setContentText(mensajeEstado)
            .setContentIntent(abrirApp)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (reproduciendo) {
            builder.addAction(
                android.R.drawable.ic_media_pause,
                "Pausar",
                crearPendingIntent(ACCION_PAUSAR, 1)
            )
        } else if (mediaPlayer != null) {
            builder.addAction(
                android.R.drawable.ic_media_play,
                "Reanudar",
                crearPendingIntent(ACCION_REPRODUCIR, 2)
            )
        }

        builder.addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Detener",
            crearPendingIntent(ACCION_DETENER, 3)
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

    private fun informarEstado() {
        sendBroadcast(
            Intent(ACCION_ESTADO).apply {
                setPackage(packageName)

                putExtra(EXTRA_EDIFICIO, edificioActual)
                putExtra(EXTRA_REPRODUCIENDO, reproduciendo)
                putExtra(EXTRA_ACTIVO, servicioActivo)
                putExtra(EXTRA_MENSAJE, mensajeEstado)
            }
        )

        Log.d(TAG, "Estado enviado: $mensajeEstado")
    }

    private fun guardarUltimoEdificio() {

        val edificio = EdificiosCampus.lista.find {
            it.nombre == edificioActual
        } ?: return

        serviceScope.launch {
            try {
                AppDatabase.obtenerInstancia(
                    applicationContext
                ).reproduccionDao().guardarUltimaReproduccion(
                    UltimaReproduccionEntity(
                        edificioId = edificio.id,
                        nombreEdificio = edificio.nombre
                    )
                )

                Log.d(
                    TAG,
                    "Room: último edificio guardado: ${edificio.nombre}"
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error guardando en Room", e)
            }
        }
    }

    private fun liberarReproductor() {
        mediaPlayer?.let { player ->
            player.setOnCompletionListener(null)
            player.release()
        }

        mediaPlayer = null
        reproduciendo = false
    }

    private fun detenerServicio() {
        liberarReproductor()

        servicioActivo = false
        mensajeEstado = "Audio-guía detenida"

        informarEstado()

        stopForeground(STOP_FOREGROUND_REMOVE)

        Log.d(TAG, "Reproducción detenida")
        stopSelf()
    }

    private fun reproducir() {

        val recurso = when (edificioActual) {
            "Escuela Profesional de Ingeniería de Sistemas" ->
                R.raw.ingenieria_sistemas

            "Biblioteca de Ingenierías" ->
                R.raw.biblioteca_ingenierias

            "Facultad de Arquitectura" ->
                R.raw.facultad_arquitectura

            "Escuela Profesional de Ingeniería Civil" ->
                R.raw.ingenieria_civil

            "Escuela Profesional de Ingeniería Mecánica" ->
                R.raw.ingenieria_mecanica

            "Ingeniería Química y Metalúrgica" ->
                R.raw.ingenieria_quimica

            "Facultad de Producción y Servicios" ->
                R.raw.produccion_servicios

            "Escuela de Matemática y Física" ->
                R.raw.matematica_fisica

            "Pabellón Nicholson / Pabellón de la Cultura" ->
                R.raw.pabellon_nicholson

            "Estadio Ho Chi Minh" ->
                R.raw.estadio_ho_chi_minh

            "Infounsa" ->
                R.raw.infounsa

            "Panificadora e Imprenta Universitaria" ->
                R.raw.panificadora_imprenta

            "Ingeniería Geológica" ->
                R.raw.ingenieria_geologica

            "Escuela de Arte" ->
                R.raw.escuela_arte

            else -> null
        }

        if (recurso == null) {
            liberarReproductor()

            reproduciendo = false
            mensajeEstado = "Audio no disponible para este edificio"

            actualizarNotificacion()
            informarEstado()

            Log.d(
                TAG,
                "Sin audio disponible: $edificioActual"
            )
            return
        }

        try {
            // Reanudar si ya existe un reproductor preparado
            if (mediaPlayer != null) {
                mediaPlayer?.start()
            } else {
                mediaPlayer = MediaPlayer.create(
                    this,
                    recurso
                ) ?: error("No se pudo cargar el archivo MP3")

                mediaPlayer?.setOnCompletionListener {
                    Log.d(TAG, "Audio finalizado")
                    detenerServicio()
                }

                mediaPlayer?.start()
            }

            reproduciendo = true
            mensajeEstado = "Reproduciendo audio-guía"

            guardarUltimoEdificio()
            actualizarNotificacion()
            informarEstado()

            Log.d(
                TAG,
                "MediaPlayer reproduciendo: $edificioActual"
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error de MediaPlayer", e)

            liberarReproductor()
            mensajeEstado = "Error al reproducir el audio"

            actualizarNotificacion()
            informarEstado()
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACCION_REPRODUCIR -> {

                val solicitado = intent.getStringExtra(
                    EXTRA_EDIFICIO
                )

                if (
                    solicitado != null &&
                    EdificiosCampus.lista.any {
                        it.nombre == solicitado
                    }
                ) {
                    if (solicitado != edificioActual) {
                        liberarReproductor()
                    }

                    edificioActual = solicitado
                }

                // Activar el FGS con su notificación
                if (!servicioActivo) {
                    servicioActivo = true
                    mensajeEstado = "Preparando audio-guía"

                    startForeground(
                        ID_NOTIFICACION,
                        construirNotificacion()
                    )
                }

                reproducir()
            }

            ACCION_PAUSAR -> {

                if (servicioActivo && reproduciendo) {
                    mediaPlayer?.pause()

                    reproduciendo = false
                    mensajeEstado = "Audio-guía pausada"

                    actualizarNotificacion()
                    informarEstado()

                    Log.d(TAG, "MediaPlayer pausado")
                }
            }

            ACCION_CAMBIAR_EDIFICIO -> {

                if (servicioActivo) {
                    val solicitado = intent.getStringExtra(
                        EXTRA_EDIFICIO
                    )

                    if (
                        solicitado != null &&
                        EdificiosCampus.lista.any {
                            it.nombre == solicitado
                        }
                    ) {
                        val estabaReproduciendo = reproduciendo

                        liberarReproductor()
                        edificioActual = solicitado

                        if (estabaReproduciendo) {
                            reproducir()
                        } else {
                            mensajeEstado =
                                "Edificio seleccionado"
                            actualizarNotificacion()
                            informarEstado()
                        }

                        Log.d(
                            TAG,
                            "Edificio cambiado: $edificioActual"
                        )
                    }
                }
            }

            ACCION_DETENER -> {
                detenerServicio()
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        liberarReproductor()
        serviceScope.cancel()

        Log.d(TAG, "Servicio finalizado")
        super.onDestroy()
    }
}
