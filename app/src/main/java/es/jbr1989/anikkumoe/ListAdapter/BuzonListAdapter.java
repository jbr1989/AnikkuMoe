package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsBuzon;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 26/05/2016.
 */
public class BuzonListAdapter extends RecyclerView.Adapter<BuzonListAdapter.ViewHolder>{

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String IMG_URL = AppController.getInstance().getImg();
    public static final String SP_NAME = "Mensajes";

    private Context context;

    private ArrayList<clsBuzon> oMensajes;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    public RequestQueue requestQueue;
    public CustomRequest request;

    public homeActivity home;

    //endregion

    //region CONSTRUCTOR

    public BuzonListAdapter(Context context){
        this.context = context;

        this.oMensajes=new ArrayList<clsBuzon>();
        nuevos=0;
    }

    public BuzonListAdapter(Context context, ArrayList<clsBuzon> oNotificaciones) {
        this.context = context;
        this.home = (homeActivity) context;

        this.oMensajes=oNotificaciones;
        nuevos=0;
    }

    //endregion


    @Override
    public BuzonListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buzon, parent, false);
        return new BuzonListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BuzonListAdapter.ViewHolder holder, final int position) {

        final clsBuzon oMensaje = oMensajes.get(position);

        holder.imgAvatar.setImageURI(Uri.parse(IMG_URL+oMensaje.getAvatar()));
        holder.txtUsuario.setText(oMensaje.getUsuario());
        holder.txtDescr.setText(oMensaje.getTextoHTML());
        holder.txtFecha.setText((oMensaje.getLast_creator().equals("user") ? "Recibido " : "Enviado ")+oDate.DateDiff(oMensaje.getFecha(), System.currentTimeMillis()));
    }

    @Override
    public int getItemCount() {
        return oMensajes.size();
    }

    public int getNewsCount(){
        return nuevos;
    }

    public clsBuzon getItem(int position){ return oMensajes.get(position);}

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMensajes(JSONArray response){

        clsBuzon oMensaje;

        try {
            //JSONArray jNotificaciones=response.getJSONArray("data");

            int num = response.length();
            for (int i=0; i<num;i++){
                oMensaje= new clsBuzon(response.getJSONObject(i));
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout lytMensaje;
        public final SimpleDraweeView imgAvatar;
        public final TextView txtUsuario, txtDescr, txtFecha;

        public ViewHolder(View itemView) {
            super(itemView);

            this.lytMensaje=(LinearLayout) itemView.findViewById(R.id.lytMensaje);
            this.imgAvatar= (SimpleDraweeView) itemView.findViewById(R.id.imgAvatar);
            this.txtUsuario=(TextView) itemView.findViewById(R.id.txtUsuario);
            this.txtDescr=(TextView) itemView.findViewById(R.id.txtDescr);
            this.txtFecha=(TextView) itemView.findViewById(R.id.txtFecha);
        }
    }

}
