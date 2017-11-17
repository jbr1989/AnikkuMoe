package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.Chat2PrivadoListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsChatPrivado;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class chat2PrivadoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String API_OLD_URL = AppController.getInstance().getApiOld();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private SuperRecyclerView mRecycler;
    private Chat2PrivadoListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Handler mHandler;

    public static String id;
    public static String name;

    Long intervalo;

    private homeActivity home;
    @Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.avatar) SimpleDraweeView mAvatar;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.txtMensaje) TextView messageET;
    @Bind(R.id.btnComentario) ImageButton sendBtn;

    //region CONSTRUCTOR

    public chat2PrivadoFragment(){}
    public static chat2PrivadoFragment newInstance(Bundle arguments){
        chat2PrivadoFragment f = new chat2PrivadoFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }


    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, rootView);

        SharedPreferences ChatsConfig = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        intervalo=Long.parseLong(ChatsConfig.getString("chat_intervalo", "10"));

        id= getArguments().getString("id");
        name = getArguments().getString("name");

        home = (homeActivity) rootView.getContext();
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.toggleDrawer();
            }
        });
        mTitle.setText(name);

        ArrayList<clsChatPrivado> list = new ArrayList<>();
        mAdapter = new Chat2PrivadoListAdapter(rootView.getContext(),list);

        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecycler.setLayoutManager(mLayoutManager);

        mHandler = new Handler(Looper.getMainLooper());

        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

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

    @Override
    public void onRefresh() {
        //Toast.makeText(getActivity(), "Recargar", Toast.LENGTH_LONG).show();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                cargar_chats();
            }
        }, 2000);
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargar_chats();
    }

    private Runnable onEverySecond=new Runnable() {
        public void run() {
            cargar_chats();
        }
    };


    // region CHATS

    public void cargar_chats(){

        home.setRefreshActionButtonState(true);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "chatsu");
        params.put("acc", "private");
        params.put("id", id);

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mAdapter.setUsuario(response);
                mAdapter.clearChats();
                mAdapter.setChats(response);
                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);

                home.setRefreshActionButtonState(false);

                String url=ROOT_URL+"static-img/"+mAdapter.getAvatar();
                mAvatar.setImageURI(Uri.parse(url));

                if (intervalo!=0) mHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home.setRefreshActionButtonState(false);
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();

                if (intervalo!=0) mHandler.postDelayed(onEverySecond, intervalo*1000);
            }
        }, API_OLD_URL);

        home.requestQueue.add(home.request);
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
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "chatsu");
        params.put("acc", "send");
        params.put("id", id);
        params.put("mensaje", messageText);

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (mAdapter.setNewChats(response)) mAdapter.notifyDataSetChanged();
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

        home.requestQueue.add(home.request);
    }

    public void clearMessage(){
        messageET.setText("");
    }

    // end region


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

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        //mCallback = null;
        super.onDetach();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (intervalo!=0) mHandler.postDelayed(onEverySecond, intervalo*1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(onEverySecond);
    }

}
