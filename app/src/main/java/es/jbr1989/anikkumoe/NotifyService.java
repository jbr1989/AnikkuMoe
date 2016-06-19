package es.jbr1989.anikkumoe;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.ListAdapter.NotificacionListAdapter;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

public class NotifyService extends Service {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    MyTask myTask;
    Context context;

    private static final String TAG = "MyService";

    public NotifyService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        myTask = new MyTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myTask.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        myTask.cancel(true);
        //Toast.makeText(this, "My Service Stopped", Toast.LENGTH_SHORT).show();
    }

    private class MyTask extends AsyncTask<String, String, String> {

        private DateFormat dateFormat;
        private String date;
        private boolean cent;

        public RequestQueue requestQueue;
        public CustomRequest2 request;

        private clsUsuarioSession oUsuarioSession;
        private NotificacionListAdapter oListadoNotificaciones;

        public static final String SP_NAME = "Notificaciones";

        SharedPreferences Config;
        SharedPreferences NotificacionesConfig;
        SharedPreferences.Editor NotificacionesConfigEditor;

        //private NotificationManager nm;
        //private NotificationCompat notificacion;
        //private static final int ID_NOTIFICACION_CREAR = 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            cent = true;

            oUsuarioSession = new clsUsuarioSession(context);
            requestQueue = Volley.newRequestQueue(context);

            oListadoNotificaciones= new NotificacionListAdapter(context);

            Config = PreferenceManager.getDefaultSharedPreferences(context);
            NotificacionesConfig = context.getSharedPreferences(SP_NAME, 0);
            NotificacionesConfigEditor = Config.edit();

            //nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        }

        @Override
        protected String doInBackground(String... param) {

            while(!isCancelled()){

                try {

                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("mdl", "notificaciones");
                    params.put("acc", "obtener");

                    request = new CustomRequest2(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            oListadoNotificaciones.clearNotificaciones();
                            oListadoNotificaciones.setNotificaciones(response);
                            oListadoNotificaciones.putConfigNewsCount();

                            if (oListadoNotificaciones.IfNews()) {
                                notificar(oListadoNotificaciones.getNewsCount() + " notificaciones nuevas.", oListadoNotificaciones.getNovedades());
                            }

                            // setting the nav drawer list adapter
                            //lstNotificaciones.setAdapter(oListadoNotificaciones);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    },ROOT_URL+"api/user/notifications");

                    requestQueue.add(request);

                    String intervalo=Config.getString("notificacion_intervalo", "300");
                    Thread.sleep(Long.parseLong(intervalo)*1000); // Stop 5s

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }



            /*
            while (cent){
                date = dateFormat.format(new Date());
                try {
                    publishProgress(date);
                    // Stop 5s
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/

            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            //Toast.makeText(getApplicationContext(), "Hora actual: " + values[0], Toast.LENGTH_SHORT).show();

            //Notification notificacion = new Notification(R.drawable.logo_anikku, "Creando Servicio de Música", System.currentTimeMillis() );
            //nm.notify(ID_NOTIFICACION_CREAR, notificacion);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cent = false;
            //nm.cancel(ID_NOTIFICACION_CREAR);
        }

        protected void notificar(String titulo, String texto){
            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent resultIntent = new Intent(context, homeActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    getBaseContext())
                    .setSmallIcon(R.drawable.icono_anikku)
                    .setContentTitle(titulo)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                    .setWhen(System.currentTimeMillis());

            builder.setContentIntent(resultPendingIntent);
            //builder.setDefaults(Notification.DEFAULT_SOUND);

            nManager.notify(12345, builder.build());
        }
    }

}
