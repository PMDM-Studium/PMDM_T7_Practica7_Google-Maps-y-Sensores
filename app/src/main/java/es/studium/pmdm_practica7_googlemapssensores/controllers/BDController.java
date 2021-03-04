package es.studium.pmdm_practica7_googlemapssensores.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import es.studium.pmdm_practica7_googlemapssensores.AyudanteBaseDeDatos;
import es.studium.pmdm_practica7_googlemapssensores.modelos.Sensores;

public class BDController {
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "TbSensores";

    public BDController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }
    public long nuevoSensor(Sensores sensores) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();
        valoresParaInsertar.put("Latitud", sensores.getLatitud());
        valoresParaInsertar.put("Longitud", sensores.getLongitud());
        valoresParaInsertar.put("battery", sensores.getBattery());
        return baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
    }

    public ArrayList<Sensores> obtenerSensor() {
        ArrayList<Sensores> sensores = new ArrayList<>();
        // readable porqué no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        // SELECT Latitud, Longitud, battery, id
        String[] columnasAConsultar = {"Latitud", "Longitud", "battery", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,//from frasesfamosas
                columnasAConsultar,
                null,
                null,
                null,
                null,
                null
        );
        if (cursor == null) {
/*
Salimos aquí porque hubo un error, regresar
lista vacía
*/
            return sensores;
        }
// Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return sensores;

// En caso de que sí haya, itéranos y vamos agregando los
// datos a la lista de frases
        do {

// El 0 es el número de la columna, como seleccionamos
// Latitud, Longitud, battery, id entonces la Latitud es 0, Longitud 1, battery 2 e id es 3
            String txtLatitud = cursor.getString(0);
            String txtLongitud = cursor.getString(1);
            int txtbattery = cursor.getInt(2);
            long idFrase = cursor.getLong(3);
            Sensores sensoresDB = new Sensores(txtLatitud, txtLongitud, txtbattery, idFrase);
            sensores.add(sensoresDB);
        } while (cursor.moveToNext());
        // Fin del ciclo. Cerramos cursor y regresamos la lista de sensores :)
        cursor.close();
        return sensores;
    }
}
