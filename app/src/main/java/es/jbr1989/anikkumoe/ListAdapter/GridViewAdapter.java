package es.jbr1989.anikkumoe.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.object.clsUser;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 29/09/2016.
 */

public class GridViewAdapter extends BaseAdapter {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    public static final String SP_NAME = "Usuarios";

    private Context context;
    private ArrayList<clsUser> oUsers;
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

    public GridViewAdapter(Context context){
        this.context = context;

        oUsuarioSession = new clsUsuarioSession(context);

        oUsers=new ArrayList<clsUser>();
        nuevos=0;

        MensajesConfig = context.getSharedPreferences(SP_NAME, 0);
        MensajesConfigEditor = MensajesConfig.edit();
    }

    //endregion

    @Override
    public int getCount() {
        return oUsers.size();
    }

    public int getNewsCount(){
        return nuevos;
    }

    public boolean IfNews(){return nuevos>0;}

    @Override
    public Object getItem(int position) {
        return oUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //LinearLayout lytMensaje;
        NetworkImageView imgGridViewItem;
        TextView txtGridViewItem;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);

            //lytMensaje = (LinearLayout) convertView.findViewById(R.id.lytMensaje);
            imgGridViewItem = (NetworkImageView) convertView.findViewById(R.id.imgGridViewItem);
            txtGridViewItem = (TextView) convertView.findViewById(R.id.txtGridViewItem);

            convertView.setTag(new GridViewAdapter.ViewHolder(imgGridViewItem,txtGridViewItem));

        } else{
            GridViewAdapter.ViewHolder viewHolder = (GridViewAdapter.ViewHolder) convertView.getTag();
            imgGridViewItem = viewHolder.imgGridViewItem;
            txtGridViewItem  = viewHolder.txtGridViewItem;
        }

        clsUser oUser = oUsers.get(position);

        //if (oNotificacion.nuevo()==Boolean.TRUE) lytNotificacion.setBackgroundColor(context.getResources().getColor(R.color.notification_new));
        //else lytNotificacion.setBackgroundColor(Color.TRANSPARENT);

        imgGridViewItem.setImageUrl(ROOT_URL+"static-img/"+ oUser.getAvatar(), imageLoader);
        txtGridViewItem.setText(oUser.getNombre());
        return convertView;
    }

    public void setUsers(JSONArray response){

        clsUser oUser;

        try {
            //JSONArray jNotificaciones=response.getJSONArray("data");

            int num = response.length();
            for (int i=0; i<num;i++){
                oUser = new clsUser(response.getJSONObject(i));
                oUsers.add(oUser);
            }

        }catch (JSONException ex){ex.printStackTrace();}

    }

    public void remove(Integer pos){
        oUsers.remove(pos);
    }

    public void clearUsers(){
        oUsers=new ArrayList<clsUser>();
        nuevos=0;
    }

    // Guardar item cargado
    private static class ViewHolder {
        //public final LinearLayout lytMensaje;
        public final NetworkImageView imgGridViewItem;
        public final TextView txtGridViewItem;

        public ViewHolder(NetworkImageView imgGridViewItem,  TextView txtGridViewItem) {
            this.imgGridViewItem=imgGridViewItem;
            this.txtGridViewItem=txtGridViewItem;
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
