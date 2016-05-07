package es.jbr1989.anikkumoe.object;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jbr1989 on 04/12/2015.
 */
public class clsNakama {

    //region VARIABLES

    private Integer id;
    private String nombre;
    private String nakama;
    private String avatar;
    private boolean online;

    //endregion

    //region CONSTRUCTOR

    public clsNakama(JSONObject response){

            JSONObject nakama=response;

            try {this.id=nakama.getInt("id");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.nombre=nakama.getString("nombre");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.nakama=nakama.getString("nakama");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.avatar=nakama.getString("avatar");}
            catch (JSONException ex){ex.printStackTrace();}

            online=false;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof clsNakama){
            sameSame = this.id == ((clsNakama) object).id;
        }

        return sameSame;
    }

    //endregion

    //region SETTER

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNakama(String descripcion) {
        this.nakama = nakama;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setOnline(boolean online) {this.online=online;}
    //endregion

    //region GETTER

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNakama() {
        return nakama;
    }

    public String getAvatar() {
        return avatar;
    }

    public Boolean getOnline() { return online;}
    //endregion

}
