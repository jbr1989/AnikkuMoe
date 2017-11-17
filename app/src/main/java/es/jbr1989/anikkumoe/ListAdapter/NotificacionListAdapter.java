package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsNotificacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 06/12/2015.
 */
public class NotificacionListAdapter extends RecyclerView.Adapter<NotificacionListAdapter.ViewHolder> {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String IMG_URL = AppController.getInstance().getImg();
    public static final String SP_NAME = "Notificaciones";

    private Context context;
    private Long ultima_fecha;

    private ArrayList<clsNotificacion> oNotificaciones ;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private SharedPreferences NotificacionesConfig;
    private SharedPreferences.Editor NotificacionesConfigEditor;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;
    public CustomRequest2 request2;

    public homeActivity home;

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

    public NotificacionListAdapter(Context context, ArrayList<clsNotificacion> oNotificaciones) {
        this.context = context;
        this.home = (homeActivity) context;

        this.oNotificaciones=oNotificaciones;
        nuevos=0;

        NotificacionesConfig = context.getSharedPreferences(SP_NAME, 0);
        NotificacionesConfigEditor = NotificacionesConfig.edit();

        cargar_mensajes(context);
    }

    @Override
    public NotificacionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificacionListAdapter.ViewHolder holder, final int position) {

        final clsNotificacion oNotificacion = oNotificaciones.get(position);

        if (oNotificacion.nuevo()==Boolean.TRUE) holder.lytNotificacion.setBackgroundColor(context.getResources().getColor(R.color.notification_new));
        else holder.lytNotificacion.setBackgroundColor(Color.TRANSPARENT);

        holder.imgAvatar.setImageURI(Uri.parse(IMG_URL +oNotificacion.user.getAvatar()));
        holder.txtFecha.setText(oDate.DateDiff(oNotificacion.getFecha13(), System.currentTimeMillis()));

        if (!oNotificacion.getTipo().equalsIgnoreCase("REACT")) holder.txtDescr.setText("@" + oNotificacion.user.getNombre() + " " + MSG_NOTIFICACION.get(oNotificacion.getTipo()));
        else holder.txtDescr.setText("@" + oNotificacion.user.getNombre() + " ha reaccionado \""+ oNotificacion.getReactionString(context)+ "\" a tu publicación");
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
    public int getItemCount() {
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
    public long getItemId(int position) {
        return position;
    }

    public Integer getIdPublicacion(int position) {
        return oNotificaciones.get(position).feed.getId();
    }

    public String getUserName(int position) {
        return oNotificaciones.get(position).user.getUsuario();
    }

    public Long get_UltimaFecha() {
        return ultima_fecha;
    }

    public void setNotificaciones(JSONArray response){

        clsNotificacion oNotificacion;

        try {
            //JSONArray jNotificaciones=response.getJSONArray("data");

            int num = response.length();
            for (int i=0; i<num;i++){
                oNotificacion= new clsNotificacion(response.getJSONObject(i));
                ultima_fecha=oNotificacion.getFecha13();
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
            case "USRSEG": tipo="perfil"; break;
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
        return url;
    }


    // Guardar item cargado
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout lytNotificacion;
        public final SimpleDraweeView imgAvatar;
        public final TextView txtDescr, txtFecha;

        public ViewHolder(View itemView){
            super(itemView);

            //this.lytPublicacion = (CardView) itemView.findViewById(R.id.lytPublicacion);
            this.lytNotificacion = (LinearLayout) itemView.findViewById(R.id.lytNotificacion);
            this.imgAvatar = (SimpleDraweeView) itemView.findViewById(R.id.imgAvatar);
            this.txtDescr = (TextView) itemView.findViewById(R.id.txtDescr);
            this.txtFecha= (TextView) itemView.findViewById(R.id.txtFecha);
        }
    }

}
