package com.example.gardilcicapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.gardilcicapp.model.database.Inventario_SAP


@Database(entities = [Inventario_SAP::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inventarioSAPDao(): InventarioSAPDao
  /*  abstract fun inventarioDao(): InventarioDao
    abstract fun inventarioFisicoDao(): InventarioFisicoDao
    abstract fun itemsNoEncontradosDao(): ItemsNoEncontradosDao */

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { instance = it }

            }
        }
    }
}
