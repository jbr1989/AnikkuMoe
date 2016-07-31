package es.jbr1989.anikkumoe.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import es.jbr1989.anikkumoe.ListAdapter.ChatPrivadoListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class chatPrivadoFragment extends Fragment {

    private static final String API_OLD_URL = AppController.getInstance().getApiOld();

    public static final String TAG = "chatFragment";
    ProgressDialog pDialog;

    private clsUsuarioSession oUsuarioSession;
    private ChatPrivadoListAdapter oListadoChats;

    private ListView lstChats;

    public RequestQueue requestQueue;
    public CustomRequest request;

    Handler timerHandler = new Handler();

    private EditText messageET;
    private ImageButton sendBtn;

    public static final String SP_NAME = "Chats";
    public static String id;
    public static String name;

    Long intervalo;

    private homeActivity home;

    private FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }
    public static chatPrivadoFragment newInstance(Bundle arguments){
        chatPrivadoFragment f = new chatPrivadoFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }
    public chatPrivadoFragment(){}

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.chat, container, false);
        cargar_preferencias(rootView);

        home = (homeActivity) rootView.getContext();
        home.id_menu=R.menu.chat_menu;
        home.onCreateOptionsMenu(home.optionsMenu);
        home.setTitle(name);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        oListadoChats= new ChatPrivadoListAdapter(rootView.getContext());

        lstChats = (ListView) rootView.findViewById(R.id.lstChat);
        lstChats.setAdapter(oListadoChats);

        messageET = (EditText) rootView.findViewById(R.id.txtMensaje);
        sendBtn = (ImageButton) rootView.findViewById(R.id.btnComentario);

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
            //Toast.makeText(getActivity(), "Buscando nuevos mensajes...", Toast.LENGTH_SHORT).show();

            //if (oListadoChats.get_UltimaFecha()!=null) cargar_nuevos_chats();
            //else cargar_chats();

            cargar_chats();
        }
    };

    private void cargar_preferencias(View view){

        SharedPreferences ChatsConfig = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        intervalo=Long.parseLong(ChatsConfig.getString("chat_intervalo", "10"));

        id= getArguments().getString("id");
        name = getArguments().getString("name");
    }

    // region CHATS

    public void cargar_chats(){

        home.setRefreshActionButtonState(true);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "chatsu");
        params.put("acc", "private");
        params.put("id", id);

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                oListadoChats.setUsuarios(response);
                oListadoChats.addUsuario(oUsuarioSession.getUsuarioSession());
                oListadoChats.clearChats();
                oListadoChats.setChats(response);
                oListadoChats.notifyDataSetChanged();
                home.setRefreshActionButtonState(false);

                if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home.setRefreshActionButtonState(false);
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();

                if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, API_OLD_URL);

        requestQueue.add(request);
    }

    /*
    public void cargar_nuevos_chats(){

    home.setRefreshActionButtonState(true);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox_messages_news");
        params.put("fecha", oListadoChats.get_UltimaFecha().toString());
        params.put("id", id);

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (oListadoChats.setNewChats(response)) oListadoChats.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }
    */

    public void sendMessage(String messageText){

        sendBtn.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "chatsu");
        params.put("acc", "send");
        params.put("id", id);
        params.put("mensaje", messageText);

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
        super.onPause();
        if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(onEverySecond);
    }

}
