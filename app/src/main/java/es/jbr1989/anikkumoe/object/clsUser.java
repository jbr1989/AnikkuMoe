package es.jbr1989.anikkumoe.object;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by jbr1989 on 13/04/2016.
 */
public class clsUser {
    private Integer id;
    private String usuario;
    private String nombre;
    private String avatar;

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