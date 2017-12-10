package es.jbr1989.anikkumoe.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import es.jbr1989.anikkumoe.object.clsChat;

/**
 * Created by jbr1989 on 19/11/2017.
 */

public class ChatSQLite extends SQLiteHelper {

    private String table="chat";
    public Integer limit = 15;

    public ChatSQLite(Context contexto) {
        super(contexto);
    }

    public Boolean setChat(Integer id_chat, clsChat oChat){

        if (db==null || !db.isOpen()) db = this.getWritableDatabase();

        if (existe(id_chat,oChat)) return true;

        try {
            //Creamos el registro a insertar como objeto ContentValues
            ContentValues rChat = new ContentValues();

            rChat.put("id_chat", id_chat);
            rChat.put("user_id", oChat.getUser_id());
            rChat.put("usuario", oChat.getUsuario());
            rChat.put("nombre", oChat.getNombre());
            rChat.put("mensaje", oChat.getMensaje());
            rChat.put("avatar", oChat.getAvatar());
            rChat.put("enviado13", oChat.getEnviado13());

            //Insertamos el registro en la base de datos
            long id = db.insert(table, null, rChat);

            return (id>0);
        }catch(SQLException e){
            //REGISTRO
            return false;
            //do something
        }

    }

    public ArrayList<clsChat> obtenerLista(String filtro){
        String order=" ORDER BY enviado13 ASC";

        String sql= "";
        if (filtro!="") sql+=" WHERE "+filtro;

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<clsChat> oChats = new ArrayList<clsChat>();

        //String[] args = new String[] {"usu1"};
        //Cursor c = db.rawQuery(" SELECT codigo,nombre FROM Usuarios WHERE nombre=? ", args);

        // (strftime('%s', inicio) * 1000) AS f_inicio
        Cursor c = db.rawQuery("SELECT * FROM "+table+sql+order, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            do { //Recorremos el cursor hasta que no haya más registros
                oChats.add(new clsChat(c));
            } while(c.moveToNext());
        }

        return oChats;
    }

    public ArrayList<clsChat> obtenerUltimos(Integer id_chat, long fecha){
        clsChat oChat;
        String order=" ORDER BY enviado13 DESC";
        //String sql= " WHERE enviado13>"+fecha;
        String sql= "";
        if (id_chat!=0) sql+=" WHERE id_chat="+id_chat;
        if (fecha!=0) sql += " AND enviado13<"+fecha;

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<clsChat> oChats = new ArrayList<clsChat>();

        //String[] args = new String[] {"usu1"};
        //Cursor c = db.rawQuery(" SELECT codigo,nombre FROM Usuarios WHERE nombre=? ", args);

        // (strftime('%s', inicio) * 1000) AS f_inicio
        sql = "SELECT * FROM "+table+sql+order+ " LIMIT "+limit;
        Cursor c = db.rawQuery(sql, null);

        Integer num = c.getCount();

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            do { //Recorremos el cursor hasta que no haya más registros
                oChat = new clsChat(c);
                oChats.add(0, oChat);
            } while(c.moveToNext());
        }

        return oChats;
    }

    public Boolean existe(Integer id_chat, clsChat oChat){

        if (db==null || !db.isOpen()) db = this.getWritableDatabase();

        String sql = " WHERE id_chat="+id_chat+" AND user_id="+oChat.getUser_id()+ " AND enviado13="+oChat.getEnviado13();

        Cursor c = db.rawQuery("SELECT * FROM "+table+sql, null);

        return (c.getCount()>0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {

        if (versionAnterior<=1) {
            db.execSQL("DELETE FROM " + table);
        }

    }

}
