package es.studium.pmdm_practica7_googlemapssensores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AyudanteBaseDeDatos extends SQLiteOpenHelper
{
    private static final String NOMBRE_BASE_DE_DATOS = "PasitosApp",
            NOMBRE_TABLA_FRASES = "TbSensores";
    private static final int VERSION_BASE_DE_DATOS = 1;

    public AyudanteBaseDeDatos(Context context)
    {
        super(context, NOMBRE_BASE_DE_DATOS, null, VERSION_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "Latitud TEXT, Longitud TEXT, battery INTEGER)", NOMBRE_TABLA_FRASES));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", NOMBRE_TABLA_FRASES));
        onCreate(db);
    }
}