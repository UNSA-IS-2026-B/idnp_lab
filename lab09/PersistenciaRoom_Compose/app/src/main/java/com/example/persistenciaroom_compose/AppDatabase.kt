package com.example.persistenciaroom_compose

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [EdificioEntity::class, AmbienteEntity::class, NotaEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun edificioDao(): EdificioDao
}

fun crearBaseDeDatos(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app.db"
    )
        .fallbackToDestructiveMigration()
        .build()
}