package es.jbr1989.anikkumoe.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.object.clsMensaje;
import es.jbr1989.anikkumoe.object.clsMensajes;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 26/05/2016.
 */
public class MensajeListAdapter extends BaseAdapter {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    public static final String SP_NAME = "Mensajes";

    private Context context;
    private clsMensajes oMensajes;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private clsUsuarioSession oUsuarioSession;

    private SharedPreferences MensajesConfig;
    private SharedPreferences.Editor MensajesConfigEditor;

    private Activity activity;
    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //endregion

    //region CONSTRUCTOR

    public MensajeListAdapter(Context context){
        this.context = context;

        oUsuarioSession = new clsUsuarioSession(context);

        oMensajes=new clsMensajes();
        nuevos=0;

        MensajesConfig = context.getSharedPreferences(SP_NAME, 0);
        MensajesConfigEditor = MensajesConfig.edit();
    }

    //endregion

    @Override
    public int getCount() {
        return oMensajes.oMensajes.size();
    }

    public int getNewsCount(){
        return nuevos;
    }

    public boolean IfNews(){return nuevos>0;}

    @Override
    public Object getItem(int position) {
        return oMensajes.oMensajes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getAvatar(){return oMensajes.user.getAvatar();}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout lytMensaje;
        NetworkImageView imgAvatar;
        TextView txtUsuario,txtDescr, txtFecha;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mensaje_item, null);

            lytMensaje = (LinearLayout) convertView.findViewById(R.id.lytMensaje);
            imgAvatar = (NetworkImageView) convertView.findViewById(R.id.ImgAvatar);
            txtUsuario = (TextView) convertView.findViewById(R.id.txtUsuario);
            txtDescr = (TextView) convertView.findViewById(R.id.txtDescr);
            txtFecha = (TextView) convertView.findViewById(R.id.txtFecha);

            convertView.setTag(new ViewHolder(lytMensaje,imgAvatar,txtUsuario,txtDescr,txtFecha));

        } else{
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            lytMensaje = viewHolder.lytMensaje;
            imgAvatar = viewHolder.imgAvatar;
            txtUsuario = viewHolder.txtUsuario;
            txtDescr = viewHolder.txtDescr;
            txtFecha = viewHolder.txtFecha;
        }

        clsMensaje oMensaje = oMensajes.oMensajes.get(position);

        //if (oNotificacion.nuevo()==Boolean.TRUE) lytNotificacion.setBackgroundColor(context.getResources().getColor(R.color.notification_new));
        //else lytNotificacion.setBackgroundColor(Color.TRANSPARENT);

        imgAvatar.setImageUrl(ROOT_URL+"static-img/"+ (oMensaje.getCreador().equals(oMensajes.user.getId()) ? oMensajes.user.getAvatar() : oUsuarioSession.getAvatar()), imageLoader);
        txtUsuario.setText((oMensaje.getCreador().equals(oMensajes.user.getId()) ? oMensajes.user.getNombre() : oUsuarioSession.getNombre()));
        txtDescr.setText(oMensaje.getTextoHTML());
        txtFecha.setText((oMensaje.getCreador().equals(oMensajes.user.getId()) ? "Recibido " : "Enviado ")+oDate.DateDiff(oMensaje.getFecha(), System.currentTimeMillis()));

        return convertView;
    }

    public void setMensajes(JSONObject response){
        oMensajes=new clsMensajes(response);
    }

    public void addMensaje(JSONObject response){
        oMensajes.oMensajes.add(new clsMensaje(response));
    }

    public void clearMensajes(){
        oMensajes.oMensajes=new ArrayList<clsMensaje>();
        nuevos=0;
    }

    // Guardar item cargado
    private static class ViewHolder {
        public final LinearLayout lytMensaje;
        public final NetworkImageView imgAvatar;
        public final TextView txtUsuario, txtDescr, txtFecha;

        public ViewHolder(LinearLayout lytMensaje, NetworkImageView imgAvatar,  TextView txtUsuario, TextView txtDescr,  TextView txtFecha) {
            this.lytMensaje=lytMensaje;
            this.imgAvatar=imgAvatar;
            this.txtUsuario=txtUsuario;
            this.txtDescr=txtDescr;
            this.txtFecha=txtFecha;
        }
    }

    //region CONFIGURACION

    public void putConfigNewsCount(){
        MensajesConfigEditor.putInt("nuevos", this.getNewsCount());
        MensajesConfigEditor.commit();
    }

    public Integer getConfigNewsCount(){
        return MensajesConfig.getInt("nuevos",0);
    }

    //endregion

}
