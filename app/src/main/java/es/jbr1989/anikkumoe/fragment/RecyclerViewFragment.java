package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.PublicacionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsPublicacion;


public class RecyclerViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener{
    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private homeActivity home;

    private SuperRecyclerView          mRecycler;
    private PublicacionListAdapter          mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Handler                    mHandler;

    private String tipo;
    private String valor;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    public static RecyclerViewFragment newInstance(Context context, String tipo, String valor) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        //Bundle args = new Bundle();
        //args.putSerializable(ARG_TAB_NAME, tabName);
        //fragment.setArguments(args);
        //fragment.setRetainInstance(true);
        fragment.home=(homeActivity) context;
        fragment.tipo=tipo;
        fragment.valor= valor;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_simple, container, false);
        ButterKnife.bind(this, rootView);

        ArrayList<clsPublicacion> list = new ArrayList<>();
        mAdapter = new PublicacionListAdapter(rootView.getContext(),list);

        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecycler.setLayoutManager(mLayoutManager);
        mHandler = new Handler(Looper.getMainLooper());

        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mRecycler.setupMoreListener(this, 1);

        return rootView;
    }

    @Override
    public void onRefresh() {
        //Toast.makeText(getActivity(), "Recargar", Toast.LENGTH_LONG).show();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                cargar_publicaciones();
                //mAdapter.add("New stuff");
            }
        }, 2000);
    }

    @Override
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
        //Toast.makeText(getActivity(), "Cargando más publicaciones", Toast.LENGTH_LONG).show();

        mHandler.postDelayed(new Runnable() {
            public void run() {
                cargar_nuevas_publicaciones();
                //mAdapter.add("More asked, more served");
            }
        }, 300);
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargar_publicaciones();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //region PUBLICACIONES

    public void cargar_publicaciones(){
        switch(tipo){
            case "resumen": resumen_publicaciones();break;
            case "explorar": explorar_publicaciones();break;
            case "hashtag": hashtag_publicaciones(); break;
            case "user": usuario_publicaciones();break;
            case "serie": serie_publicaciones(); break;
        }
    }

    public void cargar_nuevas_publicaciones(){
        switch(tipo){
            case "resumen": resumen_nuevas_publicaciones();break;
            case "explorar": explorar_nuevas_publicaciones();break;
            case "hashtag": hashtag_nuevas_publicaciones(); break;
            case "user": usuario_nuevas_publicaciones();break;
            case "serie": serie_nuevas_publicaciones();break;
        }
    }

    //region RESUMEN

    public void resumen_publicaciones(){

        mAdapter.clearPublicaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error!=null && error.toString().equals("com.android.volley.AuthFailureError")){
                    home.logout();
                }else{
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    resumen_publicaciones();
                }
            }
        },ROOT_URL+"api/user/activity?page=0");

        home.requestQueue.add(home.request);
    }

    public void resumen_nuevas_publicaciones(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/activity?page="+mAdapter.get_UltimaFecha().toString());

        home.requestQueue.add(home.request);
    }

    //endregion

    //region EXPLORAR

    public void explorar_publicaciones(){

        mAdapter.clearPublicaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                cargar_publicaciones();
            }
        },ROOT_URL+"api/user/browse?page=0");

        home.requestQueue.add(home.request);
    }

    public void explorar_nuevas_publicaciones(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/browse?page="+mAdapter.get_UltimaFecha().toString());

        home.requestQueue.add(home.request);
    }

    //endregion

    //region HASHTAG

    public void hashtag_publicaciones(){

        mAdapter.clearPublicaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                cargar_publicaciones();
            }
        },ROOT_URL+"api/user/hashtag/"+valor+"?page=0");

        home.requestQueue.add(home.request);
    }

    public void hashtag_nuevas_publicaciones(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/hashtag/"+valor+"?page="+mAdapter.get_UltimaFecha().toString());

        home.requestQueue.add(home.request);
    }

    //endregion

    //region USUARIO

    public void usuario_publicaciones(){

        mAdapter.clearPublicaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                usuario_publicaciones();
            }
        },ROOT_URL+"api/user/"+valor+"/activity?type=0&page=0");

        home.requestQueue.add(home.request);
    }

    public void usuario_nuevas_publicaciones(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/"+valor+"/activity?type=0&page="+mAdapter.get_UltimaFecha().toString());

        home.requestQueue.add(home.request);
    }

    //endregion

    //region SERIE

    public void serie_publicaciones(){

        mAdapter.clearPublicaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error!=null && error.toString().equals("com.android.volley.AuthFailureError")){
                    home.logout();
                }else{
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    serie_publicaciones();
                }
            }
        },ROOT_URL+"api/serie/"+valor +"/activity?page=0");

        home.requestQueue.add(home.request);
    }

    public void serie_nuevas_publicaciones(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/serie/"+valor +"/activity?page="+mAdapter.get_UltimaFecha().toString());

        home.requestQueue.add(home.request);
    }

    //endregion

    //endregion

    /**
     * Slide menu item click listener
     * */
    private class ListClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // display view for selected nav drawer item
            //Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(oListadoPublicaciones.getUrl(position)));
            //startActivity(browserIntent1);
        }
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
