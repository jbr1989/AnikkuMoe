package es.jbr1989.anikkumoe.object;

import org.json.JSONException;
import org.json.JSONObject;

import es.jbr1989.anikkumoe.other.clsTexto;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class clsChatPrivado {

    //region VARIABLES

    private String id_de;
    private String mensaje;
    private long enviado13;

    private clsTexto oTexto = new clsTexto();
    //endregion

    //region CONSTRUCTOR


    public clsChatPrivado(JSONObject jPub){

        try {this.id_de=jPub.getString("id_de");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.mensaje= jPub.getString("mensaje");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.enviado13=jPub.getLong("enviado13");}
        catch (JSONException ex){ex.printStackTrace();}

    }

    //endregion

    //region SETTER

    public void setId_de(String id_de) {
        this.id_de = id_de;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setEnviado13(long enviado13) {
        this.enviado13 = enviado13;
    }

    //endregion

    //region GETTERS

    public String getId_de() {
        return id_de;
    }

    public String getMensaje() {
        return mensaje;
    }

    public long getEnviado13() {
        return enviado13;
    }



    public String getHTMLMensaje() {
        return oTexto.toHTML(mensaje);
    }

    //endregion

}
