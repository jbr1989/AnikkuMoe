package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.JSONParser;
import es.jbr1989.anikkumoe.object.clsUsuario;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 04/12/2015.
 */
public class perfilFragment extends Fragment implements OnClickListener{

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String LOGIN_URL = ROOT_URL+"core/";

    clsUsuarioSession oUsuarioSession;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private NetworkImageView imgAvatar, imgWeb;
    private TextView lblUsuario, lblDescr, lblArroba, lblPais, lblWeb, lblNumNakamas, lblNumSiguiendo, lblNumSeguidores, lblNumPublicaciones, lblPerfilSeguidor;
    public ImageButton btnPerfilAdd;

    JSONParser jsonParser = new JSONParser();

    private homeActivity home;
    @Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.avatar) NetworkImageView mAvatar;
    @Bind(R.id.title) TextView mTitle;

    //region CONSTRUCTOR

    public static final String TAG = "ExampleFragment";
    private FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }

    public static perfilFragment newInstance(Bundle arguments){
        perfilFragment f = new perfilFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }

    public perfilFragment(){}

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.perfil, container, false);
        ButterKnife.bind(this, rootView);
        home = (homeActivity) rootView.getContext();
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.toggleDrawer();
            }
        });

        imgAvatar = (NetworkImageView) rootView.findViewById(R.id.imgAvatar);
        imgWeb = (NetworkImageView) rootView.findViewById(R.id.imgWeb);

        lblUsuario = (TextView) rootView.findViewById(R.id.lblUsuario);
        lblDescr = (TextView) rootView.findViewById(R.id.lblDescr);
        lblArroba = (TextView) rootView.findViewById(R.id.lblArroba);
        lblPais = (TextView) rootView.findViewById(R.id.lblPais);
        lblWeb = (TextView) rootView.findViewById(R.id.lblWeb);
        lblNumNakamas = (TextView) rootView.findViewById(R.id.lblNumNakamas);
        lblNumSiguiendo = (TextView) rootView.findViewById(R.id.lblNumSiguiendo);
        lblNumSeguidores = (TextView) rootView.findViewById(R.id.lblNumSeguidores);
        lblNumPublicaciones = (TextView) rootView.findViewById(R.id.lblNumPublicaciones);

        lblPerfilSeguidor = (TextView) rootView.findViewById(R.id.lblPerfilSeguidor);
        btnPerfilAdd = (ImageButton) rootView.findViewById(R.id.btnPerfilAdd);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());

        return rootView;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String usuario=getArguments().getString("usuario");

        if (oUsuarioSession.getUsuario()==usuario) cargar_datos_usuario(oUsuarioSession.getUsuarioSession());
        else cargar_usuario(usuario);
    }

    public void cargar_datos_usuario(final clsUsuario oUsuario){

        try {
            mTitle.setText(oUsuario.getNombre());
            mAvatar.setImageUrl(ROOT_URL+"/static-img/" + oUsuario.getAvatar(), imageLoader);

            lblUsuario.setText(oUsuario.getNombre());
            lblDescr.setText(oUsuario.getDescripcion());
            lblArroba.setText("@" + oUsuario.getUsuario());
            lblPais.setVisibility((!oUsuario.getPais().equalsIgnoreCase("null") ? View.VISIBLE : View.GONE));
            lblPais.setText(oUsuario.getPais());
            //lblWeb.setText(oUsuario.getWeb());
            lblNumNakamas.setText(oUsuario.getNakamasN().toString());
            lblNumSiguiendo.setText(oUsuario.getSiguiendoN().toString());
            lblNumSeguidores.setText(oUsuario.getMesiguenN().toString());
            lblNumPublicaciones.setText(oUsuario.getPublicacionesN().toString());

            imgAvatar.setImageUrl(ROOT_URL+"/static-img/" + oUsuario.getAvatar(), imageLoader);
            imgWeb.setImageUrl("http://www.google.com/s2/favicons?domain="+oUsuario.getWeb(), imageLoader);

            lblPerfilSeguidor.setVisibility((oUsuario.getId().toString()!=oUsuarioSession.getId() && oUsuario.getIs_following_me() ? View.VISIBLE: View.GONE));
            btnPerfilAdd.setVisibility((!oUsuarioSession.getId().equalsIgnoreCase(oUsuario.getId().toString()) ? View.VISIBLE: View.GONE));
            btnPerfilAdd.setImageResource((oUsuario.getIs_following() ? R.drawable.ic_person_del_white_24dp : R.drawable.ic_person_add_white_24dp));
            btnPerfilAdd.setBackgroundColor((oUsuario.getIs_following() ? getResources().getColor(R.color.perfil_del) : getResources().getColor(R.color.perfil_add)));

            btnPerfilAdd.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    seguir(oUsuario.getUsuario());
                }
            });

            imgWeb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    abrir_web(oUsuario.getWeb());
                }
            });

            lblWeb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    abrir_web(oUsuario.getWeb());
                }
            });

        }catch (Exception ex){ex.printStackTrace();}
    }

    public void cargar_usuario(String usuario){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                clsUsuario oUsuario= new clsUsuario();
                oUsuario.setUsuario(response);

                cargar_datos_usuario(oUsuario);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, ROOT_URL+"api/user/"+usuario+"/page");

        home.requestQueue.add(home.request);
    }

    public void seguir(final String usuario){

        btnPerfilAdd.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equalsIgnoreCase("followed")){
                        btnPerfilAdd.setImageResource(R.drawable.ic_person_del_white_24dp);
                        btnPerfilAdd.setBackgroundColor(getResources().getColor(R.color.perfil_del));
                    }else if (response.getString("status").equalsIgnoreCase("unfollowed")) {
                        btnPerfilAdd.setImageResource(R.drawable.ic_person_add_white_24dp);
                        btnPerfilAdd.setBackgroundColor(getResources().getColor(R.color.perfil_add));
                    }else{
                        Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){ex.printStackTrace();}
                btnPerfilAdd.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                btnPerfilAdd.setEnabled(true);
            }
        }, ROOT_URL+ "api/user/" + usuario + "/follow");

        home.requestQueue.add(home.request);
    }

    public void abrir_web(String url){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onClick (View v){
        switch(v.getId()){
            case R.id.imgAvatar: break;
            case R.id.lblUsuario:break;
            case R.id.lblDescr:break;
            case R.id.lblArroba: break;
            case R.id.lblPais:break;
            case R.id.lblWeb:
                break;
            case R.id.lblNumNakamas:break;
            case R.id.lblNumSiguiendo: break;
            case R.id.lblNumSeguidores:break;
            case R.id.lblNumPublicaciones:break;
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
