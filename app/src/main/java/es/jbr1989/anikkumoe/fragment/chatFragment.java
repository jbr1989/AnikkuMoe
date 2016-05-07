package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.ChatListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class chatFragment extends Fragment {

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


    private FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }
    public static chatFragment newInstance(Bundle arguments){
        chatFragment f = new chatFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }
    public chatFragment(){}

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.chat, container, false);
        cargar_preferencias(rootView);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        oListadoChats= new ChatListAdapter(rootView.getContext());

        lstChats = (ListView) rootView.findViewById(R.id.lstChat);

        //View headerView = View.inflate(rootView.getContext(), R.layout.header, null);
        //lstChats.addHeaderView(headerView);

        //View footerView = View.inflate(rootView.getContext(), R.layout.footer, null);
        //lstChats.addFooterView(footerView);

        lstChats.setAdapter(oListadoChats);

        messageET = (EditText) rootView.findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) rootView.findViewById(R.id.chatSendButton);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    Toast.makeText(getActivity(), "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(messageText);
            }
        });

        //if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);

        return rootView;
    }


    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargar_chats();
    }

    private Runnable onEverySecond=new Runnable() {
        public void run() {
            // do real work here
            Toast.makeText(getActivity(), "Buscando nuevos mensajes...", Toast.LENGTH_SHORT).show();

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

                if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();

                if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);
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
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                sendBtn.setEnabled(true);
            }
        }, API_OLD_URL);

        requestQueue.add(request);
    }

    public void clearMessage(){
        messageET.setText("");
    }


    // end region

    /*
    //El fragment se ha adjuntado al Activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    //El Fragment ha sido creado
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //La vista ha sido creada y cualquier configuración guardada está cargada
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    //El Activity que contiene el Fragment ha terminado su creación
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    */

    @Override
    public void onResume() {
        super.onResume();
        if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(onEverySecond);
    }


}
