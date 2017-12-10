package es.jbr1989.anikkumoe.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.malinskiy.superrecyclerview.swipe.SwipeDismissRecyclerViewTouchListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.GridViewAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.http.MyWebClient;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 29/09/2016.
 */

public class GridViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private static final String ARG_TAB_NAME = "tab_name";
    @Bind(R.id.grid) GridView mGridView;

    private Context mContext;
    private String mTabName;
    private ArrayList<String> mTabNames = new ArrayList<>();

    private clsUsuarioSession oUsuarioSession;

    private GridViewAdapter mAdapter;

    public RequestQueue requestQueue;
    public CustomRequest2 request2;
    public MyWebClient webClient;
    public String user;

    public GridViewFragment() {
        // Required empty public constructor
    }

    public static GridViewFragment newInstance(String tabName, MyWebClient webClient, String user) {
        GridViewFragment fragment = new GridViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAB_NAME, tabName);
        fragment.setArguments(args);
        fragment.webClient=webClient;
        fragment.user = user;
        return fragment;
    }

    public static GridViewFragment newInstance(String tabName, String user) {
        GridViewFragment fragment = new GridViewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAB_NAME, tabName);
        fragment.setArguments(args);
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabName = getArguments().getString(ARG_TAB_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gridview, container, false);
        ButterKnife.bind(this, rootView);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        mAdapter = new GridViewAdapter(rootView.getContext());
        mGridView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cargar_usuarios();
    }

    @Override
    public void onRefresh() {
        //Toast.makeText(getActivity(), "Recargar", Toast.LENGTH_LONG).show();

        //mHandler.postDelayed(new Runnable() {
            //public void run() {
                cargar_usuarios();
                //mAdapter.add("New stuff");
            //}
        //}, 2000);
    }


    @Override
    public boolean canDismiss(int position) {
        return true;
    }

    @Override
    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            //mSparseAnimator.setSkipNext(true);
            mAdapter.remove(position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    //region USUARIO

    public void cargar_usuarios(){
        switch(mTabName){
            case "Nakamas": cargar_nakamas();break;
            case "Siguiendo": cargar_seguiendo();break;
            case "Seguidores": cargar_seguidores();break;
        }
    }



    /* Listado de todos los mensajes */
    public void cargar_nakamas(){

        mAdapter.clearUsers();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        request2 = new CustomRequest2(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                mAdapter.setUsers(response);
                mAdapter.putConfigNewsCount();

                // setting the nav drawer list adapter
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/"+user+"/nakamas");

        requestQueue.add(request2);

    }

    public void cargar_seguiendo(){

        mAdapter.clearUsers();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        request2 = new CustomRequest2(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                mAdapter.setUsers(response);
                mAdapter.putConfigNewsCount();

                // setting the nav drawer list adapter
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/"+user+"/following");

        requestQueue.add(request2);

    }

    public void cargar_seguidores(){

        mAdapter.clearUsers();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        request2 = new CustomRequest2(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                mAdapter.setUsers(response);
                mAdapter.putConfigNewsCount();

                // setting the nav drawer list adapter
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/"+user+"/followers");

        requestQueue.add(request2);

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

}