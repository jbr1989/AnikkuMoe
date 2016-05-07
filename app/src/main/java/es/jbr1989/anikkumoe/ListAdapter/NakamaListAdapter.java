package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.object.clsNakama;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class NakamaListAdapter extends BaseAdapter {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    public static final String SP_NAME = "Nakamas";
    public Integer pos;

    private Context context;
    private Long fecha;
    private Long ultima_fecha;

    private ArrayList<clsNakama> oNakamas;
    private ArrayList<Integer> oNakamasOnline;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //endregion

    //region CONSTRUCTOR

    public NakamaListAdapter(Context context){
        this.context = context;

        oNakamas=new ArrayList<clsNakama>();
        oNakamasOnline=new ArrayList<Integer>();
        nuevos=0;
        ultima_fecha=null;

    }
    //endregion

    //region GETTER

    public Long getFecha() {
        return fecha;
    }

    public Long get_UltimaFecha() {
        return ultima_fecha;
    }

    //endregion

    @Override
    public int getCount() { return oNakamas.size();}

    @Override
    public Object getItem(int position) {
        return oNakamas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getId(int position){
        return oNakamas.get(position).getId().toString();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        NetworkImageView imgAvatar;
        ImageView imgUsuario;
        TextView  txtUsuario;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nakama_item, parent, false);

            imgAvatar = (NetworkImageView) convertView.findViewById(R.id.ImgAvatar);
            txtUsuario = (TextView) convertView.findViewById(R.id.txtUsuario);
            imgUsuario = (ImageView) convertView.findViewById(R.id.imgUsuario);

            convertView.setTag(new ViewHolder(imgAvatar,txtUsuario, imgUsuario));

        } else{
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            imgAvatar = viewHolder.imgAvatar;
            txtUsuario = viewHolder.txtUsuario;
            imgUsuario = viewHolder.imgUsuario;
        }

        final clsNakama oNakama=oNakamas.get(position);

        imgAvatar.setImageUrl(ROOT_URL + "/static-img/" + oNakama.getAvatar(), imageLoader);
        txtUsuario.setText(oNakama.getNombre());
        imgUsuario.setVisibility((isOnline(oNakama.getId()) ? View.VISIBLE : View.GONE));

        return convertView;
    }

    public boolean setNakamas(JSONObject response){

        clsNakama oNakama;
        boolean nuevos=false;

        try {
            fecha = response.getLong("ncache");
            JSONArray jNakamas=response.getJSONArray("data");

            int num = jNakamas.length();
            for (int i=0; i<num;i++){
                oNakama= new clsNakama(jNakamas.getJSONObject(i));
                //if (!oNakamas.contains(oNakama)) {
                    oNakamas.add(oNakama);
                    nuevos=true;
                //}
            }

            return nuevos;
        }catch (JSONException ex){ex.printStackTrace(); return false;}

    }

    public void clearNakamas(){
        oNakamas.clear();
        nuevos=0;
    }

    public boolean setNakamasOn(JSONObject response){

        clsNakama oNakama;
        boolean nuevos=false;

        try {
            fecha = response.getLong("ncache");
            JSONArray jNakamas=response.getJSONArray("data");

            Integer num = jNakamas.length();
            if (num!=0) {

                for (int i = 0; i < num; i++) {
                    oNakama = new clsNakama(jNakamas.getJSONObject(i));
                    if (!oNakamasOnline.contains(oNakama.getId())) {
                        oNakamasOnline.add(oNakama.getId());
                        nuevos = true;
                    }
                }
            }

            return nuevos;
        }catch (JSONException ex){
            ex.printStackTrace();
            return false;
        }

    }

    public void clearNakamasOn(){
        oNakamasOnline.clear();
        nuevos=0;
    }

    public boolean isOnline(Integer id){
        return oNakamasOnline.contains(id);
    }

    // Guardar item cargado
    private static class ViewHolder {
        public final NetworkImageView imgAvatar;
        public final ImageView imgUsuario;
        public final TextView txtUsuario;

        public ViewHolder(NetworkImageView imgAvatar, TextView txtUsuario, ImageView imgUsuario) {
            this.imgAvatar=imgAvatar;
            this.txtUsuario=txtUsuario;
            this.imgUsuario=imgUsuario;
        }
    }

}
