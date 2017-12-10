package es.jbr1989.anikkumoe.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jbr1989 on 12/11/2017.
 */

public class RegistroSQLite extends SQLiteHelper {

    private String table="registro";

    public RegistroSQLite(Context contexto) {
        super(contexto);
    }
/*
    public ArrayList<clsRegistro> obtenerLista(String filtro){
        String order=" ORDER BY fecha DESC, id_registro DESC";

        String sql= "";
        if (filtro!="") sql+=" WHERE "+filtro;

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<clsRegistro> oRegistros = new ArrayList<clsRegistro>();

        //String[] args = new String[] {"usu1"};
        //Cursor c = db.rawQuery(" SELECT codigo,nombre FROM Usuarios WHERE nombre=? ", args);

        // (strftime('%s', inicio) * 1000) AS f_inicio
        Cursor c = db.rawQuery("SELECT * FROM registro"+sql+order, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            do { //Recorremos el cursor hasta que no haya más registros
                oRegistros.add(new clsRegistro(c));
            } while(c.moveToNext());
        }

        return oRegistros;
    }
*/
    public Boolean setRegistro(Date fecha, String categoria, String tipo, String descr){
        if (db==null || !db.isOpen()) db = this.getWritableDatabase();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            //Creamos el registro a insertar como objeto ContentValues
            ContentValues nuevoRegistro = new ContentValues();
            if (fecha!=null) nuevoRegistro.put("fecha", dateFormat.format(fecha));
            else nuevoRegistro.put("fecha", dateFormat.format(new Date()));
            nuevoRegistro.put("categoria", categoria);
            nuevoRegistro.put("tipo", tipo);
            nuevoRegistro.put("descr", descr);

            //Insertamos el registro en la base de datos
            long id = db.insert(table, null, nuevoRegistro);

            return (id>0);
        }catch(SQLException e){
            //REGISTRO
            return false;
            //do something
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

    }

}
