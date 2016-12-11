package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
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
import es.jbr1989.anikkumoe.ListAdapter.NotificacionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.PublicacionActivity;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsNotificacion;
import es.jbr1989.anikkumoe.other.RecyclerItemClickListener;

/**
 * Created by jbr1989 on 06/12/2015.
 */
public class notificacionesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private SuperRecyclerView mRecycler;
    private NotificacionListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Handler                    mHandler;

    private homeActivity home;
    @Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.title) TextView mTitle;

    //region CONSTRUCTOR

    public static final String TAG = "ExampleFragment";

    public static notificacionesFragment newInstance(Bundle arguments){
        notificacionesFragment f = new notificacionesFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }
    public notificacionesFragment(){}

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        ButterKnife.bind(this, rootView);
        home = (homeActivity) rootView.getContext();
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.toggleDrawer();
            }
        });
        mTitle.setText(R.string.FragmentNotificacion);

        ArrayList<clsNotificacion> list = new ArrayList<>();
        mAdapter = new NotificacionListAdapter(rootView.getContext(),list);

        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        //mRecycler.addOnItemTouchListener();
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
                cargar_notificaciones();
                //mAdapter.add("New stuff");
            }
        }, 2000);
    }


    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargar_notificaciones();
    }

    public void cargar_notificaciones(){

        mAdapter.clearNotificaciones();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        home.request2 = new CustomRequest2(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                mAdapter.setNotificaciones(response);
                mAdapter.notifyDataSetChanged();
                mRecycler.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/notifications");

        home.requestQueue.add(home.request2);
    }


    public void itemClick( View view, int position) {
        // display view for selected nav drawer item

        Intent i;

        switch(mAdapter.getTipo(position)){
            case "url":
                i = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdapter.getUrl(position)));
                startActivity(i);
                break;
            case "publicacion":
                i = new Intent(getActivity(), PublicacionActivity.class);
                i.putExtra("id_publicacion", mAdapter.getIdPublicacion(position));
                i.putExtra("ver_comentarios", false);
                startActivity(i);
                break;
            case "publicacion_comentario":
                i = new Intent(getActivity(), PublicacionActivity.class);
                i.putExtra("id_publicacion", mAdapter.getIdPublicacion(position));
                i.putExtra("ver_comentarios", true);
                startActivity(i);
                break;
            case "perfil":

                Bundle arguments = new Bundle();
                arguments.putString("usuario", mAdapter.getUserName(position));

                Fragment fragment =perfil3Fragment.newInstance(arguments);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                break;
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
