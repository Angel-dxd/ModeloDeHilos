package com.example.modelodehilos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProfesorCRUD(context: Context) : SQLiteOpenHelper(context, "profesores.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val sqlCrearTabla = """
            CREATE TABLE profesor (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                departamento TEXT
            )
        """.trimIndent()
        db.execSQL(sqlCrearTabla)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Por ahora lo dejamos vacío para desarrollo
    }

    // --- OPERACIONES CRUD ---

    fun insertarProfesor(profesor: Profesor): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("id", profesor.id)
            put("nombre", profesor.nombre)
            // Si el departamento es nulo, SQLite lo guardará como NULL automáticamente
            put("departamento", profesor.departamento)
        }
        val resultado = db.insert("profesor", null, valores)
        db.close()
        return resultado
    }

    fun consultarTodos(): List<Profesor> {
        val lista = mutableListOf<Profesor>()
        val db = readableDatabase
        val sqlConsulta = "SELECT id, nombre, departamento FROM profesor ORDER BY id"

        val cursor = db.rawQuery(sqlConsulta, null)

        if (cursor.moveToFirst()) {
            do {
                // Obtenemos los datos por índice de columna
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val departamento = cursor.getString(2) // Puede ser null

                // Creamos el objeto limpiamente
                val profesor = Profesor(
                    id = id,
                    nombre = nombre,
                    departamento = departamento
                )
                lista.add(profesor)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun borrarProfesor(idProfesor: Int): Int {
        val db = writableDatabase
        val filasBorradas = db.delete(
            "profesor",
            "id = ?",
            arrayOf(idProfesor.toString())
        )
        db.close()
        return filasBorradas
    }

    fun actualizarProfesor(profesor: Profesor): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", profesor.nombre)
            put("departamento", profesor.departamento)
        }
        val filasActualizadas = db.update(
            "profesor",
            valores,
            "id = ?",
            arrayOf(profesor.id.toString())
        )
        db.close()
        return filasActualizadas
    }
}