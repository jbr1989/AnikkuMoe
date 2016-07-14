package es.jbr1989.anikkumoe.ListAdapter;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.fragment.perfilFragment;
import es.jbr1989.anikkumoe.object.clsChat;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class ChatListAdapter extends BaseAdapter {

    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    public static final String SP_NAME = "Chats";
    public Integer pos;

    private Context context;
    private Long fecha;
    private Long ultima_fecha;

    private clsUsuarioSession oUsuarioSession;
    private ArrayList<clsChat> oChats;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private SharedPreferences ChatsConfig;
    private SharedPreferences.Editor ChatsConfigEditor;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    //endregion

    //region CONSTRUCTOR

    public ChatListAdapter(Context context){
        this.context = context;

        oUsuarioSession = new clsUsuarioSession(context);

        oChats=new ArrayList<clsChat>();
        nuevos=0;
        ultima_fecha=null;

        ChatsConfig = context.getSharedPreferences(SP_NAME, 0);
        ChatsConfigEditor = ChatsConfig.edit();

    }
    //endregion

    //region GETTER

    public Long getFecha() {
        return fecha;
    }

    public Long get_UltimaFecha() {
        return ultima_fecha;
    }

    public Long get_PrimeraFecha(){
        final clsChat oChat=oChats.get(0);
        return oChat.getEnviado13();
    }

    public Long get_AnteriorFecha(long tiempo){
        return get_PrimeraFecha()-tiempo;
    }

    //endregion

    @Override
    public int getCount() { return oChats.size();}

    @Override
    public Object getItem(int position) {
        return oChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        NetworkImageView imgAvatar;
        LinearLayout lytBody;
        TextView  txtUsuario, txtFecha;
        WebView webBody;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
            viewHolder =new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else{
            viewHolder  = (ViewHolder) convertView.getTag();
        }

        final clsChat oChat=oChats.get(position);

        setAlignment(viewHolder, isMe(oChat.getUser_id()));

        viewHolder.imgAvatar.setImageUrl(ROOT_URL + "/static-img/" + oChat.getAvatar(), imageLoader);
        viewHolder.txtUsuario.setText(oChat.getNombre());

        viewHolder.txtFecha.setText(oDate.DateDiff(oChat.getEnviado13(), System.currentTimeMillis()));
        //txtBody.setText(oChat.getHTMLMensaje());
        viewHolder.webBody.loadDataWithBaseURL(null, oChat.getHTMLMensaje(), "text/html", "UTF-8", null);
        viewHolder.webBody.setBackgroundColor(0x00000000);

        viewHolder.txtUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cargar_perfil(oChat.getUsuario());
            }
        });

        viewHolder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cargar_perfil(oChat.getUsuario());
            }
        });

        return convertView;
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (isMe) {
            holder.lytBody.setBackgroundResource(R.drawable.in_message_bg);

            RelativeLayout.LayoutParams lp =(RelativeLayout.LayoutParams) holder.imgAvatar.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.imgAvatar.setLayoutParams(lp);

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

    public void cargar_perfil(String usuario){
        Bundle arguments = new Bundle();
        arguments.putString("usuario", usuario);

        Fragment fragment = perfilFragment.newInstance(arguments);

        if (context instanceof homeActivity) {
            homeActivity feeds = (homeActivity) context;
            feeds.switchContent(fragment);
        }
    }

    public boolean isMe(String id_usuario){
        return (oUsuarioSession.getId().equals(id_usuario));
    }

    public String getUrlUser(clsChat oChat){
        return ROOT_URL+"user/" + oChat.getUsuario();
    }

    public void setChats(JSONObject response){

        clsChat oChat;

        try {
            //fecha = response.getLong("ncache");
            JSONArray jChats=response.getJSONArray("messages");

            int num = jChats.length();
            for (int i=num-1; i>=0;i--){
                oChat= new clsChat(jChats.getJSONObject(i));
                if (i==0) ultima_fecha=oChat.getEnviado13();
                oChats.add(oChat);
                //if(oPublicacion.nuevo()) nuevos+=1;
            }

        }catch (JSONException ex){ex.printStackTrace();}

    }

    public boolean setNewChats(JSONObject response){

        clsChat oChat;

        try {
            //fecha = response.getLong("ncache");
            JSONArray jChats=response.getJSONArray("messages");

            int num = jChats.length();
            for (int i=0; i<num;i++){
                oChat= new clsChat(jChats.getJSONObject(i));
                ultima_fecha=oChat.getEnviado13();
                oChats.add(oChat);
                //if(oPublicacion.nuevo()) nuevos+=1;
            }

            return jChats.length()>0;

        }catch (JSONException ex){ex.printStackTrace();return false;}

    }

    public boolean setOlderChats(JSONObject response){

        clsChat oChat;

        try {
            //fecha = response.getLong("ncache");
            JSONArray jChats=response.getJSONArray("messages");

            int num = jChats.length();
            for (int i=0; i<num;i++){
                oChat= new clsChat(jChats.getJSONObject(i));
                //ultima_fecha=oChat.getEnviado13();
                oChats.add(0, oChat);
                //if(oPublicacion.nuevo()) nuevos+=1;
            }

            return jChats.length()>0;

        }catch (JSONException ex){ex.printStackTrace();return false;}
    }

    public void clearPublicaciones(){
        oChats.clear();
        nuevos=0;
    }

    // Guardar item cargado
    private static class ViewHolder {
        public final NetworkImageView imgAvatar;
        public final LinearLayout lytBody;
        public final TextView txtUsuario, txtFecha;
        public final WebView webBody;

        public ViewHolder(View v){
            this.lytBody = (LinearLayout) v.findViewById(R.id.lytBody);
            this.imgAvatar = (NetworkImageView) v.findViewById(R.id.ImgAvatar);
            this.txtUsuario = (TextView) v.findViewById(R.id.txtUsuario);
            this.txtFecha = (TextView) v.findViewById(R.id.txtFecha);
            this.webBody= (WebView) v.findViewById(R.id.webBody);
        }

        public ViewHolder( NetworkImageView imgAvatar, LinearLayout lytBody,  TextView txtUsuario,TextView txtFecha, WebView webBody) {
            this.imgAvatar=imgAvatar;
            this.lytBody= lytBody;
            this.txtFecha=txtFecha;
            this.txtUsuario=txtUsuario;
            this.webBody=webBody;
        }
    }

}
