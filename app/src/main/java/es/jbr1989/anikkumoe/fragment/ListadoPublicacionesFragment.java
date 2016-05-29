package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.malinskiy.superrecyclerview.swipe.SparseItemRemoveAnimator;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.PublicacionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.ReactionActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsPublicacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 02/04/2016.
 */
public class ListadoPublicacionesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    public static final String TAG = "ListadopublicacionesFragment";

    private clsUsuarioSession oUsuarioSession;

    private SuperRecyclerView          mRecycler;
    private PublicacionListAdapter          mAdapter;
    private SparseItemRemoveAnimator mSparseAnimator;
    private RecyclerView.LayoutManager mLayoutManager;
    private Handler                    mHandler;


    public RequestQueue requestQueue;
    public CustomRequest request;


    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.listado_publicaciones, container, false);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        ArrayList<clsPublicacion> list = new ArrayList<>();
        mAdapter = new PublicacionListAdapter(rootView.getContext(),list);

        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecycler.setLayoutManager(mLayoutManager);
        // mRecycler.addItemDecoration(new PaddingItemDecoration());


        mRecycler.setupSwipeToDismiss(this);
        mSparseAnimator = new SparseItemRemoveAnimator();
        mRecycler.getRecyclerView().setItemAnimator(mSparseAnimator);


        mHandler = new Handler(Looper.getMainLooper());
/*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecycler.setAdapter(mAdapter);
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargar_publicaciones();
                        //mAdapter.addAll(new String[]{"More stuff", "More stuff", "More stuff"});
                    }
                });
            }
        });
        thread.start();

*/
        mRecycler.setAdapter(mAdapter);
        //cargar_publicaciones();

        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mRecycler.setupMoreListener(this, 1);

        return rootView;
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            String countryCode = data.getStringExtra(ReactionActivity.RESULT_CONTRYCODE);
            Toast.makeText(getActivity(), "You selected countrycode: " + countryCode, Toast.LENGTH_LONG).show();
        }
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

    @Override
    public boolean canDismiss(int position) {
        return true;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mSparseAnimator.setSkipNext(true);
            mAdapter.remove(position);
        }
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargar_publicaciones();
    }

    public void cargar_publicaciones(){

        mAdapter.clearPublicaciones();

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
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                cargar_publicaciones();
            }
        },ROOT_URL+"api/user/activity?page=0");

        requestQueue.add(request);
    }

    public void cargar_nuevas_publicaciones(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "publicaciones");
        //params.put("acc", "resumen");
        //params.put("publicacion", mAdapter.get_UltimaFecha().toString());
        //params.put("uid", "0");

        request = new CustomRequest(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
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

        requestQueue.add(request);
    }

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
