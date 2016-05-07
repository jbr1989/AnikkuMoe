package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
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

    private NetworkImageView imgAvatar;
    private TextView lblUsuario, lblDescr, lblArroba, lblPais, lblWeb, lblNumNakamas, lblNumSiguiendo, lblNumSeguidores, lblNumPublicaciones;

    JSONParser jsonParser = new JSONParser();

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

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.perfil, container, false);

        imgAvatar = (NetworkImageView) rootView.findViewById(R.id.imgAvatar);
        lblUsuario = (TextView) rootView.findViewById(R.id.lblUsuario);
        lblDescr = (TextView) rootView.findViewById(R.id.lblDescr);
        lblArroba = (TextView) rootView.findViewById(R.id.lblArroba);
        lblPais = (TextView) rootView.findViewById(R.id.lblPais);
        lblWeb = (TextView) rootView.findViewById(R.id.lblWeb);
        lblNumNakamas = (TextView) rootView.findViewById(R.id.lblNumNakamas);
        lblNumSiguiendo = (TextView) rootView.findViewById(R.id.lblNumSiguiendo);
        lblNumSeguidores = (TextView) rootView.findViewById(R.id.lblNumSeguidores);
        lblNumPublicaciones = (TextView) rootView.findViewById(R.id.lblNumPublicaciones);

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());

        return rootView;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clsUsuario oUsuario = oUsuarioSession.getUsuarioSession();

        try {
            lblUsuario.setText(oUsuario.getNombre());
            lblDescr.setText(oUsuario.getDescripcion());
            lblArroba.setText("@" + oUsuario.getUsuario());
            lblPais.setText(oUsuario.getPais());
            lblWeb.setText(oUsuario.getWeb());
            lblNumNakamas.setText(oUsuario.getNakamasN().toString());
            lblNumSiguiendo.setText(oUsuario.getSiguiendoN().toString());
            lblNumSeguidores.setText(oUsuario.getMesiguenN().toString());
            lblNumPublicaciones.setText(oUsuario.getPublicacionesN().toString());

            imgAvatar.setImageUrl(ROOT_URL+"/static-img/" + oUsuario.getAvatar(), imageLoader);

            Log.d("USUARIO", this.toString());

        }catch (Exception ex){ex.printStackTrace();}

    }

    @Override
    public void onClick (View v){
        switch(v.getId()){
            case R.id.imgAvatar: break;
            case R.id.lblUsuario:
                break;
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
