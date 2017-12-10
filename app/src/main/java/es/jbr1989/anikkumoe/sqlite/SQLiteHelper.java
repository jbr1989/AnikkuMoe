package es.jbr1989.anikkumoe.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by jbr1989 on 12/11/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "AnikkuMoe";

    SQLiteDatabase db = null;

    String sqlCreateRegistro= "CREATE TABLE registro(" +
            "id_registro integer PRIMARY KEY AUTOINCREMENT, " +
            "fecha timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "categoria varchar(50) NOT NULL, " +
            "tipo varchar(10) NOT NULL, " +
            "descr text" +
            ");";

    String sqlCreateConfig = "CREATE TABLE config(" +
            "name varchar(50) PRIMARY KEY, " +
            "value varchar(100) NOT NULL" +
            ");";

    String sqlCreateChat = "CREATE TABLE chat(" +
            "id_chat integer," +
            "user_id integer," +
            "usuario varchar(100),"+
            "nombre varchar(100),"+
            "avatar text,"+
            "mensaje text, "+
            "enviado13 long," +
            "PRIMARY KEY (id_chat,user_id,enviado13)"+
            ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (db==null || !db.isOpen()) db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateRegistro);
        db.execSQL(sqlCreateConfig);
        db.execSQL(sqlCreateChat);

        Long now = new Date().getTime();

        db.execSQL("INSERT INTO config (name,value) VALUES (\"time_chat_global\",\""+now.toString()+"\")");
        db.execSQL("INSERT INTO config (name,value) VALUES (\"notification_chat_global\",\"0\")");
        db.execSQL("INSERT INTO config (name,value) VALUES (\"notification_mensajes\",\"0\")");
        db.execSQL("INSERT INTO config (name,value) VALUES (\"notification_notificaciones\",\"0\")");

        db.execSQL("INSERT INTO config (name,value) VALUES (\"user\",\"\")");
        db.execSQL("INSERT INTO config (name,value) VALUES (\"password\",\"\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
