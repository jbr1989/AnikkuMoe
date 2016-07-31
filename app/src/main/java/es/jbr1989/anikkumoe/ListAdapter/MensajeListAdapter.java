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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.object.clsMensaje;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 26/05/2016.
 */
public class MensajeListAdapter extends BaseAdapter {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    public static final String SP_NAME = "Mensajes";

    private Context context;
    private ArrayList<clsMensaje> oMensajes ;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private SharedPreferences MensajesConfig;
    private SharedPreferences.Editor MensajesConfigEditor;

    private Activity activity;
    private LayoutInflater inflater;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //endregion

    //region CONSTRUCTOR

    public MensajeListAdapter(Context context){
        this.context = context;

        this.oMensajes=new ArrayList<clsMensaje>();
        nuevos=0;

        MensajesConfig = context.getSharedPreferences(SP_NAME, 0);
        MensajesConfigEditor = MensajesConfig.edit();

    }

    //endregion

    @Override
    public int getCount() {
        return oMensajes.size();
    }

    public int getNewsCount(){
        return nuevos;
    }

    public boolean IfNews(){return nuevos>0;}

    @Override
    public Object getItem(int position) {
        return oMensajes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout lytMensaje;
        NetworkImageView imgAvatar;
        TextView txtUsuario,txtDescr, txtFecha;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mensaje, null);

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

        clsMensaje oMensaje = oMensajes.get(position);

        //if (oNotificacion.nuevo()==Boolean.TRUE) lytNotificacion.setBackgroundColor(context.getResources().getColor(R.color.notification_new));
        //else lytNotificacion.setBackgroundColor(Color.TRANSPARENT);

        imgAvatar.setImageUrl(ROOT_URL+"static-img/"+oMensaje.getAvatar(), imageLoader);
        txtUsuario.setText(oMensaje.getUsuario());
        txtDescr.setText(oMensaje.getTextoHTML());
        txtFecha.setText(oDate.DateDiff(oMensaje.getFecha(), System.currentTimeMillis()));

        return convertView;
    }

    public void setMensajes(JSONArray response){

        clsMensaje oMensaje;

        try {
            //JSONArray jNotificaciones=response.getJSONArray("data");

            int num = response.length();
            for (int i=0; i<num;i++){
                oMensaje= new clsMensaje(response.getJSONObject(i));
                oMensajes.add(oMensaje);
                //if(oNotificacion.nuevo()) nuevos+=1;
            }

        }catch (JSONException ex){ex.printStackTrace();}

    }

    public void clearMensajes(){
        oMensajes.clear();
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
