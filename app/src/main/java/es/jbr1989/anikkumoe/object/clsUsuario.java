package es.jbr1989.anikkumoe.object;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jbr1989 on 04/12/2015.
 */
public class clsUsuario {

    //region VARIABLES

    private String token;

    private Integer id;
    private String usuario;
    private String nombre;
    private String descripcion;
    private Long ultimo_acceso;
    private Long creacion;
    private String estado;
    private String pais;
    private String img_app;
    private String video_app;
    private String web;
    private Integer publicacionesN;
    private Integer experienciaN;
    private Integer siguiendoN;
    private Integer mesiguenN;
    private Integer nakamasN;
    private Integer lovereN;
    private Integer yaloviN;
    private Integer viendoN;
    private Integer yalosigo;
    private Integer visitas;
    private String fechaperfil;
    private String avatar;
    private String banner;
    private Boolean is_following;
    private Boolean is_following_me;
    private Boolean is_favourite;

    //endregion

    //region CONSTRUCTOR

    public clsUsuario(){

        token="";

        id=null;
        usuario="";
        nombre="";
        descripcion="";
        ultimo_acceso=null;
        creacion=null;
        estado="";
        pais="";
        img_app="";
        video_app="";
        web="";
        publicacionesN=null;
        experienciaN=null;
        siguiendoN=null;
        mesiguenN=null;
        nakamasN=null;
        lovereN=null;
        yaloviN=null;
        viendoN=null;
        yalosigo=null;
        visitas=null;
        fechaperfil="";
        avatar="";
        banner="";
        is_following=false;
        is_following_me=false;
        is_favourite=false;

    }

    public clsUsuario(JSONObject jUser){

        try {this.id=jUser.getInt("id");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.usuario= jUser.getString("usuario");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.nombre= jUser.getString("nombre");}
        catch (JSONException ex){ex.printStackTrace();}

        try {this.avatar= jUser.getString("avatar");}
        catch (JSONException ex){ex.printStackTrace();}

    }

    //endregion

    //region SETTER

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setUltimo_acceso(Long ultimo_acceso) {
        this.ultimo_acceso = (ultimo_acceso!=0) ? ultimo_acceso : null;
    }

    public void setCreacion(Long creacion) {
        this.creacion = (creacion!=0) ? creacion : null;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setImg_app(String img_app) {
        this.img_app = img_app;
    }

    public void setVideo_app(String video_app) {
        this.video_app = video_app;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setPublicacionesN(Integer publicacionesN) {
        this.publicacionesN = publicacionesN;
    }

    public void setExperienciaN(Integer experienciaN) {
        this.experienciaN = experienciaN;
    }

    public void setSiguiendoN(Integer siguiendoN) {
        this.siguiendoN = siguiendoN;
    }

    public void setMesiguenN(Integer mesiguenN) {
        this.mesiguenN = mesiguenN;
    }

    public void setNakamasN(Integer nakamasN) {
        this.nakamasN = nakamasN;
    }

    public void setLovereN(Integer lovereN) {
        this.lovereN = lovereN;
    }

    public void setYaloviN(Integer yaloviN) {
        this.yaloviN = yaloviN;
    }

    public void setViendoN(Integer viendoN) {
        this.viendoN = viendoN;
    }

    public void setYalosigo(Integer yalosigo) {
        this.yalosigo = yalosigo;
    }

    public void setVisitas(Integer visitas) {
        this.visitas = visitas;
    }

    public void setFechaperfil(String fechaperfil) {
        this.fechaperfil = fechaperfil;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void setIs_following(Boolean is_following) {
        this.is_following = is_following;
    }

    public void setIs_following_me(Boolean is_following_me) {
        this.is_following_me = is_following_me;
    }

    public void setIs_favourite(Boolean is_favourite) {
        this.is_favourite = is_favourite;
    }

    public void setUsuario(JSONObject response){

        try {
            JSONObject user=response.getJSONObject("user");

            try {this.id=user.getInt("id");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.usuario=user.getString("usuario");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.nombre=user.getString("nombre");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.descripcion=user.getString("descripcion");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.ultimo_acceso= user.getLong("ultimo_acceso");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.creacion=user.getLong("creacion");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.estado=user.getString("estado");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.pais=user.getString("pais");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.img_app=user.getString("img_app");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.video_app=user.getString("video_app");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.web=user.getString("web");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.publicacionesN=user.getInt("publicacionesN");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.experienciaN=user.getInt("experienciaN");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.siguiendoN=user.getInt("siguiendoN");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.mesiguenN=user.getInt("mesiguenN");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.nakamasN=user.getInt("nakamasN");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.lovereN=user.getInt("lovereN");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.yaloviN=user.getInt("yaloviN");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.viendoN=user.getInt("viendoN");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.yalosigo=user.getInt("yalosigo");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.visitas=user.getInt("visitas");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.fechaperfil=user.getString("fechaperfil");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.avatar=user.getString("avatar");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.banner=user.getString("banner");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.is_following=response.getBoolean("is_following");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.is_following_me=response.getBoolean("is_following_me");}
            catch (JSONException ex){ex.printStackTrace();}

            try {this.is_favourite=response.getBoolean("is_favourite");}
            catch (JSONException ex){ex.printStackTrace();}

        }catch (JSONException ex){ex.printStackTrace();}

    }

    //endregion

    //region GETTER

    public String getToken() {
        return token;
    }

    public Integer getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Long getUltimo_acceso() {
        return ultimo_acceso;
    }

    public Long getCreacion() {
        return creacion;
    }

    public String getEstado() {
        return estado;
    }

    public String getPais() {
        return pais;
    }

    public String getImg_app() {
        return img_app;
    }

    public String getVideo_app() {
        return video_app;
    }

    public String getWeb() {
        return web;
    }

    public Integer getPublicacionesN() {
        return publicacionesN;
    }

    public Integer getExperienciaN() {
        return experienciaN;
    }

    public Integer getSiguiendoN() {
        return siguiendoN;
    }

    public Integer getMesiguenN() {
        return mesiguenN;
    }

    public Integer getNakamasN() {
        return nakamasN;
    }

    public Integer getLovereN() {
        return lovereN;
    }

    public Integer getYaloviN() {
        return yaloviN;
    }

    public Integer getViendoN() {
        return viendoN;
    }

    public Integer getYalosigo() {
        return yalosigo;
    }

    public Integer getVisitas() {
        return visitas;
    }

    public String getFechaperfil() {
        return fechaperfil;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBanner() {
        return banner;
    }

    public Boolean getIs_following() {
        return is_following;
    }

    public Boolean getIs_following_me() {
        return is_following_me;
    }

    public Boolean getIs_favourite() {
        return is_favourite;
    }

    public String getUrlUser(){
        return "user/" + usuario;
    }

    //endregion

}
