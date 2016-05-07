package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
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
import es.jbr1989.anikkumoe.ListAdapter.PublicacionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class publicacionesFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    public static final String TAG = "publicacionesFragment";
    ProgressDialog pDialog;

    private clsUsuarioSession oUsuarioSession;
    private PublicacionListAdapter oListadoPublicaciones;

    private ListView lstPublicaciones;
    private Button btnMore;

    public RequestQueue requestQueue;
    public CustomRequest request;

    private SwipeRefreshLayout swipeContainer;

    private FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }
    public static publicacionesFragment newInstance(Bundle arguments){
        publicacionesFragment f = new publicacionesFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }
    public publicacionesFragment(){}

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.publicaciones, container, false);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        oListadoPublicaciones= new PublicacionListAdapter(rootView.getContext());

        lstPublicaciones = (ListView) rootView.findViewById(R.id.lstPublicaciones);


        View footerView = View.inflate(rootView.getContext(), R.layout.footer, null);
        btnMore = (Button) footerView.findViewById(R.id.btnNext);
        lstPublicaciones.addFooterView(footerView);

        //lstPublicaciones.setAdapter(oListadoPublicaciones);

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oListadoPublicaciones.get_UltimaFecha()!=null) cargar_nuevas_publicaciones();
                else cargar_publicaciones();
            }
        });

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.srlContainer);
        swipeContainer.setOnRefreshListener(this);
        // Set colors to display in widget.
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        cargando_start(rootView.getContext(), "Cargando...");

        return rootView;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargar_publicaciones();
    }

    public void cargar_publicaciones(){

        btnMore.setEnabled(false);

        oListadoPublicaciones.clearPublicaciones();

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
                oListadoPublicaciones.setPublicaciones(response);
                oListadoPublicaciones.notifyDataSetChanged();
                btnMore.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                btnMore.setEnabled(true);
            }
        },ROOT_URL+"api/user/activity?page=0");

        requestQueue.add(request);
    }

    public void cargar_nuevas_publicaciones(){

        btnMore.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "publicaciones");
        params.put("acc", "resumen");
        params.put("publicacion", oListadoPublicaciones.get_UltimaFecha().toString());
        params.put("uid", "0");

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                oListadoPublicaciones.setPublicaciones(response);
                oListadoPublicaciones.notifyDataSetChanged();
                btnMore.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                btnMore.setEnabled(true);
            }
        });

        requestQueue.add(request);
    }

    /**
     * Slide menu item click listener
     * */
    private class ListClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position,long id) {
            // display view for selected nav drawer item
            //Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(oListadoPublicaciones.getUrl(position)));
            //startActivity(browserIntent1);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                cargar_publicaciones();
                // Remove widget from screen.
                swipeContainer.setRefreshing(false);
            }
        }, 3000);
    }

    public void onClick(final View v) { //check for what button is pressed
        switch (v.getId()) { }
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
        cargando_end();
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        //mCallback = null;
        super.onDetach();
    }


    //region SCROLL

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private onScrollEndListener onScrollEnd;

    public interface onScrollEndListener {void onEnd(int page);}

    /*
    public publicacionesFragment(onScrollEndListener onScrollEnd) {
        super();
        this.onScrollEnd = onScrollEnd;
    }
    public publicacionesFragment(int visibleThreshold, onScrollEndListener onScrollEnd) {
        super();
        this.visibleThreshold = visibleThreshold;
        this.onScrollEnd = onScrollEnd;
    }
    */

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            loading = true;
            if(totalItemCount > visibleThreshold)
                onScrollEnd.onEnd(currentPage);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    //endregion

    //region CARGANDO

    public void cargando_start(Context context, String mensaje){
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(mensaje);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void cargando_msg(String mensaje){
        pDialog.setMessage("Comprobando...");
        pDialog.show();
    }

    public void cargando_end(){
        pDialog.dismiss();
    }

    //endregion

}
