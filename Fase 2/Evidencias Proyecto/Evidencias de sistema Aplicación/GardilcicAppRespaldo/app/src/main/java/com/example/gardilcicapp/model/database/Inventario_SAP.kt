package com.example.gardilcicapp.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Inventario_SAP")
data class Inventario_SAP(
    @PrimaryKey(autoGenerate = true) val id_inventario_sap: Int = 0,
    val id_inventario: Int,
    val numero_articulo: String,
    val descripcion_articulo: String,
    val unidad_medida: String,
    val stock_almacen: Int,
    val precio_unitario: Double,
    val saldo_almacen: Int,
    val codigo_barras: String,
    val codigo_almacen: String,
    val fecha_carga: String
)

