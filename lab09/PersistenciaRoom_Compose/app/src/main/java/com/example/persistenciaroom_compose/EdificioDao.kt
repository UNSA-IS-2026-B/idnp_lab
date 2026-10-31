package com.example.persistenciaroom_compose

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface EdificioDao {

    // --- Operaciones CRUD: EdificioEntity ---
    @Insert
    suspend fun insertarEdificio(edificio: EdificioEntity): Long

    @Update
    suspend fun actualizarEdificio(edificio: EdificioEntity)

    @Delete
    suspend fun eliminarEdificio(edificio: EdificioEntity)

    @Query("SELECT * FROM EdificioEntity")
    suspend fun obtenerTodosLosEdificios(): List<EdificioEntity>


    // --- Operaciones CRUD: AmbienteEntity ---
    @Insert
    suspend fun insertarAmbiente(ambiente: AmbienteEntity): Long

    @Update
    suspend fun actualizarAmbiente(ambiente: AmbienteEntity)

    @Delete
    suspend fun eliminarAmbiente(ambiente: AmbienteEntity)

    @Query("SELECT * FROM AmbienteEntity WHERE edificioId = :edificioId")
    suspend fun obtenerAmbientesPorEdificio(edificioId: Int): List<AmbienteEntity>


    // --- Operaciones CRUD: NotaEntity ---
    @Insert
    suspend fun insertarNota(nota: NotaEntity): Long

    @Update
    suspend fun actualizarNota(nota: NotaEntity)

    @Delete
    suspend fun eliminarNota(nota: NotaEntity)

    @Query("SELECT * FROM NotaEntity WHERE ambienteId = :ambienteId")
    suspend fun obtenerNotasPorAmbiente(ambienteId: Int): List<NotaEntity>


    // --- Consultas Relacionales Compuestas ---
    @Transaction
    @Query("SELECT * FROM EdificioEntity")
    suspend fun obtenerEdificiosConAmbientes(): List<EdificioConAmbientes>

    @Transaction
    @Query("SELECT * FROM AmbienteEntity")
    suspend fun obtenerAmbientesConNotas(): List<AmbienteConNotas>
}