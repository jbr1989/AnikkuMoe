package es.jbr1989.anikkumoe.object;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jbr1989 on 02/08/2016.
 */
public class clsMensajes {

    //region VARIABLES
    public clsUser user;
    public ArrayList<clsMensaje> oMensajes;
    //endregion

    //region CONSTRUCTOR

    public clsMensajes(){
        oMensajes= new ArrayList<clsMensaje>();
    }

    public clsMensajes(JSONObject jData){

        try {
            JSONObject user=jData.getJSONObject("user");
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

        try{
            JSONArray jMensajes = jData.getJSONArray("data");

            oMensajes= new ArrayList<clsMensaje>();

            for (int i=0;i<jMensajes.length();i++){
                oMensajes.add(new clsMensaje(jMensajes.getJSONObject(i)));
            }

        }catch (JSONException ex){ex.printStackTrace();}

    }
    //endregion

}
