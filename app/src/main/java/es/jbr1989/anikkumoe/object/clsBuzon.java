package es.jbr1989.anikkumoe.object;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import es.jbr1989.anikkumoe.other.clsTexto;

/**
 * Created by jbr1989 on 26/05/2016.
 */
public class clsBuzon {

    //region VARIABLES

    private Integer id;
    private String usuario;
    private String nombre;
    private String avatar;
    private String texto;
    private String texto_html;

    private Long fecha;
    private String last_creator;

    //endregion

    //region CONSTRUCTOR

    public clsBuzon(JSONObject jPub){

        try {this.id=jPub.getInt("id");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.usuario=jPub.getString("usuario");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.nombre=jPub.getString("nombre");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.avatar=jPub.getString("avatar");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.texto=jPub.getString("texto"); this.texto_html= clsTexto.toHTML(this.texto);}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.fecha= jPub.getLong("fecha");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.last_creator=jPub.getString("last_creator");}
        catch (JSONException ex){ex.printStackTrace();}

    }

    //endregion

    public Integer getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getTexto() {
        return texto;
    }

    public Spanned getTextoHTML(){ return Html.fromHtml(clsTexto.recortar(texto_html,90));}

    public Long getFecha() {
        return fecha;
    }

    public String getLast_creator() {
        return last_creator;
    }

    //region GETTERS


    //endregion

    //region SETTERS

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public void setLast_creator(String last_creator) {
        this.last_creator = last_creator;
    }


    //endregion


}
