
package com.example.sincronizacionservicio_compose

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.room.withTransaction
import com.example.sincronizacionservicio_compose.data.AppDatabase
import com.example.sincronizacionservicio_compose.data.EdificioEntity
import kotlinx.coroutines.*

class SincronizacionService : Service() {

    companion object {
        const val ACCION_SYNC_LISTA =
            "com.example.sincronizacionservicio_compose.ACCION_SYNC_LISTA"

        const val ACCION_DETENER =
            "com.example.sincronizacionservicio_compose.ACCION_DETENER"

        const val ACCION_CANCELADA =
            "com.example.sincronizacionservicio_compose.ACCION_CANCELADA"

        const val ACCION_SYNC_ERROR =
            "com.example.sincronizacionservicio_compose.ACCION_SYNC_ERROR"

        const val EXTRA_EDIFICIOS = "edificios_actualizados"
        const val EXTRA_ERROR = "mensaje_error"
        const val EXTRA_SIMULAR_ERROR = "simular_error"

        private const val TAG = "SYNC"
    }

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var sincronizacionJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        // Cancelar la sincronización
        if (intent?.action == ACCION_DETENER) {

            Log.d(TAG, "Solicitud de cancelación recibida")

            sincronizacionJob?.cancel()
            sincronizacionJob = null

            sendBroadcast(
                Intent(ACCION_CANCELADA).apply {
                    setPackage(packageName)
                }
            )

            Log.d(TAG, "Cancelación enviada a la interfaz")

            stopSelfResult(startId)
            return START_NOT_STICKY
        }

        // Evitar tareas simultáneas
        if (sincronizacionJob?.isActive == true) {
            Log.d(TAG, "Sincronización ya en ejecución")
            return START_NOT_STICKY
        }

        val simularError = intent?.getBooleanExtra(
            EXTRA_SIMULAR_ERROR, false
        ) ?: false

        Log.d(TAG, "1. Servicio iniciado")

        sincronizacionJob = serviceScope.launch {

            try {
                val database = AppDatabase.obtenerInstancia(
                    applicationContext
                )

                val dao = database.edificioDao()

                // Insertar registros iniciales
                database.withTransaction {

                    if (dao.contarEdificios() == 0) {

                        dao.insertarEdificios(
                            listOf(
                                EdificioEntity(
                                    nombre = "Biblioteca Central",
                                    categoria = "Servicios"
                                ),
                                EdificioEntity(
                                    nombre = "Facultad de Ingeniería",
                                    categoria = "Académico"
                                ),
                                EdificioEntity(
                                    nombre = "Facultad de Ciencias",
                                    categoria = "Académico"
                                ),
                                EdificioEntity(
                                    nombre = "Comedor Universitario",
                                    categoria = "Servicios"
                                ),
                                EdificioEntity(
                                    nombre = "Laboratorios de Sistemas",
                                    categoria = "Académico"
                                )
                            )
                        )

                        Log.d(TAG, "Base de datos inicializada")
                    }
                }

                // Simulación de trabajo en segundo plano
                delay(3000)

                currentCoroutineContext().ensureActive()

                // Error controlado para el ejercicio 3
                if (simularError) {
                    throw IllegalStateException(
                        "Error simulado de conexión con el servidor"
                    )
                }

                // Sincronización real de registros Room
                val actualizados = database.withTransaction {

                    val pendientes = dao.obtenerPendientes()

                    pendientes.forEach { edificio ->

                        currentCoroutineContext().ensureActive()

                        dao.actualizarEdificio(
                            edificio.copy(sincronizado = true)
                        )

                        Log.d(
                            TAG,
                            "Edificio sincronizado: ${edificio.nombre}"
                        )
                    }

                    pendientes.size
                }

                currentCoroutineContext().ensureActive()

                val resultado = Intent(ACCION_SYNC_LISTA).apply {
                    setPackage(packageName)
                    putExtra(EXTRA_EDIFICIOS, actualizados)
                }

                sendBroadcast(resultado)

                Log.d(
                    TAG,
                    "2. Sincronización completa: $actualizados edificios"
                )

            } catch (e: CancellationException) {

                Log.d(TAG, "Sincronización cancelada")
                throw e

            } catch (e: Exception) {

                Log.e(TAG, "3. Error de sincronización: ${e.message}")

                val errorIntent = Intent(ACCION_SYNC_ERROR).apply {
                    setPackage(packageName)
                    putExtra(
                        EXTRA_ERROR,
                        e.message ?: "Error desconocido"
                    )
                }

                sendBroadcast(errorIntent)

                Log.d(TAG, "4. Broadcast de error enviado")

            } finally {
                stopSelfResult(startId)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        Log.d(TAG, "5. Servicio finalizado")
        super.onDestroy()
    }
}
