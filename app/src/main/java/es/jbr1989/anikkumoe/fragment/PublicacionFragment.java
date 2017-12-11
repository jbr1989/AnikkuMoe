package es.jbr1989.anikkumoe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import es.jbr1989.anikkumoe.ListAdapter.PublicacionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsPublicacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 27/09/2017.
 */

public class PublicacionFragment extends Fragment {

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    public static final String TAG = "PublicacionFragment";

    private homeActivity home;
    private View rootView;

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;

    private Integer id_publicacion;
    private Boolean comentarios;

    //region CONSTRUCTOR

    public PublicacionFragment(){}

    public static PublicacionFragment newInstance(Bundle arguments){
        PublicacionFragment f = new PublicacionFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.publicacion_info, container, false);

        home = (homeActivity) rootView.getContext();
        home.setTitle("Publicación");

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        if (getArguments()!=null){
            id_publicacion = getArguments().getInt("id_publicacion");
            comentarios=getArguments().getBoolean("ver_comentarios");
            //usuario = extras.getString("usuario");

            cargar_publicacion();
        }

        return rootView;
    }

    public void cargar_publicacion(){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "publicaciones");
        //params.put("acc", "resumen");
        //params.put("publicacion", "0");
        //params.put("uid", "0");

        request = new CustomRequest(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                clsPublicacion oPublicacion= new clsPublicacion(response);

                PublicacionListAdapter oListadoPublicaciones= new PublicacionListAdapter(rootView.getContext());
                PublicacionListAdapter.ViewHolder holder= new PublicacionListAdapter.ViewHolder(rootView);
                oListadoPublicaciones.cargar_publicacion(holder, oPublicacion, 0);

                //if(comentarios) PublicacionListAdapter.ver_comentarios();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(home, error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/activity/"+id_publicacion.toString());

        requestQueue.add(request);
    }

}
