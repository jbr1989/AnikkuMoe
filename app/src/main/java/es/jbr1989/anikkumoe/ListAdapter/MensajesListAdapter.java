package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsMensaje;
import es.jbr1989.anikkumoe.object.clsUser;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 26/05/2016.
 */
public class MensajesListAdapter extends RecyclerView.Adapter<MensajesListAdapter.ViewHolder> {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String IMG_URL = AppController.getInstance().getImg();
    public static final String SP_NAME = "Mensajes";

    private Context context;
    private Long ultima_fecha;

    private clsUser oUser;
    private ArrayList<clsMensaje> oMensajes;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private clsUsuarioSession oUsuarioSession;

    private SharedPreferences MensajesConfig;
    private SharedPreferences.Editor MensajesConfigEditor;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public RequestQueue requestQueue;
    public CustomRequest request;
    public CustomRequest2 request2;

    public homeActivity home;

    //endregion

    //region CONSTRUCTOR

    public MensajesListAdapter(Context context){
        this.context = context;

        oUsuarioSession = new clsUsuarioSession(context);

        oMensajes=new ArrayList<clsMensaje>();
        nuevos=0;

        MensajesConfig = context.getSharedPreferences(SP_NAME, 0);
        MensajesConfigEditor = MensajesConfig.edit();
    }

    public MensajesListAdapter(Context context, ArrayList<clsMensaje> oMensajes) {
        this.context = context;
        this.home = (homeActivity) context;

        oUsuarioSession = new clsUsuarioSession(context);

        this.oMensajes=oMensajes;
        nuevos=0;

        MensajesConfig = context.getSharedPreferences(SP_NAME, 0);
        MensajesConfigEditor = MensajesConfig.edit();
    }

    //endregion

    @Override
    public MensajesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MensajesListAdapter.ViewHolder holder, final int position) {

        final clsMensaje oMensaje = oMensajes.get(position);

        holder.imgAvatar.setImageURI(Uri.parse(IMG_URL + (oMensaje.getCreador().equals(oUser.getId()) ? oUser.getAvatar() : oUsuarioSession.getAvatar())));
        holder.txtUsuario.setText((oMensaje.getCreador().equals(oUser.getId()) ? oUser.getNombre() : oUsuarioSession.getNombre()));
        holder.txtDescr.setText(oMensaje.getTextoHTML());
        holder.txtFecha.setText((oMensaje.getCreador().equals(oUser.getId()) ? "Recibido " : "Enviado ")+oDate.DateDiff(oMensaje.getFecha(), System.currentTimeMillis()));

    }

    @Override
    public int getItemCount() {
        return oMensajes.size();
    }

    public clsMensaje getItem(int position){ return oMensajes.get(position);}

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getAvatar(){return oUser.getAvatar();}

    public void setDatos(JSONObject jData){

        try {
            JSONObject user=jData.getJSONObject("user");
            oUser= new clsUser(user);
        }
        catch (JSONException ex){ex.printStackTrace();}

        try{
            JSONArray jMensajes = jData.getJSONArray("data");

            oMensajes= new ArrayList<clsMensaje>();

            for (int i=0;i<jMensajes.length();i++){
                oMensajes.add(new clsMensaje(jMensajes.getJSONObject(i)));
            }

        }catch (JSONException ex){ex.printStackTrace();}

    }


    public void addMensaje(JSONObject response){
        oMensajes.add(new clsMensaje(response));
    }

    public void clearMensajes(){
        oMensajes=new ArrayList<clsMensaje>();
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
