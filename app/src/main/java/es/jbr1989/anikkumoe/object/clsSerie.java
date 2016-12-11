package es.jbr1989.anikkumoe.object;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jbr1989 on 08/11/2016.
 */

public class clsSerie {

    private Integer id;
    private String titulo;
    private String titulo_ingles;
    private String titulo_japones;
    private String titulo_sinonimo;
    private String tipo;
    private String sinopsis;
    private String sinopsis_mcanime;
    private String portada;

    public clsSerie(JSONObject jSerie){

        try {this.id=jSerie.getInt("id");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.titulo= jSerie.getString("titulo");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.titulo_ingles= jSerie.getString("titulo_ingles");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.titulo_japones= jSerie.getString("titulo_japones");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.titulo_sinonimo= jSerie.getString("titulo_sinonimo");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.tipo= jSerie.getString("tipo");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.sinopsis= jSerie.getString("sinopsis");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.sinopsis_mcanime= jSerie.getString("sinopsis_mcanime");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.portada= jSerie.getString("portada");}
        catch (JSONException ex){ex.printStackTrace();}

    }

    //region SETTERS

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setTitulo_ingles(String titulo_ingles) {
        this.titulo_ingles = titulo_ingles;
    }

    public void setTitulo_japones(String titulo_japones) {
        this.titulo_japones = titulo_japones;
    }

    public void setTitulo_sinonimo(String titulo_sinonimo) {
        this.titulo_sinonimo = titulo_sinonimo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public void setSinopsis_mcanime(String sinopsis_mcanime) {
        this.sinopsis_mcanime = sinopsis_mcanime;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }


    //endregion

    //region GETTERS

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTitulo_ingles() {
        return titulo_ingles;
    }

    public String getTitulo_japones() {
        return titulo_japones;
    }

    public String getTitulo_sinonimo() {
        return titulo_sinonimo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public String getSinopsis_mcanime() {
        return sinopsis_mcanime;
    }

    public String getPortada() {
        return portada;
    }

    public String getDescr(){
        if (!sinopsis_mcanime.equalsIgnoreCase("null")) return sinopsis_mcanime;
        else if(!sinopsis.equalsIgnoreCase("null")) return sinopsis;
        else return "";
    }


    //endregion

}
