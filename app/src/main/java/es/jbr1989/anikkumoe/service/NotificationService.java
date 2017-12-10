package es.jbr1989.anikkumoe.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsChat;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.sqlite.ConfigSQLite;
import es.jbr1989.anikkumoe.sqlite.RegistroSQLite;

/**
 * Created by jbr1989 on 07/11/2017.
 */

public class NotificationService extends IntentService {

    Context ctx;
    ConfigSQLite oConfigSQLite;
    RegistroSQLite oRegistroSQLite;
    private Thread workerThread = null;

    public NotificationManager manager;
    public SharedPreferences Config;

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String API_OLD = AppController.getInstance().getApiOld();
    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest2 request;

    public NotificationService(){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        oUsuarioSession = new clsUsuarioSession(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        ctx = getApplicationContext();
        oConfigSQLite = new ConfigSQLite(ctx);
        oRegistroSQLite = new RegistroSQLite(ctx);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Config = PreferenceManager.getDefaultSharedPreferences(ctx);

        if(workerThread == null || !workerThread.isAlive()){
            //Toast.makeText(context, "Registro de llamada ACTIVADO", Toast.LENGTH_SHORT).show();

            workerThread = new Thread(new Runnable() {
                public void run() {
                    //mostrarNotificacion(0, "Buscar notificaciones", new Date().toString(), new Date().toString(), 0);
                    if (Config.getBoolean("notificacion_activo_notificaciones", Boolean.FALSE) || Config.getBoolean("notificacion_activo_mensajes", Boolean.FALSE)) buscar_notificaciones();
                    if (Config.getBoolean("notificacion_activo_chat_global", Boolean.FALSE)) buscar_novedades_chat();
                }
            });
            workerThread.start();
        }
    }


    private void mostrarNotificacion(Integer notifyId, String title, String text, String ticker, Integer fragment){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
        mBuilder.setSmallIcon(R.drawable.icono_anikku);
        mBuilder.setContentTitle(title);//("RI Llamadas");
        mBuilder.setContentText(text);//("Registro de llamada ACTIVADO");
        mBuilder.setTicker(ticker);//("Registro de llamada ACTIVADO");
        //mBuilder.setOngoing(true); //NOTIFICACION PERMANENTE

        if (fragment>0) {
            final Intent notificationIntent = new Intent(this, homeActivity.class);
            notificationIntent.putExtra("fragment", fragment);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), notifyId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);
        }

        // Add as notification
        manager.notify(notifyId, mBuilder.build());
    }

    private void buscar_notificaciones(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "notificaciones");
        params.put("acc", "obtener");

        CustomRequest request = new CustomRequest(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Integer numNotif= response.getInt("count");
                    Integer numMP=response.getInt("count_mp");

                    if(Config.getBoolean("notificacion_activo_notificaciones", Boolean.FALSE) && numNotif>0){
                        Integer numNotifAnt = Integer.parseInt(oConfigSQLite.getConfig("notification_notificaciones"));
                        if (numNotif!=numNotifAnt) mostrarNotificacion(1, numNotif+" notificaciones nuevas.", numNotif+" notificaciones nuevas.", numNotif+" notificaciones nuevas.", R.id.nav_FragmentNotificacion);
                    }
                    if(Config.getBoolean("notificacion_activo_mensajes", Boolean.FALSE) && numMP>0) {
                        Integer numMPAnt = Integer.parseInt(oConfigSQLite.getConfig("notification_notificaciones"));
                        if (numNotif!=numMPAnt) mostrarNotificacion(2, numMP+" mensajes privados nuevos.\n", numMP+" mensajes privados nuevos.\n", numMP+" mensajes privados nuevos.\n", R.id.nav_FragmentMensajes);
                    }

                }catch (JSONException ex){ex.printStackTrace();}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"/api/user/notifications/count");

        oRegistroSQLite.setRegistro(new Date(),"buscar_notificaciones","cron","");
        requestQueue.add(request);
    }

    private void buscar_novedades_chat(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox_messages_news");
        params.put("id", "1");
        params.put("fecha", oConfigSQLite.getConfig("time_chat_global"));

        CustomRequest request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray messages= response.getJSONArray("messages");
                    Integer numChat = messages.length();
                    if(numChat>0) {
                        Integer total = numChat + Integer.parseInt(oConfigSQLite.getConfig("notification_chat_global"));
                        clsChat oChat = new clsChat(messages.getJSONObject(numChat-1));

                        oConfigSQLite.setConfig("notification_chat_global", total.toString());
                        oConfigSQLite.setConfig("time_chat_global", String.valueOf(oChat.getEnviado13()));
                        mostrarNotificacion(3, "CHAT GLOBAL", total+" mensajes nuevos.", total+" mensajes nuevos.", R.id.nav_FragmentChatGlobal);

                        if (numChat==15) buscar_novedades_chat(); // CADA VEZ SOLO RECUPERA 15 MENSAJES. Repetir hasta que no haya más mensajes.
                    }

                }catch (JSONException ex){ex.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        },API_OLD);

        oRegistroSQLite.setRegistro(new Date(),"buscar_novedades_chat_global","cron","");
        requestQueue.add(request);
    }

}

