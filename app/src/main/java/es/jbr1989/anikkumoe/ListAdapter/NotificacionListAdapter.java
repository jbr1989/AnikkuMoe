package es.jbr1989.anikkumoe.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.object.clsNotificacion;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 06/12/2015.
 */
public class NotificacionListAdapter extends BaseAdapter {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    public static final String SP_NAME = "Notificaciones";

    private Context context;
    private ArrayList<clsNotificacion> oNotificaciones ;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private SharedPreferences NotificacionesConfig;
    private SharedPreferences.Editor NotificacionesConfigEditor;

    private Activity activity;
    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //endregion

    //region CONSTRUCTOR

    public NotificacionListAdapter(Context context){
        this.context = context;

        this.oNotificaciones=new ArrayList<clsNotificacion>();
        nuevos=0;

        NotificacionesConfig = context.getSharedPreferences(SP_NAME, 0);
        NotificacionesConfigEditor = NotificacionesConfig.edit();

        cargar_mensajes(context);
    }

    private void cargar_mensajes(Context context){

        MSG_NOTIFICACION = new HashMap<String, String>();

        MSG_NOTIFICACION.put("MEGCOM",context.getResources().getString(R.string.MSG_NOTIF_MEGCOM));
        MSG_NOTIFICACION.put("REPPUB",context.getResources().getString(R.string.MSG_NOTIF_REPPUB));
        MSG_NOTIFICACION.put("MEGPUB",context.getResources().getString(R.string.MSG_NOTIF_MEGPUB));
        MSG_NOTIFICACION.put("USRSEG",context.getResources().getString(R.string.MSG_NOTIF_USRSEG));
        MSG_NOTIFICACION.put("COMPUB",context.getResources().getString(R.string.MSG_NOTIF_COMPUB));
        MSG_NOTIFICACION.put("PUBMEN",context.getResources().getString(R.string.MSG_NOTIF_PUBMEN));
        MSG_NOTIFICACION.put("COMMEN",context.getResources().getString(R.string.MSG_NOTIF_COMMEN));
        MSG_NOTIFICACION.put("MENPRIV",context.getResources().getString(R.string.MSG_NOTIF_MENPRIV));
        MSG_NOTIFICACION.put("PUBGUA",context.getResources().getString(R.string.MSG_NOTIF_PUBGUA));
        MSG_NOTIFICACION.put("MEGRPUB",context.getResources().getString(R.string.MSG_NOTIF_MEGRPUB));
        MSG_NOTIFICACION.put("COMRPUB",context.getResources().getString(R.string.MSG_NOTIF_COMRPUB));
        MSG_NOTIFICACION.put("PLUGMEGCOM",context.getResources().getString(R.string.MSG_NOTIF_PLUGMEGCOM));
        MSG_NOTIFICACION.put("PLUGPUBMEN",context.getResources().getString(R.string.MSG_NOTIF_PLUGPUBMEN));

    }

    //endregion

    @Override
    public int getCount() {
        return oNotificaciones.size();
    }

    public int getNewsCount(){
        return nuevos;
    }

    public boolean IfNews(){return nuevos>0;}

    public String getNovedades(){
        String novedades="";

        for(clsNotificacion oNotificacion : oNotificaciones){
            if(oNotificacion.nuevo()) {
                if (!oNotificacion.getTipo().equalsIgnoreCase("REACT")) novedades += "\n" + "@" + oNotificacion.user.getNombre() + " " + MSG_NOTIFICACION.get(oNotificacion.getTipo());
                else novedades += "\n" + "@" + oNotificacion.user.getNombre() + "ha reaccionado "+ "a tu publicación";
            }
        }

        return novedades;
    }



    @Override
    public Object getItem(int position) {
        return oNotificaciones.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public Integer getIdPublicacion(int position) {
        return oNotificaciones.get(position).feed.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout lytNotificacion;
        NetworkImageView imgAvatar;
        TextView txtDescr;
        TextView txtFecha;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notificacion, null);

            lytNotificacion = (LinearLayout) convertView.findViewById(R.id.lytNotificacion);
            imgAvatar = (NetworkImageView) convertView.findViewById(R.id.ImgAvatar);
            txtDescr = (TextView) convertView.findViewById(R.id.txtDescr);
            txtFecha = (TextView) convertView.findViewById(R.id.txtFecha);

            convertView.setTag(new ViewHolder(lytNotificacion,imgAvatar,txtDescr,txtFecha));

        } else{
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            lytNotificacion = viewHolder.lytNotificacion;
            imgAvatar = viewHolder.imgAvatar;
            txtDescr = viewHolder.txtDescr;
            txtFecha = viewHolder.txtFecha;
        }

        clsNotificacion oNotificacion = oNotificaciones.get(position);

        if (oNotificacion.nuevo()==Boolean.TRUE) lytNotificacion.setBackgroundColor(context.getResources().getColor(R.color.notification_new));
        else lytNotificacion.setBackgroundColor(Color.TRANSPARENT);

        imgAvatar.setImageUrl(ROOT_URL+"static-img/"+oNotificacion.user.getAvatar(), imageLoader);
        txtFecha.setText(oDate.DateDiff(oNotificacion.getFecha13(), System.currentTimeMillis()));

        if (!oNotificacion.getTipo().equalsIgnoreCase("REACT")) txtDescr.setText("@" + oNotificacion.user.getNombre() + " " + MSG_NOTIFICACION.get(oNotificacion.getTipo()));
        else txtDescr.setText("@" + oNotificacion.user.getNombre() + " ha reaccionado \""+ oNotificacion.getReactionString(context)+ "\" a tu publicación");

        return convertView;
    }

    public void setNotificaciones(JSONArray response){

        clsNotificacion oNotificacion;

        try {
            //JSONArray jNotificaciones=response.getJSONArray("data");

            int num = response.length();
            for (int i=0; i<num;i++){
                oNotificacion= new clsNotificacion(response.getJSONObject(i));
                oNotificaciones.add(oNotificacion);
                if(oNotificacion.nuevo()) nuevos+=1;
            }

        }catch (JSONException ex){ex.printStackTrace();}

    }

    public void clearNotificaciones(){
        oNotificaciones.clear();
        nuevos=0;
    }

    public String getTipo(Integer pos){
        String tipo="url";
        clsNotificacion oNotificacion = oNotificaciones.get(pos);

        switch(oNotificacion.getTipo()){
            case "MEGCOM": tipo="publicacion_comentario";break;
            case "REPPUB": tipo="publicacion"; break;
            case "MEGPUB": tipo="publicacion"; break;
            case "USRSEG": tipo="url"; break;
            case "COMPUB": tipo="publicacion_comentario"; break;
            case "PUBMEN": tipo="publicacion"; break;
            case "COMMEN": tipo="publicacion_comentario"; break;
            case "MENPRIV": tipo="url"; break;
            case "PUBGUA": tipo="publicacion"; break;
            case "MEGRPUB": tipo="publicacion"; break;
            case "COMRPUB": tipo="publicacion"; break;
            case "PLUGMEGCOM": tipo="publicacion_comentario"; break;
            case "PLUGPUBMEN": tipo="publicacion_comentario"; break;
            case "REACT": tipo="publicacion";break;
        }

        return tipo;
    }

    public String getUrl(Integer pos){
        String url=ROOT_URL;
        clsNotificacion oNotificacion = oNotificaciones.get(pos);

        switch(oNotificacion.getTipo()){
            case "MEGCOM": url+="feed/"+oNotificacion.feed.user.getUsuario()+"/"+oNotificacion.getId().toString();break;
            case "REPPUB": url+="feed/"+oNotificacion.feed.user.getUsuario()+ "/"+oNotificacion.getId().toString(); break;
            case "MEGPUB": url+="feed/"+oNotificacion.feed.user.getUsuario()+"/"+oNotificacion.getId().toString(); break;
            case "USRSEG": url+="user/"+oNotificacion.user.getNombre(); break;
            case "COMPUB": url+="feed/"+oNotificacion.feed.user.getUsuario()+"/"+oNotificacion.getId().toString(); break;
            case "PUBMEN": url+="feed/"+oNotificacion.user.getNombre()+ "/"+oNotificacion.getId().toString(); break;
            case "COMMEN": url+="feed/"+oNotificacion.feed.user.getUsuario()+ "/"+oNotificacion.getId().toString(); break;
            case "MENPRIV": url+="mensajes"; break;
            case "PUBGUA": url+="feed/"+oNotificacion.feed.user.getUsuario()+"/"+oNotificacion.getId().toString(); break;
            case "MEGRPUB": url+="feed/"+oNotificacion.feed.user.getUsuario()+ "/"+oNotificacion.getId().toString(); break;
            case "COMRPUB": url+="feed/"+oNotificacion.feed.user.getUsuario()+ "/"+oNotificacion.getId().toString(); break;
            case "PLUGMEGCOM": url+=oNotificacion.getLink(); break;
            case "PLUGPUBMEN": url+=oNotificacion.getLink(); break;
            case "REACT": url+="feed/"+oNotificacion.feed.user.getUsuario()+"/"+oNotificacion.getId().toString();break;
        }
/*
        switch(oNotificacion.getTipo()){
            case "MEGCOM": url+="feed/"+oNotificacion.getUsuariopub()+"/"+oNotificacion.getIdentificador().toString();break;
            case "REPPUB": url+="feed/"+oNotificacion.getUsuario()+ "/"+oNotificacion.getIdentificador().toString(); break;
            case "MEGPUB": url+="feed/"+oNotificacion.getUsuario()+"/"+oNotificacion.getIdentificador().toString(); break;
            case "USRSEG": url+="user/"+oNotificacion.getNombre(); break;
            case "COMPUB": url+="feed/"+oNotificacion.getUsuario()+"/"+oNotificacion.getIdentificador().toString(); break;
            case "PUBMEN": url+="feed/"+oNotificacion.getActor()+ "/"+oNotificacion.getIdentificador().toString(); break;
            case "COMMEN": url+="feed/"+oNotificacion.getUsuariopub()+ "/"+oNotificacion.getIdentificador().toString(); break;
            case "MENPRIV": url+="mensajes"; break;
            case "PUBGUA": url+="feed/"+oNotificacion.getUsuario()+"/"+oNotificacion.getIdentificador().toString(); break;
            case "MEGRPUB": url+="feed/"+oNotificacion.getUsuariopub()+ "/"+oNotificacion.getIdentificador().toString(); break;
            case "COMRPUB": url+="feed/"+oNotificacion.getUsuario()+ "/"+oNotificacion.getIdentificador().toString(); break;
            case "PLUGMEGCOM": url+=oNotificacion.getEnlace(); break;
            case "PLUGPUBMEN": url+=oNotificacion.getEnlace(); break;
        }
        */
        return url;
    }

    // Guardar item cargado
    private static class ViewHolder {
        public final LinearLayout lytNotificacion;
        public final NetworkImageView imgAvatar;
        public final TextView txtDescr, txtFecha;


        public ViewHolder(LinearLayout lytNotificacion, NetworkImageView imgAvatar,  TextView txtDescr,  TextView txtFecha) {
            this.lytNotificacion=lytNotificacion;
            this.imgAvatar=imgAvatar;
            this.txtDescr=txtDescr;
            this.txtFecha=txtFecha;
        }
    }

    //region CONFIGURACION

    public void putConfigNewsCount(){
        NotificacionesConfigEditor.putInt("nuevos", this.getNewsCount());
        NotificacionesConfigEditor.commit();
    }

    public Integer getConfigNewsCount(){
        return NotificacionesConfig.getInt("nuevos",0);
    }

    //endregion

}
