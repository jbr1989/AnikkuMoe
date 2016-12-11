package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.object.clsChatPrivado;
import es.jbr1989.anikkumoe.object.clsUsuario;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class Chat2PrivadoListAdapter extends RecyclerView.Adapter<Chat2PrivadoListAdapter.ViewHolder> {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    public static final String SP_NAME = "Chats";

    private Context context;
    private Long ultima_fecha;

    private clsUsuarioSession oUsuarioSession;
    private ArrayList<clsChatPrivado> oChats;
    private clsUsuario oUsuario;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private SharedPreferences ChatsConfig;
    private SharedPreferences.Editor ChatsConfigEditor;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public homeActivity home;

    //endregion

    //region CONSTRUCTOR

    public Chat2PrivadoListAdapter(Context context){
        this.context = context;

        oUsuarioSession = new clsUsuarioSession(context);

        oChats=new ArrayList<clsChatPrivado>();
        oUsuario = new clsUsuario();
        nuevos=0;
        ultima_fecha=null;

        ChatsConfig = context.getSharedPreferences(SP_NAME, 0);
        ChatsConfigEditor = ChatsConfig.edit();

    }

    public Chat2PrivadoListAdapter(Context context, ArrayList<clsChatPrivado> oChats){
        this.context = context;
        this.home = (homeActivity) context;

        oUsuarioSession = new clsUsuarioSession(context);

        this.oChats=oChats;
        nuevos=0;
        ultima_fecha=null;

        ChatsConfig = context.getSharedPreferences(SP_NAME, 0);
        ChatsConfigEditor = ChatsConfig.edit();
    }

    //endregion

    @Override
    public Chat2PrivadoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new Chat2PrivadoListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Chat2PrivadoListAdapter.ViewHolder holder, final int position) {

        final clsChatPrivado oChat=oChats.get(position);
        final boolean isMe = isMe(oChat.getId_de());

        setAlignment(holder, isMe);

        holder.imgAvatar.setImageUrl(ROOT_URL + "/static-img/" + (isMe ? oUsuarioSession.getAvatar() : oUsuario.getAvatar()), imageLoader);
        holder.txtUsuario.setText((isMe ? oUsuarioSession.getNombre() : oUsuario.getNombre()));

        holder.txtFecha.setText(oDate.DateDiff(oChat.getEnviado13(), System.currentTimeMillis()));
        //txtBody.setText(oChat.getHTMLMensaje());

        holder.webBody.setWebViewClient(home.webClient);
        holder.webBody.getSettings().setJavaScriptEnabled(true);
        holder.webBody.loadDataWithBaseURL(null, oChat.getHTMLMensaje(), "text/html", "UTF-8", null);
        holder.webBody.setBackgroundColor(0x00000000);

        holder.txtUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                home.cargar_perfil((isMe ? oUsuarioSession.getUsuario() : oUsuario.getUsuario()));
            }
        });

        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                home.cargar_perfil((isMe ? oUsuarioSession.getUsuario() : oUsuario.getUsuario()));
            }
        });
    }


    //region GETTER

    public Long get_UltimaFecha() {
        return ultima_fecha;
    }

    public Long get_PrimeraFecha(){
        final clsChatPrivado oChat=oChats.get(0);
        return oChat.getEnviado13();
    }

    public Long get_AnteriorFecha(long tiempo){
        return get_PrimeraFecha()-tiempo;
    }

    public String getAvatar(){ return oUsuario.getAvatar();}

    //endregion

    @Override
    public int getItemCount() { return oChats.size();}

    public Object getItem(int position) {
        return oChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {

        if (isMe) {
            holder.lytBody.setBackgroundResource(R.drawable.in_message_bg);

            RelativeLayout.LayoutParams lp =(RelativeLayout.LayoutParams) holder.imgAvatar.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.imgAvatar.setLayoutParams(lp);

            holder.imgAvatar.getLayoutParams().width=150;
            holder.imgAvatar.getLayoutParams().height=150;

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.lytBody.getLayoutParams();
            layoutParams.setMargins(5,0,170,0);
            holder.lytBody.setLayoutParams(layoutParams);

        } else {
            holder.lytBody.setBackgroundResource(R.drawable.out_message_bg);

            RelativeLayout.LayoutParams lp =(RelativeLayout.LayoutParams) holder.imgAvatar.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.imgAvatar.setLayoutParams(lp);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.lytBody.getLayoutParams();
            layoutParams.setMargins(170,0,5,0);
            holder.lytBody.setLayoutParams(layoutParams);

        }
    }


    public boolean isMe(String id_usuario){
        return (oUsuarioSession.getId().equals(id_usuario));
    }


    public void setUsuario(JSONObject response){

        try {
            //fecha = response.getLong("ncache");
            JSONObject jData = response.getJSONObject("data");

            JSONObject jUsuario = jData.getJSONObject("usuario"); // usuario
            oUsuario = new clsUsuario(jUsuario);

        }catch (JSONException ex){ex.printStackTrace();}

    }

    public void setChats(JSONObject response){

        clsChatPrivado oChat;

        try {
            //fecha = response.getLong("ncache");
            JSONObject jData = response.getJSONObject("data");
            JSONArray jChats=jData.getJSONArray("mensajes");

            int num =jChats.length();
            for (int i=num-1; i>=0;i--){
                oChat= new clsChatPrivado(jChats.getJSONObject(i));
                if (i==0) ultima_fecha=oChat.getEnviado13();
                oChats.add(oChat);
                //if(oPublicacion.nuevo()) nuevos+=1;
            }

        }catch (JSONException ex){ex.printStackTrace();}

    }

    public boolean setNewChats(JSONObject response){

        clsChatPrivado oChat;

        try {
            //fecha = response.getLong("ncache");
            JSONArray jChats=response.getJSONArray("messages");

            int num = jChats.length();
            for (int i=0; i<num;i++){
                oChat= new clsChatPrivado(jChats.getJSONObject(i));
                ultima_fecha=oChat.getEnviado13();
                oChats.add(oChat);
                //if(oPublicacion.nuevo()) nuevos+=1;
            }

            return jChats.length()>0;

        }catch (JSONException ex){ex.printStackTrace();return false;}

    }

    public void clearChats(){
        oChats= new ArrayList<clsChatPrivado>();
        nuevos=0;
    }

    // Guardar item cargado
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final NetworkImageView imgAvatar;
        public final LinearLayout lytBody;
        public final TextView txtUsuario, txtFecha;
        public final WebView webBody;

        public ViewHolder(View itemView){
            super(itemView);

            this.lytBody = (LinearLayout) itemView.findViewById(R.id.lytBody);
            this.imgAvatar = (NetworkImageView) itemView.findViewById(R.id.imgAvatar);
            this.txtUsuario = (TextView) itemView.findViewById(R.id.txtUsuario);
            this.txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
            this.webBody= (WebView) itemView.findViewById(R.id.webBody);
        }

    }

}
