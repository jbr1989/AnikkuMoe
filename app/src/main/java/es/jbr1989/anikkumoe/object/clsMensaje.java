package es.jbr1989.anikkumoe.object;

import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import es.jbr1989.anikkumoe.other.clsTexto;

/**
 * Created by jbr1989 on 02/08/2016.
 */
public class clsMensaje{

    private Integer id;
    private Integer creador;
    private Integer destino;
    private String texto;
    private String texto_html;
    private Long fecha;


    //region CONSTRUCTOR
    public clsMensaje(JSONObject jMensaje){

        try {this.setId(jMensaje.getInt("id"));}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.setCreador(jMensaje.getInt("creador"));}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.setDestino(jMensaje.getInt("destino"));}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.setTexto(jMensaje.getString("texto"));}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.setFecha(jMensaje.getLong("fecha"));}
        catch (JSONException ex){ex.printStackTrace();}

    }
    //endregion

    //region SETTERS

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreador(Integer creador) {
        this.creador = creador;
    }

    public void setDestino(Integer destino) {
        this.destino = destino;
    }

    public void setTexto(String texto) {
        this.texto = texto;
        this.texto_html= clsTexto.toHTML(this.texto);
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    //endregion

    //region GETTERS

    public Integer getId() {
        return id;
    }

    public Integer getCreador() {
        return creador;
    }

    public Integer getDestino() {
        return destino;
    }

    public String getTexto() {
        return texto;
    }

    public Long getFecha() {
        return fecha;
    }

    public Spanned getTextoHTML(){ return Html.fromHtml(texto_html);}


    //endregion
}
