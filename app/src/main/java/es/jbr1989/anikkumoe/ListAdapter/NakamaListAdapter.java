package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.object.clsNakama;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class NakamaListAdapter extends BaseAdapter {

    //region VARIABLES

    private static final String IMG_URL = AppController.getInstance().getImg();

    public static final String SP_NAME = "Nakamas";

    private Context context;
    private Long fecha;
    private Long ultima_fecha;

    private ArrayList<clsNakama> oNakamas;
    private ArrayList<Integer> oNakamasOnline;

    public homeActivity home;

    //endregion

    //region CONSTRUCTOR

    public NakamaListAdapter(Context context){
        this.context = context;
        this.home = (homeActivity) context;

        oNakamas=new ArrayList<clsNakama>();
        oNakamasOnline=new ArrayList<Integer>();
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

        SimpleDraweeView imgAvatar;
        ImageView imgUsuario;
        TextView  txtUsuario;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nakama_item, parent, false);

            imgAvatar = (SimpleDraweeView) convertView.findViewById(R.id.ImgAvatar);
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

        imgAvatar.setImageURI(Uri.parse(IMG_URL + oNakama.getAvatar()));
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
    }

    public boolean isOnline(Integer id){
        return oNakamasOnline.contains(id);
    }

    // Guardar item cargado
    private static class ViewHolder {
        public final SimpleDraweeView imgAvatar;
        public final ImageView imgUsuario;
        public final TextView txtUsuario;

        public ViewHolder(SimpleDraweeView imgAvatar, TextView txtUsuario, ImageView imgUsuario) {
            this.imgAvatar=imgAvatar;
            this.txtUsuario=txtUsuario;
            this.imgUsuario=imgUsuario;
        }
    }

}
