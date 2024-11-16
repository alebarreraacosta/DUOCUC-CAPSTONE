package com.example.gardilcicapp.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.util.Log
import com.example.gardilcicapp.data.remote.InventarioFisicoItem
import com.example.gardilcicapp.data.remote.InventarioSapItem
import com.example.gardilcicapp.data.remote.ItemNoEncontradoRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_database.db"
        private const val DATABASE_VERSION = 7

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

        // Tabla items_no_encontrados
        const val TABLE_ITEMS_NO_ENCONTRADOS = "items_no_encontrados"
        const val COLUMN_ID_ITEM_NO_ENCONTRADO = "id_item_no_encontrado"
        const val COLUMN_CANTIDAD_CONTADA = "cantidad_contada"
        const val COLUMN_ID_USUARIO = "id_usuario"

        // Tabla Inventario_Fisico
        const val TABLE_INVENTARIO_FISICO = "Inventario_Fisico"
        const val COLUMN_ID_INVENTARIO_FISICO = "id_inventario_fisico"
        const val COLUMN_ID_INVENTARIO_SAP = "id_inventario_sap"
        const val COLUMN_CANTIDAD_FISICA = "cantidad_fisica"
        const val COLUMN_FECHA = "fecha"


        // Tabla Inventario
        const val TABLE_INVENTARIO = "Inventario"
        const val COLUMN_ID_INVENTARIO_NEW = "id_inventario"
        const val COLUMN_ID_USUARIO_NEW = "id_usuario"
        const val COLUMN_FECHA_INICIO = "fecha_inicio"
        const val COLUMN_FECHA_TERMINO = "fecha_termino"
        const val COLUMN_ID_ESTADO = "id_estado"


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
                $COLUMN_CODIGO_BARRAS TEXT ,
                $COLUMN_CODIGO_ALMACEN TEXT,
                $COLUMN_FECHA_CARGA TEXT
            )
        """.trimIndent()
        db.execSQL(createInventarioSAPTable)

        // Crear la tabla items_no_encontrados
        val createItemsNoEncontradosTable = """
        CREATE TABLE $TABLE_ITEMS_NO_ENCONTRADOS (
            $COLUMN_ID_ITEM_NO_ENCONTRADO INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ID_INVENTARIO INTEGER NOT NULL,
            $COLUMN_NUMERO_ARTICULO TEXT NOT NULL,
            $COLUMN_CODIGO_BARRAS TEXT NOT NULL, 
            $COLUMN_DESCRIPCION_ARTICULO TEXT,
            $COLUMN_CANTIDAD_CONTADA INTEGER NOT NULL DEFAULT 1,
            $COLUMN_CODIGO_ALMACEN TEXT DEFAULT '', 
            $COLUMN_FECHA_CARGA TEXT NOT NULL,
            $COLUMN_ID_USUARIO INTEGER NOT NULL
        )
    """.trimIndent()
        db.execSQL(createItemsNoEncontradosTable)

        // Crear la tabla Inventario_Fisico
        val createInventarioFisicoTable = """
        CREATE TABLE $TABLE_INVENTARIO_FISICO (
            $COLUMN_ID_INVENTARIO_FISICO INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ID_INVENTARIO INTEGER,
            $COLUMN_ID_INVENTARIO_SAP TEXT,
            $COLUMN_CANTIDAD_FISICA INTEGER,
            $COLUMN_FECHA TEXT,
            $COLUMN_ID_USUARIO INTEGER
        )
    """.trimIndent()
        db.execSQL(createInventarioFisicoTable)

        // Crear la tabla Inventario
        val createInventarioTable = """
        CREATE TABLE $TABLE_INVENTARIO (
            $COLUMN_ID_INVENTARIO INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ID_USUARIO INTEGER NOT NULL,
            $COLUMN_FECHA_INICIO TEXT NOT NULL,
            $COLUMN_FECHA_TERMINO TEXT,
            $COLUMN_ID_ESTADO INTEGER 
    )
""".trimIndent()
        db.execSQL(createInventarioTable)


    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTARIO_SAP")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS_NO_ENCONTRADOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTARIO_FISICO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTARIO")

        onCreate(db)
    }



    // Método para insertar datos en la tabla items_no_encontrados
    fun insertItemNoEncontrado(
        idInventario: Int,
        numeroArticulo: String,
        descripcion: String,
        cantidadContada: Int,
        idUsuario: Int,
        codigoBarras: String // Añadir como parámetro obligatorio
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_INVENTARIO, idInventario)
            put(COLUMN_NUMERO_ARTICULO, numeroArticulo.ifEmpty { "N/A" }) // Verifica si está vacío
            put(COLUMN_CODIGO_BARRAS, codigoBarras) // Insertar el código de barras recibido
            put(COLUMN_DESCRIPCION_ARTICULO, descripcion.ifEmpty { "N/A" })
            put(COLUMN_CANTIDAD_CONTADA, if (cantidadContada > 0) cantidadContada else 1) // Verifica si es positivo
            put(COLUMN_CODIGO_ALMACEN, "20030") // Código almacén vacío
            put(COLUMN_FECHA_CARGA, SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
            put(COLUMN_ID_USUARIO, idUsuario)
        }

        val result = db.insert(TABLE_ITEMS_NO_ENCONTRADOS, null, values)
        if (result == -1L) {
            Log.e("Database Error", "Error al insertar en items_no_encontrados. Datos: $values")
        }
        return result
    }



    // Método para insertar datos en la tabla Inventario_SAP
    fun insertInventarioSAP(
        idInventario: Int,
        numeroArticulo: String,
        descripcionArticulo: String,
        unidadMedida: String, // Agregar este parámetro
        stockAlmacen: Int,
        precioUnitario: Float,
        saldoAlmacen: Int,
        codigoBarras: String,
        codigoAlmacen: String,
        fechaCarga: String
    ): Long {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_ID_INVENTARIO, idInventario)
            put(COLUMN_NUMERO_ARTICULO, numeroArticulo)
            put(COLUMN_DESCRIPCION_ARTICULO, descripcionArticulo)
            put(COLUMN_UNIDAD_MEDIDA, unidadMedida) // Asegurarte de agregar este valor
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
                rowData[COLUMN_NUMERO_ARTICULO] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMERO_ARTICULO))
                rowData[COLUMN_DESCRIPCION_ARTICULO] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION_ARTICULO))
                rowData[COLUMN_UNIDAD_MEDIDA] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_UNIDAD_MEDIDA)) // Asegúrate de que sea leído como entero
                rowData[COLUMN_STOCK_ALMACEN] = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_STOCK_ALMACEN)) // Asegúrate de que sea leído como float
                rowData[COLUMN_PRECIO_UNITARIO] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRECIO_UNITARIO)) // Asegúrate de que sea leído como entero
                rowData[COLUMN_SALDO_ALMACEN] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SALDO_ALMACEN))
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


    fun getArticleDetailsByBarcode(barcode: String): Article? {
        val db = this.readableDatabase
        val query = "SELECT numero_articulo, descripcion_articulo FROM inventario_sap WHERE codigo_barras = ?"
        val cursor = db.rawQuery(query, arrayOf(barcode))

        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("numero_articulo"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("descripcion_articulo"))
            cursor.close()
            Article(name, description)  // Retorna un objeto Article (puedes crear esta clase según tu estructura)
        } else {
            cursor.close()
            null  // Si no se encuentra el artículo, devuelve null
        }
    }

    data class Article(val name: String, val description: String)  // Clase que representa el artículo

    fun searchArticles(query: String): List<Pair<String, String>> {
        val results = mutableListOf<Pair<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT numero_articulo, descripcion_articulo FROM inventario_sap WHERE numero_articulo LIKE ? OR descripcion_articulo LIKE ?",
            arrayOf("%$query%", "%$query%")
        )

        if (cursor.moveToFirst()) {
            do {
                val numeroArticulo = cursor.getString(cursor.getColumnIndexOrThrow("numero_articulo"))
                val descripcionArticulo = cursor.getString(cursor.getColumnIndexOrThrow("descripcion_articulo"))
                results.add(Pair(numeroArticulo, descripcionArticulo))  // Cambiamos stock_almacen por descripcion_articulo
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return results
    }


    // En la clase MyDatabaseHelper
    fun getItemsNoEncontrados(): List<String> {
        val db = this.readableDatabase
        val items = mutableListOf<String>()
        val query = "SELECT numero_articulo, descripcion_articulo, cantidad_contada, codigo_barras FROM items_no_encontrados"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val numeroArticulo = cursor.getString(cursor.getColumnIndexOrThrow("numero_articulo"))
                val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion_articulo"))
                val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_contada"))
                val codigoBarras = cursor.getString(cursor.getColumnIndexOrThrow("codigo_barras"))
                items.add("Artículo: $numeroArticulo, Descripción: $descripcion, Cantidad: $cantidad, Código: $codigoBarras")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }


    fun insertInventarioFisico(
        idInventario: Int,
        idInventarioSAP: String,
        cantidadFisica: Int,
        fecha: String,
        idUsuario: Int
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_INVENTARIO, idInventario)
            put(COLUMN_ID_INVENTARIO_SAP, idInventarioSAP)
            put(COLUMN_CANTIDAD_FISICA, cantidadFisica)
            put(COLUMN_FECHA, fecha)
            put(COLUMN_ID_USUARIO, idUsuario)
        }

        return db.insert(TABLE_INVENTARIO_FISICO, null, values)
    }


    fun getInventarioFisico(): List<Map<String, Any>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ID_INVENTARIO_SAP, $COLUMN_CANTIDAD_FISICA, $COLUMN_ID_USUARIO, $COLUMN_FECHA FROM $TABLE_INVENTARIO_FISICO",
            null
        )
        val dataList = mutableListOf<Map<String, Any>>()

        if (cursor.moveToFirst()) {
            do {
                val rowData = mutableMapOf<String, Any>()
                rowData[COLUMN_ID_INVENTARIO_SAP] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_INVENTARIO_SAP))
                rowData[COLUMN_CANTIDAD_FISICA] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANTIDAD_FISICA))
                rowData[COLUMN_ID_USUARIO] = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_USUARIO))
                rowData[COLUMN_FECHA] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
                dataList.add(rowData)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dataList
    }



    fun truncateInventarioFisico() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_INVENTARIO_FISICO")
        db.execSQL("VACUUM") // Optimiza la base de datos después de eliminar datos masivos
    }


    fun truncateItemsNoEncontrados() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_ITEMS_NO_ENCONTRADOS")
        db.execSQL("VACUUM") // Optimiza la base de datos después de eliminar datos masivos
    }

    fun truncateInventario() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_INVENTARIO")
        db.execSQL("VACUUM") // Optimiza la base de datos después de eliminar datos masivos
    }



    fun insertInventario(
        idUsuario: Int,
        fechaInicio: String,
        fechaTermino: String?,
        idEstado: Int?
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_USUARIO_NEW, idUsuario)
            put(COLUMN_FECHA_INICIO, fechaInicio)
            put(COLUMN_FECHA_TERMINO, fechaTermino)
            put(COLUMN_ID_ESTADO, idEstado)
        }
        return db.insert(TABLE_INVENTARIO, null, values).also { result ->
            if (result == -1L) {
                Log.e("Database", "Error al insertar en Inventario. Datos: $values")
            }
        }
    }




    // Método para obtener registros de la tabla Inventario
    fun getInventario(): List<Map<String, Any>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Inventario", null)
        val inventarios = mutableListOf<Map<String, Any>>()

        if (cursor.moveToFirst()) {
            do {
                val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")) // Usa getInt para números
                val fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio")) ?: "Sin Fecha"

                inventarios.add(
                    mapOf(
                        COLUMN_ID_USUARIO_NEW to idUsuario,
                        COLUMN_FECHA_INICIO to fechaInicio
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return inventarios
    }

    fun getLatestInventario(): Map<String, Any>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Inventario ORDER BY id_inventario DESC LIMIT 1",
            null
        )

        var inventario: Map<String, Any>? = null
        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"))
            val fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio"))

            inventario = mapOf(
                COLUMN_ID_USUARIO_NEW to idUsuario,
                COLUMN_FECHA_INICIO to fechaInicio
            )
        }
        cursor.close()
        return inventario
    }




    fun getInventarioSapData(): List<InventarioSapItem> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_INVENTARIO_SAP", null)
        val items = mutableListOf<InventarioSapItem>()

        if (cursor.moveToFirst()) {
            do {
                val item = InventarioSapItem(
                    NumeroArticulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMERO_ARTICULO)),
                    DescripcionArticulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION_ARTICULO)),
                    UnidadMedida = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIDAD_MEDIDA)),
                    StockAlmacen = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_ALMACEN)),
                    PrecioUnitario = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRECIO_UNITARIO)),
                    SaldoAlmacen = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SALDO_ALMACEN)),
                    CodigoBarras = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODIGO_BARRAS)),
                    CodigoAlmacen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODIGO_ALMACEN)),
                    FechaCarga = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_CARGA))
                )
                items.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }


    fun getInventarioFisicoData(): List<InventarioFisicoItem> {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_CANTIDAD_FISICA, $COLUMN_FECHA, $COLUMN_ID_USUARIO, $COLUMN_ID_INVENTARIO_SAP FROM $TABLE_INVENTARIO_FISICO",
            null
        )
        val items = mutableListOf<InventarioFisicoItem>()

        if (cursor.moveToFirst()) {
            do {
                val item = InventarioFisicoItem(
                    CantidadFisica = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANTIDAD_FISICA)),
                    Fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)),
                    IdUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_USUARIO)),
                    NumeroArticulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_INVENTARIO_SAP))
                )
                items.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }


    fun getItemsNoEncontradosData(): List<ItemNoEncontradoRequest> {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_NUMERO_ARTICULO, $COLUMN_CODIGO_BARRAS, $COLUMN_DESCRIPCION_ARTICULO, $COLUMN_CANTIDAD_CONTADA, $COLUMN_CODIGO_ALMACEN, $COLUMN_FECHA_CARGA, $COLUMN_ID_USUARIO FROM $TABLE_ITEMS_NO_ENCONTRADOS",
            null
        )
        val items = mutableListOf<ItemNoEncontradoRequest>()

        if (cursor.moveToFirst()) {
            do {
                val item = ItemNoEncontradoRequest(
                    NumeroArticulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMERO_ARTICULO)),
                    CodigoBarras = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODIGO_BARRAS)),
                    Descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION_ARTICULO)),
                    CantidadContada = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANTIDAD_CONTADA)),
                    CodigoAlmacen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODIGO_ALMACEN)),
                    Fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_CARGA)),
                    IdUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_USUARIO))
                )
                items.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }



}



