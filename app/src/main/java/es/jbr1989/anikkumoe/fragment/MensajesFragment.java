package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.MensajesListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsMensaje;

/**
 * Created by jbr1989 on 02/08/2016.
 */
public class MensajesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private SuperRecyclerView mRecycler;
    private MensajesListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Handler mHandler;

    private homeActivity home;
    @Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.avatar) NetworkImageView mAvatar;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.txtMensajeMensaje) TextView messageET;
    @Bind(R.id.btnMensajeMensaje) ImageButton sendBtn;

    public static String id;
    public static String name;

    //region CONSTRUCTOR

    public MensajesFragment(){}

    public static MensajesFragment newInstance(Bundle arguments){
        MensajesFragment f = new MensajesFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_mensajes, container, false);
        ButterKnife.bind(this, rootView);
        home = (homeActivity) rootView.getContext();
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.toggleDrawer();
            }
        });

        ArrayList<clsMensaje> list = new ArrayList<>();
        mAdapter = new MensajesListAdapter(rootView.getContext(),list);

        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecycler.setLayoutManager(mLayoutManager);
/*
        mRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(rootView.getContext(), mRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        itemClick(view, position);
                    }
                })
        );
*/
        mHandler = new Handler(Looper.getMainLooper());

        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        if (getArguments()!=null){
            id= getArguments().getString("id");
            name = getArguments().getString("name");

            if (!name.isEmpty()) mTitle.setText(name);
        }

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

        return rootView;
    }

    @Override
    public void onRefresh() {
        //Toast.makeText(getActivity(), "Recargar", Toast.LENGTH_LONG).show();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                cargar_mensajes();
                //mAdapter.add("New stuff");
            }
        }, 2000);
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargar_mensajes();
    }

    /* Listado de todos los mensajes */
    public void cargar_mensajes(){

        mAdapter.clearMensajes();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mAdapter.setDatos(response);

                String url=ROOT_URL+"static-img/"+mAdapter.getAvatar();
                mAvatar.setImageUrl(url,imageLoader);

                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/messages/"+name+"?page=0");

        home.requestQueue.add(home.request);

    }

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

    public void sendMessage(String messageText){

        sendBtn.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("message", messageText);

        home.request = new CustomRequest(home.requestQueue, Request.Method.PUT, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.addMensaje(response);

                clearMessage();
                sendBtn.setEnabled(true);

                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                sendBtn.setEnabled(true);
            }
        }, ROOT_URL+"api/user/messages/"+name);

        home.requestQueue.add(home.request);
    }

    public void clearMessage(){
        messageET.setText("");
    }

}
