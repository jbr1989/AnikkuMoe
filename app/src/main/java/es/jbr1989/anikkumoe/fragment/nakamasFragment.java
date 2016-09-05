package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.ListAdapter.NakamaListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsNakama;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class nakamasFragment extends Fragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "nakamasFragment";
    ProgressDialog pDialog;

    private clsUsuarioSession oUsuarioSession;
    private NakamaListAdapter oListadoNakamas;

    private ListView lstNakamas;

    public RequestQueue requestQueue;
    public CustomRequest request;

    Handler timerHandler = new Handler();

    public static final String SP_NAME = "Nakamas";

    Long intervalo;

    private FragmentIterationListener mCallback = null;

    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }
    public static nakamasFragment newInstance(Bundle arguments){
        nakamasFragment f = new nakamasFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }
    public nakamasFragment(){}

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nakamas, container, false);

        getActivity().setTitle(R.string.FragmentNakamas);
        cargar_preferencias(rootView);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());
        requestQueue = Volley.newRequestQueue(rootView.getContext());

        oListadoNakamas= new NakamaListAdapter(rootView.getContext());

        lstNakamas = (ListView) rootView.findViewById(R.id.lstNakama);
        lstNakamas.setOnItemClickListener(this);
        lstNakamas.setAdapter(oListadoNakamas);

        //cargando_start(rootView.getContext(), "Cargando...");

        if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);

        return rootView;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargar_nakamas();
    }

    private Runnable onEverySecond=new Runnable() {
        public void run() {
            // do real work here
            Toast.makeText(getActivity(), "Buscando nakamas conectados...", Toast.LENGTH_SHORT).show();

            if (oListadoNakamas.getCount()==0) cargar_nakamas();
            else cargar_nakamas_online();

            timerHandler.postDelayed(onEverySecond, intervalo*1000);
        }
    };

    private void cargar_preferencias(View view){

        SharedPreferences ChatsConfig = PreferenceManager.getDefaultSharedPreferences(view.getContext());

        intervalo=Long.parseLong(ChatsConfig.getString("chat_intervalo", "10"));

    }

    // region CHATS

    public void cargar_nakamas(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "usuarios");
        params.put("acc", "nakamas");
        params.put("usuario",oUsuarioSession.getUsuario());
        params.put("fecha", "undefined");
        params.put("uid","AWTlu");

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //oListadoNakamas.clearNakamas();
                if (oListadoNakamas.setNakamas(response)) {
                    oListadoNakamas.notifyDataSetChanged();
                    cargar_nakamas_online();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }

    public void cargar_nakamas_online(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "usuarios");
        params.put("acc", "nakamason");
        params.put("pag", "0");

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                oListadoNakamas.clearNakamasOn();
                if (oListadoNakamas.setNakamasOn(response)) oListadoNakamas.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }

    // end region


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        clsNakama oNakama = (clsNakama) oListadoNakamas.getItem(position);

        Bundle arguments = new Bundle();
        arguments.putString("id", oNakama.getId().toString());
        arguments.putString("name", oNakama.getNombre());

        Fragment fragment = chatPrivadoFragment.newInstance(arguments);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
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
        //cargando_end();
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        //mCallback = null;
        super.onDetach();
        timerHandler.removeCallbacks(onEverySecond);
    }

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
