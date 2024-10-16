package com.example.gardilcicapp.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_database.db"
        private const val DATABASE_VERSION = 1

        // Tabla Inventario_SAP
        const val TABLE_INVENTARIO_SAP = "Inventario_SAP"
        const val COLUMN_ID = "id_inventario_sap"
        const val COLUMN_ID_INVENTARIO = "id_inventario"
        const val COLUMN_NUMERO_ARTICULO = "numero_articulo"
        const val COLUMN_DESCRIPCION_ARTICULO = "descripcion_articulo"
        const val COLUMN_UNIDAD_MEDIDA = "unidad_medida"
        const val COLUMN_STOCK_ALMACEN = "stock_almacen"
        const val COLUMN_PRECIO_UNITARIO = "precio_unitario"
        const val COLUMN_SALDO_ALMACEN = "saldo_almacen"
        const val COLUMN_CODIGO_BARRAS = "codigo_barras"
        const val COLUMN_CODIGO_ALMACEN = "codigo_almacen"
        const val COLUMN_FECHA_CARGA = "fecha_carga"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear la tabla Inventario_SAP
        val createInventarioSAPTable = """
            CREATE TABLE $TABLE_INVENTARIO_SAP (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ID_INVENTARIO INTEGER,
                $COLUMN_NUMERO_ARTICULO TEXT,
                $COLUMN_DESCRIPCION_ARTICULO TEXT,
                $COLUMN_UNIDAD_MEDIDA TEXT,
                $COLUMN_STOCK_ALMACEN INTEGER,
                $COLUMN_PRECIO_UNITARIO REAL,
                $COLUMN_SALDO_ALMACEN INTEGER,
                $COLUMN_CODIGO_BARRAS TEXT,
                $COLUMN_CODIGO_ALMACEN TEXT,
                $COLUMN_FECHA_CARGA TEXT
            )
        """.trimIndent()
        db.execSQL(createInventarioSAPTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Actualizar la base de datos si se cambia la versión
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTARIO_SAP")
        onCreate(db)
    }

    // Método para insertar datos en la tabla Inventario_SAP
    fun insertInventarioSAP(
        idInventario: Int,
        numeroArticulo: String,
        descripcionArticulo: String,
        unidadMedida: String,
        stockAlmacen: Int,
        precioUnitario: Float,
        saldoAlmacen: Int,
        codigoBarras: String,
        codigoAlmacen: String,
        fechaCarga: String
    ): Long {
        val db = this.writableDatabase

        // Verifica que el código de barras no esté vacío o nulo antes de insertar
        if (codigoBarras.isBlank()) {
            Log.d("Database", "El código de barras está vacío, no se insertará")
            return -1
        }

        val values = ContentValues().apply {
            put(COLUMN_ID_INVENTARIO, idInventario)
            put(COLUMN_NUMERO_ARTICULO, numeroArticulo)
            put(COLUMN_DESCRIPCION_ARTICULO, descripcionArticulo)
            put(COLUMN_UNIDAD_MEDIDA, unidadMedida)
            put(COLUMN_STOCK_ALMACEN, stockAlmacen)
            put(COLUMN_PRECIO_UNITARIO, precioUnitario)
            put(COLUMN_SALDO_ALMACEN, saldoAlmacen)
            put(COLUMN_CODIGO_BARRAS, codigoBarras)
            put(COLUMN_CODIGO_ALMACEN, codigoAlmacen)
            put(COLUMN_FECHA_CARGA, fechaCarga)
        }

        return db.insert(TABLE_INVENTARIO_SAP, null, values)
    }



    // Método para leer todos los registros de Inventario_SAP
    fun getAllInventarios(): List<Map<String, Any>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_INVENTARIO_SAP", null)
        val dataList = mutableListOf<Map<String, Any>>()

        if (cursor.moveToFirst()) {
            do {
                val rowData = mutableMapOf<String, Any>()
                rowData[COLUMN_ID] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                rowData[COLUMN_NUMERO_ARTICULO] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMERO_ARTICULO))
                rowData[COLUMN_DESCRIPCION_ARTICULO] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION_ARTICULO))
                rowData[COLUMN_STOCK_ALMACEN] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_ALMACEN)) // Asegúrate de que sea leído como entero
                rowData[COLUMN_PRECIO_UNITARIO] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRECIO_UNITARIO)) // Asegúrate de que sea leído como float
                rowData[COLUMN_SALDO_ALMACEN] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALDO_ALMACEN)) // Asegúrate de que sea leído como entero
                rowData[COLUMN_CODIGO_BARRAS] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODIGO_BARRAS))

                dataList.add(rowData)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dataList
    }


    fun truncateInventarioSAP() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_INVENTARIO_SAP")
        db.execSQL("VACUUM") // Esto optimiza la base de datos después del borrado masivo
    }


    fun areInventoriesEqual(dbData: List<Map<String, Any>>, excelData: List<List<String>>): Boolean {
        // Recorremos las filas del Excel (ignorando la cabecera)
        excelData.drop(1).forEachIndexed { index, rowData ->
            // Convertimos los valores numéricos
            val stockAlmacen = rowData[3].toIntOrNull() ?: 0
            val precioUnitario = rowData[4].toFloatOrNull() ?: 0.0f
            val saldoAlmacen = rowData[5].toIntOrNull() ?: 0

            // Buscamos la fila correspondiente en los datos de la base de datos
            val dbRow = dbData.getOrNull(index) ?: return false

            // Comparamos cada campo clave
            if (dbRow["numero_articulo"] != rowData[0] ||
                dbRow["descripcion_articulo"] != rowData[1] ||
                dbRow["unidad_medida"] != rowData[2] ||
                dbRow["stock_almacen"] != stockAlmacen ||
                dbRow["precio_unitario"] != precioUnitario ||
                dbRow["saldo_almacen"] != saldoAlmacen ||
                dbRow["codigo_barras"] != rowData[6] ||
                dbRow["codigo_almacen"] != rowData[7]) {
                return false // Si alguno de los campos no coincide, devolvemos false
            }
        }
        return true // Si todo coincide, devolvemos true
    }

    fun compareWithInventarioSAP(
        numeroArticulo: String,
        descripcionArticulo: String,
        unidadMedida: String,
        stockAlmacen: Int,
        precioUnitario: Float,
        saldoAlmacen: Int,
        codigoBarras: String,
        codigoAlmacen: String
    ): Boolean {
        val db = this.readableDatabase
        val query = """
        SELECT * FROM ${MyDatabaseHelper.TABLE_INVENTARIO_SAP} 
        WHERE ${MyDatabaseHelper.COLUMN_NUMERO_ARTICULO} = ? 
        AND ${MyDatabaseHelper.COLUMN_DESCRIPCION_ARTICULO} = ? 
        AND ${MyDatabaseHelper.COLUMN_UNIDAD_MEDIDA} = ? 
        AND ${MyDatabaseHelper.COLUMN_STOCK_ALMACEN} = ? 
        AND ${MyDatabaseHelper.COLUMN_PRECIO_UNITARIO} = ? 
        AND ${MyDatabaseHelper.COLUMN_SALDO_ALMACEN} = ? 
        AND ${MyDatabaseHelper.COLUMN_CODIGO_BARRAS} = ? 
        AND ${MyDatabaseHelper.COLUMN_CODIGO_ALMACEN} = ?
    """
        val cursor = db.rawQuery(query, arrayOf(
            numeroArticulo,
            descripcionArticulo,
            unidadMedida,
            stockAlmacen.toString(),
            precioUnitario.toString(),
            saldoAlmacen.toString(),
            codigoBarras,
            codigoAlmacen
        ))

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }


    // En la clase MyDatabaseHelper
    fun getMaxInventarioId(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT MAX($COLUMN_ID_INVENTARIO) FROM $TABLE_INVENTARIO_SAP", null)
        var maxId = 0

        if (cursor.moveToFirst()) {
            maxId = cursor.getInt(0)  // Obtener el valor máximo de id_inventario
        }

        cursor.close()
        return maxId
    }


    // Método en la clase `MyDatabaseHelper` para verificar si un código de barras existe en la base de datos
    fun checkBarcodeExists(codigoBarras: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM ${MyDatabaseHelper.TABLE_INVENTARIO_SAP} WHERE ${MyDatabaseHelper.COLUMN_CODIGO_BARRAS} = ?"

        Log.d("Database", "Ejecutando consulta: $query con el código $codigoBarras")

        val cursor = db.rawQuery(query, arrayOf(codigoBarras))

        val exists = cursor.moveToFirst()

        Log.d("Database", "Resultado de la consulta: ${exists}")

        cursor.close()
        return exists
    }






}
