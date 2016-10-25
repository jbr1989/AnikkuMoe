package es.jbr1989.anikkumoe.object;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jbr1989 on 13/04/2016.
 */
public class clsUser {
    private Integer id;
    private String usuario;
    private String nombre;
    private String avatar;

    public clsUser(JSONObject jUser){

        try {this.id=jUser.getInt("id");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.usuario= jUser.getString("usuario");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.nombre= jUser.getString("nombre");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.avatar= jUser.getString("avatar");}
        catch (JSONException ex){ex.printStackTrace();}

    }

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


    //endregion

    //region GETTERS

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

    public Spanned getHTMLUsuario() {
        return Html.fromHtml(usuario);
    }

    public Spanned getHTMLNombre() {
        return Html.fromHtml(nombre);
    }

    //endregion
}
