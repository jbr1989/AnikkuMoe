package es.jbr1989.anikkumoe.object;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import es.jbr1989.anikkumoe.other.clsTexto;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class clsChat {

    //region VARIABLES

    private String user_id;
    private String usuario;
    private String nombre;
    private String mensaje;
    private String mensaje_html;
    private long enviado13;
    private String avatar;
    //endregion

    //region CONSTRUCTOR

    public clsChat(JSONObject jPub){

        try {this.enviado13=jPub.getLong("enviado13");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.user_id=jPub.getString("user_id");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.usuario= jPub.getString("usuario");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.nombre= jPub.getString("nombre");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.mensaje= jPub.getString("mensaje"); this.mensaje_html=clsTexto.toHTML(this.mensaje);}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.avatar= jPub.getString("avatar");}
        catch (JSONException ex){ex.printStackTrace();}
    }

    public clsChat(Cursor c){

        try {this.enviado13=c.getLong(c.getColumnIndex("enviado13"));}
        catch (Exception ex){ex.printStackTrace();}

        try {this.user_id=c.getString(c.getColumnIndex("user_id"));}
        catch (Exception ex){ex.printStackTrace();}

        try {this.usuario=c.getString(c.getColumnIndex("usuario"));}
        catch (Exception ex){ex.printStackTrace();}

        try {this.nombre=c.getString(c.getColumnIndex("nombre"));}
        catch (Exception ex){ex.printStackTrace();}

        try {this.mensaje=c.getString(c.getColumnIndex("mensaje")); this.mensaje_html=clsTexto.toHTML(this.mensaje);}
        catch (Exception ex){ex.printStackTrace();}

        try {this.avatar=c.getString(c.getColumnIndex("avatar"));}
        catch (Exception ex){ex.printStackTrace();}

    }

    //endregion

    //region SETTER

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setEnviado13(long enviado13) {
        this.enviado13 = enviado13;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    //endregion

    //region GETTERS

    public String getUser_id() {
        return user_id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public long getEnviado13() {
        return enviado13;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getHTMLMensaje() {
        return mensaje_html;
    }

    //endregion

}
