package es.jbr1989.anikkumoe.object;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import es.jbr1989.anikkumoe.R;

/**
 * Created by jbr1989 on 05/12/2015.
 */
public class clsNotificacion {

    //region VARIABLES

    private Integer id;
    private String id_usuario;
    private String tipo;
    private String id_owner;
    private String id_enlace;
    private String id_publicacion;
    private String estado;
    private Long fecha13;
    private String reaction;

    public clsUser user;

    public clsNotificacionFeed feed;

    private String link;

    //region CONSTRUCTOR

    public clsNotificacion(JSONObject jNotif){

        try {this.id=jNotif.getInt("id");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.id_usuario=jNotif.getString("id_usuario");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.tipo=jNotif.getString("tipo");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.id_owner=jNotif.getString("id_owner");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.id_enlace=jNotif.getString("id_enlace");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.id_publicacion=jNotif.getString("id_publicacion");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.estado=jNotif.getString("estado");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.fecha13= jNotif.getLong("fecha13");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.reaction=jNotif.getString("reaction");}
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

        try {
            JSONObject feed=jNotif.getJSONObject("feed");
            this.feed= new clsNotificacionFeed();

            try {this.feed.id=feed.getInt("id");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.feed.id_usuario= feed.getString("id_usuario");}
            catch (JSONException ex){ex.printStackTrace();}

            try {
                JSONObject user=feed.getJSONObject("user");
                this.feed.user= new clsUser();

                try {this.feed.user.setId(user.getInt("id"));}
                catch (JSONException ex){ex.printStackTrace();}

                try {this.feed.user.setUsuario(user.getString("usuario"));}
                catch (JSONException ex){ex.printStackTrace();}

                try {this.feed.user.setNombre(user.getString("nombre"));}
                catch (JSONException ex){ex.printStackTrace();}

                try {this.feed.user.setAvatar(user.getString("avatar"));}
                catch (JSONException ex){ex.printStackTrace();}

            }
            catch (JSONException ex){ex.printStackTrace();}

        }
        catch (JSONException ex){ex.printStackTrace();}

        try {this.link=jNotif.getString("link");}
        catch (JSONException ex){ex.printStackTrace();}

    }

    //endregion

    //region SETTER

    public void setId(Integer id) {
        this.id = id;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setId_owner(String id_owner) {
        this.id_owner = id_owner;
    }

    public void setId_enlace(String id_enlace) {
        this.id_enlace = id_enlace;
    }

    public void setId_publicacion(String id_publicacion) {
        this.id_publicacion = id_publicacion;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setFecha13(Long fecha13) {
        this.fecha13 = fecha13;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public void setUser(clsUser user) {
        this.user = user;
    }

    public void setFeed(clsNotificacionFeed feed) {
        this.feed = feed;
    }

    public void setLink(String link) {
        this.link = link;
    }


    //endregion

    //region GETTER

    public Integer getId() {
        return id;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public String getTipo() {
        return tipo;
    }

    public String getId_owner() {
        return id_owner;
    }

    public String getId_enlace() {
        return id_enlace;
    }

    public String getId_publicacion() {
        return id_publicacion;
    }

    public String getEstado() {
        return estado;
    }

    public Long getFecha13() {
        return fecha13;
    }

    public String getReaction() {
        return reaction;
    }

    public String getReactionString(Context context){
        String[] oReactions, oReactionsString;
        oReactions = context.getResources().getStringArray(R.array.reactions_values);
        oReactionsString = context.getResources().getStringArray(R.array.reactions_names);

        Integer pos = Arrays.asList(oReactions).indexOf(reaction);

        if (pos!=-1) return oReactionsString[pos];
        else return "";
    }

    public clsUser getUser() {
        return user;
    }

    public clsNotificacionFeed getFeed() {
        return feed;
    }

    public String getLink() {
        return link;
    }


    //endregion


    public Boolean nuevo(){
        Boolean nuevo=Boolean.FALSE;
        if (this.getEstado().equalsIgnoreCase("X")==true)
        {nuevo=Boolean.FALSE;}
        else
        {nuevo=Boolean.TRUE;}

        return nuevo;
    }

    public class clsNotificacionFeed{
        private Integer id;
        private String id_usuario;
        public clsUser user;

        public Integer getId() {
            return id;
        }

        public String getId_usuario() {
            return id_usuario;
        }

        public clsUser getUser() {
            return user;
        }
    }
}
