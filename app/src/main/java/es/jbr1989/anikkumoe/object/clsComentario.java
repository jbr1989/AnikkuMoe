package es.jbr1989.anikkumoe.object;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import es.jbr1989.anikkumoe.other.clsTexto;

/**
 * Created by jbr1989 on 16/05/2016.
 */
public class clsComentario {

    //region VARIABLES

    private Integer id;
    private String id_publicacion;
    private String texto;
    private Long fecha;

    private Integer like_count;
    private boolean is_liked;

    public clsUser user;

    //endregion

    //region CONSTRUCTOR

    public clsComentario(JSONObject jNotif){

        try {this.id=jNotif.getInt("id");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.id_publicacion=jNotif.getString("id_publicacion");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.texto=jNotif.getString("texto");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.fecha= jNotif.getLong("fecha");}
        catch (JSONException ex){ex.printStackTrace();}

        try {
            JSONObject user=jNotif.getJSONObject("user");
            this.user= new clsUser();

            try {this.user.setId(user.getInt("id"));}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.user.setUsuario(user.getString("usuario"));}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.user.setNombre(user.getString("nombre"));}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.user.setAvatar(user.getString("avatar"));}
            catch (JSONException ex){ex.printStackTrace();}

        }
        catch (JSONException ex){ex.printStackTrace();}

        try {this.like_count=jNotif.getInt("like_count");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.is_liked=jNotif.getBoolean("is_liked");}
        catch (JSONException ex){ex.printStackTrace();}

    }

    //endregion

    //region SETTER

    public void setId(Integer id) {
        this.id = id;
    }

    public void setId_publicacion(String id_publicacion) {
        this.id_publicacion = id_publicacion;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public void setUser(clsUser user) {
        this.user = user;
    }

    public void setLike_count(Integer like_count) {
        this.like_count = like_count;
    }

    public void setIs_liked(boolean is_liked) {
        this.is_liked = is_liked;
    }

//endregion

    //region GETTER

    public Integer getId() {
        return id;
    }

    public String getId_publicacion() {
        return id_publicacion;
    }

    public String getTexto() {
        return texto;
    }

    public Spanned getHTMLTexto() {
        return Html.fromHtml(clsTexto.bbcode(texto));
    }

    public Long getFecha() {
        return fecha;
    }

    public Integer getLike_count() {
        return like_count;
    }

    public boolean is_liked() {
        return is_liked;
    }

    public clsUser getUser() {
        return user;
    }

    //endregion


}
