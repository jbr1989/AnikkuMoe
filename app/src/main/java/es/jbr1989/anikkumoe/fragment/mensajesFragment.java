package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.MensajeListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsMensaje;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 26/05/2016.
 */
public class mensajesFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private clsUsuarioSession oUsuarioSession;

    private MensajeListAdapter oListadoMensajes;

    private ListView lstMensajes;
    private ArrayList<clsMensaje> oMensajes = new ArrayList<clsMensaje>();

    public RequestQueue requestQueue;
    public CustomRequest2 request;

    private SwipeRefreshLayout swipeContainer;

    public static final String TAG = "ExampleFragment";
    private FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }
    public static mensajesFragment newInstance(Bundle arguments){
        mensajesFragment f = new mensajesFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }
    public mensajesFragment(){}


    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.listado_mensajes, container, false);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        oListadoMensajes= new MensajeListAdapter(rootView.getContext());

        lstMensajes = (ListView) rootView.findViewById(R.id.lstMensajes);
        lstMensajes.setOnItemClickListener(this);
        lstMensajes.setAdapter(oListadoMensajes);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.srlContainer);
        swipeContainer.setOnRefreshListener(this);
        // Set colors to display in widget.
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        return rootView;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cargar_mensajes();
    }

    public void cargar_mensajes(){

        oListadoMensajes.clearMensajes();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        request = new CustomRequest2(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                oListadoMensajes.setMensajes(response);
                oListadoMensajes.putConfigNewsCount();

                // setting the nav drawer list adapter
                oListadoMensajes.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/messages");

        requestQueue.add(request);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // display view for selected nav drawer item
        //Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(oListadoMensajes.getUrl(position)));
        //startActivity(browserIntent1);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                cargar_mensajes();
                // Remove widget from screen.
                swipeContainer.setRefreshing(false);
            }
        }, 3000);
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


}
