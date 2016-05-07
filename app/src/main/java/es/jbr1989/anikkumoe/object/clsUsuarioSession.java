package es.jbr1989.anikkumoe.object;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jbr1989 on 04/12/2015.
 */
public class clsUsuarioSession {


    //region VARIABLES

    public static final String SP_NAME = "UsuarioSession";
    SharedPreferences UsuarioSession;

    //endregion

    //region CONSTRUCTOR

    public clsUsuarioSession(Context context) {
        UsuarioSession = context.getSharedPreferences(SP_NAME, 0);
    }

    //endregion

    public void login(String usuario, String token){
        SharedPreferences.Editor UsuarioSessionEditor = UsuarioSession.edit();
        UsuarioSessionEditor.putString("usuario", usuario);
        UsuarioSessionEditor.putString("token", token);
        UsuarioSessionEditor.commit();
    }

    public void setUsuario(clsUsuario usuario){
        SharedPreferences.Editor UsuarioSessionEditor = UsuarioSession.edit();

        UsuarioSessionEditor.putInt("id", usuario.getId());
        UsuarioSessionEditor.putString("usuario", usuario.getUsuario());
        UsuarioSessionEditor.putString("nombre", usuario.getNombre());
        UsuarioSessionEditor.putString("descripcion", usuario.getDescripcion());
        UsuarioSessionEditor.putLong("ultimo_acceso", usuario.getUltimo_acceso());
        UsuarioSessionEditor.putLong("creacion", usuario.getCreacion());
        UsuarioSessionEditor.putString("estado", usuario.getEstado());
        UsuarioSessionEditor.putString("pais", usuario.getPais());
        UsuarioSessionEditor.putString("img_app", usuario.getImg_app());
        UsuarioSessionEditor.putString("video_app", usuario.getVideo_app());
        UsuarioSessionEditor.putString("web", usuario.getWeb());

        UsuarioSessionEditor.putInt("publicacionesN", usuario.getPublicacionesN());
        UsuarioSessionEditor.putInt("experienciaN", usuario.getExperienciaN());
        UsuarioSessionEditor.putInt("siguiendoN", usuario.getSiguiendoN());
        UsuarioSessionEditor.putInt("mesiguenN", usuario.getMesiguenN());
        UsuarioSessionEditor.putInt("nakamasN", usuario.getNakamasN());
        UsuarioSessionEditor.putInt("lovereN", usuario.getLovereN());
        UsuarioSessionEditor.putInt("yaloviN", usuario.getYaloviN());
        UsuarioSessionEditor.putInt("viendoN", usuario.getViendoN());
        UsuarioSessionEditor.putInt("yalosigo", usuario.getYalosigo());
        UsuarioSessionEditor.putInt("visitas", usuario.getVisitas());

        UsuarioSessionEditor.putString("fechaperfil", usuario.getFechaperfil());
        UsuarioSessionEditor.putString("avatar", usuario.getAvatar());
        UsuarioSessionEditor.putString("banner", usuario.getBanner());

        UsuarioSessionEditor.putBoolean("is_following", usuario.getIs_following());
        UsuarioSessionEditor.putBoolean("is_following_me", usuario.getIs_following_me());
        UsuarioSessionEditor.putBoolean("is_favourite", usuario.getIs_favourite());

        UsuarioSessionEditor.commit();
    }

    public void delUsuario() {
        SharedPreferences.Editor userLocalDatabaseEditor = UsuarioSession.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public void setLogin(boolean login) {
        SharedPreferences.Editor userLocalDatabaseEditor = UsuarioSession.edit();
        userLocalDatabaseEditor.putBoolean("login", login);
        userLocalDatabaseEditor.commit();
    }

    public boolean getLogin(){
        if(UsuarioSession.getBoolean("login", false)==true) return true;
        else return false;
    }

    public clsUsuario getUsuarioSession() {
        if (!UsuarioSession.getBoolean("login", false)) return null;

        clsUsuario oUsuario = new clsUsuario();

        oUsuario.setToken(UsuarioSession.getString("token",""));

        oUsuario.setId(UsuarioSession.getInt("id", 0));
        oUsuario.setUsuario(UsuarioSession.getString("usuario", ""));
        oUsuario.setNombre(UsuarioSession.getString("nombre", ""));
        oUsuario.setDescripcion(UsuarioSession.getString("descripcion", ""));
        oUsuario.setUltimo_acceso(UsuarioSession.getLong("ultimo_acceso", 0));
        oUsuario.setCreacion(UsuarioSession.getLong("creacion", 0));
        oUsuario.setEstado(UsuarioSession.getString("estado", ""));
        oUsuario.setPais(UsuarioSession.getString("pais", ""));
        oUsuario.setImg_app(UsuarioSession.getString("img_app", ""));
        oUsuario.setVideo_app(UsuarioSession.getString("video_app", ""));
        oUsuario.setWeb(UsuarioSession.getString("web", ""));

        oUsuario.setPublicacionesN(UsuarioSession.getInt("publicacionesN", 0));
        oUsuario.setExperienciaN(UsuarioSession.getInt("experienciaN", 0));
        oUsuario.setSiguiendoN(UsuarioSession.getInt("siguiendoN", 0));
        oUsuario.setMesiguenN(UsuarioSession.getInt("mesiguenN", 0));
        oUsuario.setNakamasN(UsuarioSession.getInt("nakamasN", 0));
        oUsuario.setLovereN(UsuarioSession.getInt("lovereN", 0));
        oUsuario.setYaloviN(UsuarioSession.getInt("yaloviN", 0));
        oUsuario.setViendoN(UsuarioSession.getInt("viendoN", 0));
        oUsuario.setYalosigo(UsuarioSession.getInt("yalosigo", 0));
        oUsuario.setVisitas(UsuarioSession.getInt("visitas", 0));

        oUsuario.setFechaperfil(UsuarioSession.getString("fechaperfil", ""));
        oUsuario.setAvatar(UsuarioSession.getString("avatar", ""));
        oUsuario.setBanner(UsuarioSession.getString("banner", ""));

        oUsuario.setIs_following(UsuarioSession.getBoolean("is_following", false));
        oUsuario.setIs_following_me(UsuarioSession.getBoolean("is_following_me", false));
        oUsuario.setIs_favourite(UsuarioSession.getBoolean("is_favourite", false));

        return oUsuario;
    }

    public String getToken(){
        if (!UsuarioSession.getBoolean("login", false)) return null;
        return UsuarioSession.getString("token","");
    }

    public String getId(){
        if (!UsuarioSession.getBoolean("login", false)) return null;
        return String.valueOf(UsuarioSession.getInt("id", 0));
    }

    public String getUsuario(){
        if (!UsuarioSession.getBoolean("login", false)) return null;
        return UsuarioSession.getString("usuario","");
    }


}
