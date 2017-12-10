package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsChat;
import es.jbr1989.anikkumoe.object.clsUsuario;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;
import es.jbr1989.anikkumoe.other.clsTexto;
import es.jbr1989.anikkumoe.sqlite.ChatSQLite;
import es.jbr1989.anikkumoe.sqlite.ConfigSQLite;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class Chat2ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region VARIABLES

    private static final String API_OLD_URL = AppController.getInstance().getApiOld();
    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String IMG_URL = AppController.getInstance().getImg();
    public static final String SP_NAME = "Chats";

    private SuperRecyclerView mRecycler;
    Long intervalo;

    private Handler mHandler;

    final Chat2ListAdapter mAdapter = this;;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private Long primera_fecha, intermedia_fecha, ultima_fecha;

    private Integer id_chat;
    private ArrayList<clsChat> oChats;

    private clsDate oDate = new clsDate();

    private clsUsuarioSession oUsuarioSession;
    private ChatSQLite oChatSQL;
    private ConfigSQLite oConfigSQL;

    public homeActivity home;

    //endregion

    //region CONSTRUCTOR

    public Chat2ListAdapter(Context context){
        this.context = context;

        oUsuarioSession = new clsUsuarioSession(context);

        oChats=new ArrayList<clsChat>();
        ultima_fecha=null;
    }

    public Chat2ListAdapter(Context context, Integer id_chat, SuperRecyclerView mRecycler){
        this.context = context;
        this.home = (homeActivity) context;

        oUsuarioSession = new clsUsuarioSession(context);

        oChatSQL = new ChatSQLite(context);
        oConfigSQL = new ConfigSQLite(context);

        this.id_chat=id_chat;
        this.oChats=new ArrayList<clsChat>();
        this.mRecycler=mRecycler;

        mRecycler.setAdapter(mAdapter);

        SharedPreferences ChatsConfig = PreferenceManager.getDefaultSharedPreferences(home);
        intervalo=Long.parseLong(ChatsConfig.getString("chat_intervalo", "10"));

        mHandler = new Handler(Looper.getMainLooper());

        ultima_fecha=null;
    }
    //endregion

    //region GETTER

    @Override
    public int getItemViewType(int position) {
        if(position == 0) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    public Long get_UltimaFecha() {
        if (oChats.size()==0) return 0L;
        return oChats.get(oChats.size()-1).getEnviado13();
    }

    public Long get_PrimeraFecha(){
        final clsChat oChat=oChats.get(0);
        return oChat.getEnviado13();
    }

    @Override
    public int getItemCount() { return oChats.size()+1;}

    public Object getItem(int position) {
        return oChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean isMe(String id_usuario){
        return (oUsuarioSession.getId().equals(id_usuario));
    }

    public String getUrlUser(clsChat oChat){
        return ROOT_URL+"user/" + oChat.getUsuario();
    }

    public clsUsuario getUsuario(JSONObject response){

        clsUsuario oUsuario;

        try {
            //fecha = response.getLong("ncache");
            JSONObject jData = response.getJSONObject("data");

            JSONObject jUsuario = jData.getJSONObject("usuario"); // usuario
            oUsuario = new clsUsuario(jUsuario);
            return oUsuario;

            //oUsuarios.add(oUsuarioSession);

        }catch (JSONException ex){ex.printStackTrace(); return null;}

    }

    //endregion

    // region VIEWHOLDER

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_more_previous, parent, false);
            return new Chat2ListAdapter.ViewHolderHeader(view);
        }else {//if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
            return new Chat2ListAdapter.ViewHolderItem(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof ViewHolderHeader) {

            ViewHolderHeader header = (ViewHolderHeader) holder;
            header.txtTitle.setText("Cargar anteriores");

            header.txtTitle.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getOlderChats();
                }
            });

        } else if(holder instanceof ViewHolderItem) {

            ViewHolderItem item = (ViewHolderItem) holder;

            final clsChat oChat = oChats.get(position-1);

            setAlignment(item, isMe(oChat.getUser_id()));

            item.imgAvatar.setImageURI(Uri.parse(IMG_URL + oChat.getAvatar()));
            item.txtUsuario.setText(oChat.getNombre());

            item.txtFecha.setText(oDate.DateDiff(oChat.getEnviado13(), System.currentTimeMillis()));
            //txtBody.setText(oChat.getHTMLMensaje());

            String html = "<!DOCTYPE html><html><body style=\"margin:0;\">"+ clsTexto.styles()+oChat.getHTMLMensaje()+"</body></html>";

            item.webBody.setWebViewClient(home.webClient);
            item.webBody.getSettings().setJavaScriptEnabled(true);
            item.webBody.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            item.webBody.setBackgroundColor(0x00000000);

            item.txtUsuario.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    home.cargar_perfil(oChat.getUsuario());
                }
            });

            item.imgAvatar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    home.cargar_perfil(oChat.getUsuario());
                }
            });
        }
    }

    private void setAlignment(ViewHolderItem item, boolean isMe) {
        if (isMe) {
            item.lytBody.setBackgroundResource(R.drawable.in_message_bg);
            //holder.lytBody.setBackgroundColor(Color.RED);

            RelativeLayout.LayoutParams lp =(RelativeLayout.LayoutParams) item.imgAvatar.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            item.imgAvatar.setLayoutParams(lp);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) item.lytBody.getLayoutParams();
            layoutParams.setMargins(5,0,170,0);
            item.lytBody.setLayoutParams(layoutParams);

        } else {
            item.lytBody.setBackgroundResource(R.drawable.out_message_bg);
            //holder.lytBody.setBackgroundColor(Color.BLUE);

            RelativeLayout.LayoutParams lp =(RelativeLayout.LayoutParams) item.imgAvatar.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            item.imgAvatar.setLayoutParams(lp);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) item.lytBody.getLayoutParams();
            layoutParams.setMargins(170,0,5,0);
            item.lytBody.setLayoutParams(layoutParams);

        }
    }

    // Guardar item cargado
    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        public final SimpleDraweeView imgAvatar;
        public final LinearLayout lytBody;
        public final TextView txtUsuario, txtFecha;
        public final WebView webBody;

        public ViewHolderItem(View itemView) {
            super(itemView);

            this.lytBody = (LinearLayout) itemView.findViewById(R.id.lytBody);
            this.imgAvatar = (SimpleDraweeView) itemView.findViewById(R.id.imgAvatar);
            this.txtUsuario = (TextView) itemView.findViewById(R.id.txtUsuario);
            this.txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
            this.webBody= (WebView) itemView.findViewById(R.id.webBody);
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public final TextView txtTitle;

        public ViewHolderHeader(View itemView) {
            super(itemView);

            this.txtTitle = (TextView)itemView.findViewById(R.id.txtHeader);
        }
    }

    // endregion

    // region LISTADO

    public void getChats(Integer id_chat){
        oChats = oChatSQL.obtenerUltimos(id_chat, Long.parseLong(oConfigSQL.getConfig("time_chat_global"))+1);
        if (oChats.size()==0) cargar_chats(); // No hay chats guardados en SQL, cargar los ultimos desde Internet
        else {
            primera_fecha = get_PrimeraFecha();
            intermedia_fecha =primera_fecha;
            ultima_fecha=get_UltimaFecha();
            notifyDataSetChanged();
            //cargar_nuevos_chats(); // Despues de mostrar los ultimos mensajes del SQL cargar si hay nuevos desde la ultima conexión
        }
    }

    public void getOlderChats(){
        ArrayList<clsChat> oChatsOlder = oChatSQL.obtenerUltimos(id_chat, primera_fecha);
        if (oChatsOlder.size()==0) cargar_anteriores_chats(primera_fecha-(24*60*60*1000),0);
        else {
            oChats.addAll(0, oChatsOlder);
            primera_fecha = get_PrimeraFecha();
            intermedia_fecha =primera_fecha;
            notifyItemRangeInserted(0, oChatsOlder.size());
        }
    }

    public void setChats(Integer id_chat, JSONObject response){

        clsChat oChat;

        try {
            JSONArray jChats=response.getJSONArray("messages");

            int num = jChats.length();
            for (int i=num-1; i>=0;i--){
                oChat= new clsChat(jChats.getJSONObject(i));
                oChatSQL.setChat(id_chat, oChat);
                ultima_fecha=oChat.getEnviado13();
                if (i==num-1) primera_fecha = oChat.getEnviado13();
                oChats.add(oChat);
            }

            intermedia_fecha = primera_fecha;
        }catch (JSONException ex){ex.printStackTrace();}

    }

    public Boolean setNewChats(Integer id_chat, JSONObject response){

        clsChat oChat;
        int startIndex = oChats.size()+1;

        try {
            //fecha = response.getLong("ncache");
            JSONArray jChats=response.getJSONArray("messages");

            int num = jChats.length();
            for (int i=0; i<num;i++){
                oChat= new clsChat(jChats.getJSONObject(i));
                oChatSQL.setChat(id_chat, oChat);
                ultima_fecha=oChat.getEnviado13();
                oChats.add(oChat);
            }

            notifyItemRangeInserted(startIndex, num);

            if (num>0)
                oConfigSQL.setConfig("time_chat_global", ultima_fecha.toString());

            return (jChats.length()>0);

        }catch (JSONException ex){ex.printStackTrace();return false;}

    }

    public Integer setOlderChats(JSONObject response, Integer pos){

        clsChat oChat;

        try {
            //fecha = response.getLong("ncache");
            JSONArray jChats=response.getJSONArray("messages");

            int num = jChats.length();
            int olders = 0;

            for (int i=0; i<num;i++){
                oChat= new clsChat(jChats.getJSONObject(i));
                if (oChat.getEnviado13()<primera_fecha) {
                    oChatSQL.setChat(id_chat, oChat);
                    oChats.add(pos+i, oChat);
                    olders++;
                }
                intermedia_fecha=oChat.getEnviado13();
            }

            //notifyItemRangeInserted(pos, olders);

            return olders;

        }catch (JSONException ex){ex.printStackTrace();return -1;}
    }

    public void clearChats(){
        oChats= new ArrayList<clsChat>();
    }

    // endregion

    // region HANDLER

    private Runnable onEverySecond=new Runnable() {
        public void run() {
            if (ultima_fecha!=null) cargar_nuevos_chats();
            else cargar_chats();
        }
    };

    public void startHandler(){
        if (intervalo!=0)  mHandler.postDelayed(onEverySecond, intervalo*1000);
    }

    public void stopHandler(){
        mHandler.removeCallbacks(onEverySecond);
    }

    // endregion

    // region REMOTE

    public void cargar_chats(){

        home.setRefreshActionButtonState(true);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox");
        params.put("id", id_chat.toString());

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setChats(id_chat, response);
                notifyDataSetChanged();
                //mLayoutManager.scrollToPosition(mLayoutManager.findLastVisibleItemPosition());
                mRecycler.setAdapter(mAdapter);

                home.setRefreshActionButtonState(false);
                actualizar_config();

                if (intervalo!=0) mHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home.setRefreshActionButtonState(false);
                Toast.makeText(home, error.toString(), Toast.LENGTH_SHORT).show();

                if (intervalo!=0) mHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, API_OLD_URL);

        home.requestQueue.add(home.request);
    }

    public void cargar_nuevos_chats(){

        home.setRefreshActionButtonState(true);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox_messages_news");
        params.put("fecha", ultima_fecha.toString());
        params.put("id", id_chat.toString());

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (setNewChats(id_chat, response)) {
                    //notifyDataSetChanged();
                    //mRecycler.setAdapter(mAdapter);
                    actualizar_config();
                }
                if (intervalo!=0) mHandler.postDelayed(onEverySecond, intervalo*1000);
                home.setRefreshActionButtonState(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home.setRefreshActionButtonState(false);
                Toast.makeText(home, error.toString(), Toast.LENGTH_SHORT).show();
                if (intervalo!=0) mHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, API_OLD_URL);

        home.requestQueue.add(home.request);
    }

    public void cargar_anteriores_chats(final Long tiempo, final Integer pos){

        home.setRefreshActionButtonState(true);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox_messages_news");
        params.put("fecha", tiempo.toString());
        params.put("id", "1");

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Integer olders = setOlderChats(response, pos);
                if (olders==0)  cargar_anteriores_chats(tiempo-(24*60*60*1000), pos+olders);
                else if (primera_fecha>intermedia_fecha) cargar_anteriores_chats(intermedia_fecha, pos+olders);
                else getOlderChats();

                home.setRefreshActionButtonState(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home.setRefreshActionButtonState(false);
                Toast.makeText(home, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, API_OLD_URL);

        home.requestQueue.add(home.request);
    }

    // endregion

    // region SQL

    // SQL
    public void actualizar_config(){
        ConfigSQLite oConfigSQLite = new ConfigSQLite(home);
        oConfigSQLite.setModConfig("time_chat_global",get_UltimaFecha().toString());
        oConfigSQLite.setModConfig("notification_chat_global","0");
    }

    //endregion

}
