package es.jbr1989.anikkumoe.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jbr1989 on 12/11/2017.
 */

public class ConfigSQLite extends SQLiteHelper {

    private String table="config";

    public ConfigSQLite(Context contexto) {
        super(contexto);
    }

    public Boolean setConfig(String name, String descr){
        if (exists(name)) return setModConfig(name, descr);
        else return setNewConfig(name,descr);
    }

    public String getConfig(String name){

        Cursor c = null;
        String value= "";
        db = getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            try {
                c = db.rawQuery("SELECT * FROM "+table+" WHERE name=\""+name+"\"", null);

                //Nos aseguramos de que existe al menos un registro
                if (c.moveToFirst()) {
                    try {value= c.getString(c.getColumnIndex("value"));}
                    catch (Exception ex){ex.printStackTrace();}
                }
            }catch(SQLException e){
                e.printStackTrace();
            }

            db.close();
        }

        return value;
    }

    public Boolean exists(String name){

        db = getWritableDatabase();

        //Si hemos abierto correctamente la base de datos
        if(db != null)
        {
            try {
                Cursor c = db.rawQuery("SELECT * FROM "+table+" WHERE name=\""+name+"\"", null);

                //Nos aseguramos de que existe al menos un registro
                if (c.moveToFirst()) return Boolean.TRUE;
            }catch(SQLException e){
                e.printStackTrace();
            }

            db.close();
        }

        return Boolean.FALSE;

    }

    public Boolean setNewConfig(String name, String descr){
        if (db==null || !db.isOpen()) db = this.getWritableDatabase();

        try {
            //Creamos el registro a insertar como objeto ContentValues
            ContentValues nuevoConfig = new ContentValues();
            nuevoConfig.put("name", name);
            nuevoConfig.put("descr", descr);

            //Insertamos el registro en la base de datos
            long id = db.insert(table, null, nuevoConfig);

            return (id>0);
        }catch(SQLException e){
            //REGISTRO
            return false;
            //do something
        }
    }

    public Boolean setModConfig(String name, String value){
        if (db==null || !db.isOpen()) db = this.getWritableDatabase();

        try {
            //Creamos el registro a insertar como objeto ContentValues
            ContentValues nuevoConfig = new ContentValues();
            nuevoConfig.put("value", value);

            //Insertamos el registro en la base de datos
            long id = db.update(table, nuevoConfig, "name=\""+name+"\"",null);

            return (id>0);
        }catch(SQLException e){
            //REGISTRO
            return false;
            //do something
        }
    }

    public Boolean delConfig(String name){

        try {
            //Insertamos el registro en la base de datos
            long id = db.delete( table ,  " name= \"" + name +"\"",  null);

            return (id>0);
        }catch(SQLException e){
            //REGISTRO
            return false;
            //do something
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

        if (versionAnterior<=1) {
            db.execSQL("INSERT INTO config (name,value) VALUES (\"user\",\"\")");
            db.execSQL("INSERT INTO config (name,value) VALUES (\"password\",\"\")");
        }

    }



}
