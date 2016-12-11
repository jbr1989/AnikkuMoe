package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.BuzonListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsBuzon;
import es.jbr1989.anikkumoe.other.RecyclerItemClickListener;

/**
 * Created by jbr1989 on 26/05/2016.
 */
public class BuzonFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private SuperRecyclerView mRecycler;
    private BuzonListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Handler                    mHandler;

    private String tipo;
    private String valor;

    private homeActivity home;
    @Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.title) TextView mTitle;

    //region CONSTRUCTOR

    public BuzonFragment(){}

    public static BuzonFragment newInstance(Bundle arguments){
        BuzonFragment f = new BuzonFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_buzon, container, false);
        ButterKnife.bind(this, rootView);
        home = (homeActivity) rootView.getContext();
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.toggleDrawer();
            }
        });
        mTitle.setText(R.string.FragmentMensajes);

        ArrayList<clsBuzon> list = new ArrayList<>();
        mAdapter = new BuzonListAdapter(rootView.getContext(),list);

        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecycler.setLayoutManager(mLayoutManager);

        mRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(rootView.getContext(), mRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        itemClick(view, position);
                    }
                })
        );

        mHandler = new Handler(Looper.getMainLooper());

        mRecycler.setRefreshListener(this);
        mRecycler.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

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

        home.request2 = new CustomRequest2(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                mAdapter.setMensajes(response);
                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/messages");

        home.requestQueue.add(home.request2);
    }


    public void itemClick( View view, int position) {

        clsBuzon oMensaje = (clsBuzon) mAdapter.getItem(position);

        Bundle arguments = new Bundle();
        arguments.putString("id", oMensaje.getId().toString());
        arguments.putString("name", oMensaje.getUsuario());

        Fragment fragment = MensajesFragment.newInstance(arguments);
        getFragmentManager().beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();

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
