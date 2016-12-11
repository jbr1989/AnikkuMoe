package es.jbr1989.anikkumoe.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.PublicacionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.MyWebClient;
import es.jbr1989.anikkumoe.object.clsPublicacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;


public class RecyclerView2Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnMoreListener, SwipeDismissRecyclerViewTouchListener.DismissCallbacks {
    private static final String ARG_TAB_NAME = "tab_name";
    @Bind(R.id.list) SuperRecyclerView mRecyclerView;
    private Context mContext;
    private String mTabName;
    private ArrayList<String> mTabNames = new ArrayList<>();

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private clsUsuarioSession oUsuarioSession;

    private PublicacionListAdapter          mAdapter;
    private SparseItemRemoveAnimator mSparseAnimator;
    private RecyclerView.LayoutManager mLayoutManager;
    private Handler                    mHandler;

    public RequestQueue requestQueue;
    public CustomRequest request;
    public MyWebClient webClient;
    public String user;

    public RecyclerView2Fragment() {
        // Required empty public constructor
    }

    public static RecyclerView2Fragment newInstance(String tabName, MyWebClient webClient, String user) {
        RecyclerView2Fragment fragment = new RecyclerView2Fragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAB_NAME, tabName);
        fragment.setArguments(args);
        //fragment.setRetainInstance(true);
        fragment.webClient=webClient;
        fragment.user=user;
        return fragment;
    }

    public static RecyclerView2Fragment newInstance(String tabName) {
        RecyclerView2Fragment fragment = new RecyclerView2Fragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAB_NAME, tabName);
        fragment.setArguments(args);
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
        mContext = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_simple, container, false);
        ButterKnife.bind(this, rootView);


        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        ArrayList<clsPublicacion> list = new ArrayList<>();
        mAdapter = new PublicacionListAdapter(rootView.getContext(),list);

        mRecyclerView = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // mRecycler.addItemDecoration(new PaddingItemDecoration());

        mRecyclerView.setupSwipeToDismiss(this);
        mSparseAnimator = new SparseItemRemoveAnimator();
        mRecyclerView.getRecyclerView().setItemAnimator(mSparseAnimator);

        mHandler = new Handler(Looper.getMainLooper());


        mRecyclerView.setRefreshListener(this);
        mRecyclerView.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mRecyclerView.setupMoreListener(this, 1);


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cargar_publicaciones();


/*
        for (int i = 0; i < 20; i++) {
            mTabNames.add(mTabName);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        SimpleAdapter adapter = new SimpleAdapter(mTabNames, R.layout.item_home_fragment);

        mRecyclerView.setAdapter(adapter);
        adapter.setHandleClickListener(new BaseAdapter.HandleClickListener() {
            @Override
            public void handleClick(BaseAdapter.ViewHolder holder) {
                final TextView mItem = ButterKnife.findById(holder.getItemView(), R.id.item);
                mItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), mItem.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        */
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //region PUBLICACIONES

    public void cargar_publicaciones(){
        switch(mTabName){
            case "Publicaciones": usuario_publicaciones();break;
        }
    }

    public void cargar_nuevas_publicaciones(){
        switch(mTabName){
            case "Publicaciones": usuario_nuevas_publicaciones();break;
        }
    }

    //region USUARIO

    public void usuario_publicaciones(){

        mAdapter.clearPublicaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        request = new CustomRequest(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setPublicaciones(response);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                cargar_publicaciones();
            }
        },ROOT_URL+"api/user/"+user+"/activity?type=0&page=0");

        requestQueue.add(request);
    }

    public void usuario_nuevas_publicaciones(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

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
        },ROOT_URL+"api/user/"+user+"/activity?type=0&page="+mAdapter.get_UltimaFecha().toString());

        requestQueue.add(request);
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
