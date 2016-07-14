package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
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
import es.jbr1989.anikkumoe.ListAdapter.NotificacionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.PublicacionActivity;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsNotificacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 06/12/2015.
 */
public class notificacionesFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private clsUsuarioSession oUsuarioSession;

    private NotificacionListAdapter oListadoNotificaciones;

    private ListView lstNotificaciones;
    private ArrayList<clsNotificacion> oNotificaciones = new ArrayList<clsNotificacion>();

    public RequestQueue requestQueue;
    public CustomRequest2 request;

    private SwipeRefreshLayout swipeContainer;

    public static final String TAG = "ExampleFragment";
    private FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }
    public static notificacionesFragment newInstance(Bundle arguments){
        notificacionesFragment f = new notificacionesFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }
    public notificacionesFragment(){}


    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.notificaciones, container, false);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        oListadoNotificaciones= new NotificacionListAdapter(rootView.getContext());

        lstNotificaciones = (ListView) rootView.findViewById(R.id.lstNotificaciones);
        lstNotificaciones.setOnItemClickListener(this);
        lstNotificaciones.setAdapter(oListadoNotificaciones);

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

        cargar_notificaciones();
    }

    public void cargar_notificaciones(){

        oListadoNotificaciones.clearNotificaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        request = new CustomRequest2(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                oListadoNotificaciones.setNotificaciones(response);
                oListadoNotificaciones.putConfigNewsCount();

                // setting the nav drawer list adapter
                oListadoNotificaciones.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/notifications");

        requestQueue.add(request);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // display view for selected nav drawer item

        Intent i;

        switch(oListadoNotificaciones.getTipo(position)){
            case "url":
                i = new Intent(Intent.ACTION_VIEW, Uri.parse(oListadoNotificaciones.getUrl(position)));
                startActivity(i);
                break;
            case "publicacion":
                i = new Intent(getActivity(), PublicacionActivity.class);
                i.putExtra("id_publicacion", oListadoNotificaciones.getIdPublicacion(position));
                i.putExtra("ver_comentarios", false);
                startActivity(i);
                break;
            case "publicacion_comentario":
                i = new Intent(getActivity(), PublicacionActivity.class);
                i.putExtra("id_publicacion", oListadoNotificaciones.getIdPublicacion(position));
                i.putExtra("ver_comentarios", true);
                startActivity(i);
                break;
            case "perfil":

                Bundle arguments = new Bundle();
                arguments.putString("usuario", oListadoNotificaciones.getUserName(position));

                Fragment fragment =perfilFragment.newInstance(arguments);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                break;
        }

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                cargar_notificaciones();
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
