package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.MensajeListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 02/08/2016.
 */
public class MensajesPrivadosFragment extends Fragment {

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private clsUsuarioSession oUsuarioSession;

    private MensajeListAdapter oListadoMensajes;

    private ListView lstMensajes;

    public RequestQueue requestQueue;
    public CustomRequest request;

    private SwipeRefreshLayout swipeContainer;

    private homeActivity home;
    @Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.avatar) NetworkImageView mAvatar;
    @Bind(R.id.title) TextView mTitle;

    public static String id;
    public static String name;

    private EditText messageET;
    private ImageButton sendBtn;

    //region CONSTRUCTOR

    public static final String TAG = "ExampleFragment";
    private FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }
    public static MensajesPrivadosFragment newInstance(Bundle arguments){
        MensajesPrivadosFragment f = new MensajesPrivadosFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }
    public MensajesPrivadosFragment(){}

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.listado_mensajes, container, false);
        ButterKnife.bind(this, rootView);
        home = (homeActivity) rootView.getContext();
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.toggleDrawer();
            }
        });

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        oListadoMensajes= new MensajeListAdapter(rootView.getContext());

        lstMensajes = (ListView) rootView.findViewById(R.id.lstMensajes);
        lstMensajes.setAdapter(oListadoMensajes);

        if (getArguments()!=null){
            id= getArguments().getString("id");
            name = getArguments().getString("name");

            if (!name.isEmpty()) mTitle.setText(name);
        }

        messageET = (EditText) rootView.findViewById(R.id.txtMensajeMensaje);
        sendBtn = (ImageButton) rootView.findViewById(R.id.btnMensajeMensaje);

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

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cargar_mensajes();
    }

    /* Listado de todos los mensajes */
    public void cargar_mensajes(){

        oListadoMensajes.clearMensajes();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        request = new CustomRequest(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                oListadoMensajes.setMensajes(response);
                oListadoMensajes.putConfigNewsCount();

                String url=ROOT_URL+"static-img/"+oListadoMensajes.getAvatar();
                mAvatar.setImageUrl(url,imageLoader);

                // setting the nav drawer list adapter
                oListadoMensajes.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/messages/"+name+"?page=0");

        requestQueue.add(request);

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
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("message", messageText);

        request = new CustomRequest(requestQueue, Request.Method.PUT, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                oListadoMensajes.addMensaje(response);
                oListadoMensajes.putConfigNewsCount();

                // setting the nav drawer list adapter
                oListadoMensajes.notifyDataSetChanged();

                clearMessage();
                sendBtn.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                sendBtn.setEnabled(true);
            }
        }, ROOT_URL+"api/user/messages/"+name);

        requestQueue.add(request);
    }

    public void clearMessage(){
        messageET.setText("");
    }

}
