package es.jbr1989.anikkumoe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.ChatListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 13/04/2016.
 */
public class chatActivity extends Activity {

    private static final String API_OLD_URL = AppController.getInstance().getApiOld();

    public static final String TAG = "chatFragment";
    ProgressDialog pDialog;

    private clsUsuarioSession oUsuarioSession;
    private ChatListAdapter oListadoChats;

    private ListView lstChats;

    public RequestQueue requestQueue;
    public CustomRequest request;

    Handler timerHandler = new Handler();

    private EditText messageET;
    private ImageButton sendBtn;

    public static final String SP_NAME = "Chats";

    Long intervalo;

    private com.basgeekball.awesomevalidation.AwesomeValidation mAwesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        oUsuarioSession = new clsUsuarioSession(getBaseContext());
        requestQueue = Volley.newRequestQueue(getBaseContext());

        oListadoChats= new ChatListAdapter(getBaseContext());

        lstChats = (ListView) findViewById(R.id.lstChat);

        //View headerView = View.inflate(rootView.getContext(), R.layout.header, null);
        //lstChats.addHeaderView(headerView);

        //View footerView = View.inflate(rootView.getContext(), R.layout.footer, null);
        //lstChats.addFooterView(footerView);

        lstChats.setAdapter(oListadoChats);

        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) findViewById(R.id.chatSendButton);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    Toast.makeText(chatActivity.this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(messageText);
            }
        });

        //if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);

        cargar_chats();
    }

    private Runnable onEverySecond=new Runnable() {
        public void run() {
            // do real work here
            Toast.makeText(chatActivity.this, "Buscando nuevos mensajes...", Toast.LENGTH_SHORT).show();

            if (oListadoChats.get_UltimaFecha()!=null) cargar_nuevos_chats();
            else cargar_chats();

            //timerHandler.postDelayed(onEverySecond, intervalo*1000);
        }
    };

    private void cargar_preferencias(View view){

        SharedPreferences ChatsConfig = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        intervalo=Long.parseLong(ChatsConfig.getString("chat_intervalo", "10"));
    }

    // region CHATS

    public void cargar_chats(){

        oListadoChats.clearPublicaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox");
        params.put("id", "1");

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                oListadoChats.setChats(response);
                oListadoChats.notifyDataSetChanged();

                if (intervalo!=null) timerHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(chatActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

                if (intervalo!=null) timerHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, API_OLD_URL);

        requestQueue.add(request);
    }

    public void cargar_nuevos_chats(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox_messages_news");
        params.put("fecha", oListadoChats.get_UltimaFecha().toString());
        params.put("id", "1");

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (oListadoChats.setNewChats(response)) oListadoChats.notifyDataSetChanged();
                if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(chatActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, API_OLD_URL);

        requestQueue.add(request);
    }

    public void cargar_anteriores_chats(long tiempo){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox_messages_news");
        params.put("fecha", oListadoChats.get_AnteriorFecha(tiempo).toString());
        params.put("id", "1");

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (oListadoChats.setOlderChats(response)) oListadoChats.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(chatActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, API_OLD_URL);

        requestQueue.add(request);
    }

    public void sendMessage(String messageText){

        sendBtn.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox_message");
        params.put("id", "1");
        params.put("texto", messageText);

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (oListadoChats.setNewChats(response)) oListadoChats.notifyDataSetChanged();
                clearMessage();
                sendBtn.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(chatActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                sendBtn.setEnabled(true);
            }
        }, API_OLD_URL);

        requestQueue.add(request);
    }

    public void clearMessage(){
        messageET.setText("");
    }


    // end region

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (intervalo!=null) timerHandler.postDelayed(onEverySecond, intervalo*1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(onEverySecond);
    }


}
