package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;

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
import es.jbr1989.anikkumoe.object.clsUsuario;

/**
 * Created by jbr1989 on 25/09/2016.
 */

public class perfil3Fragment extends Fragment {

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String IMG_URL = AppController.getInstance().getImg();
    private static final String LOGIN_URL = ROOT_URL+"core/";

    private homeActivity home;

    //@Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.banner) SimpleDraweeView mBanner;
    @Bind(R.id.avatar) SimpleDraweeView mAvatar;
    @Bind(R.id.btnPerfilAdd) FloatingActionButton mPerfilAdd;

    @Bind(R.id.appbar) AppBarLayout mAppbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Bind(R.id.lblNumNakamas) TextView lblNumNakamas;
    @Bind(R.id.lblNumSiguiendo) TextView lblNumSiguiendo;
    @Bind(R.id.lblNumSeguidores) TextView lblNumSeguidores;
    @Bind(R.id.lblNumPublicaciones) TextView lblNumPublicaciones;


    //region CONSTRUCTOR

    public static final String TAG = "ExampleFragment";
    private perfil3Fragment.FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }

    public static perfil3Fragment newInstance(Bundle arguments){
        perfil3Fragment f = new perfil3Fragment();
        if(arguments != null) f.setArguments(arguments);

        SimpleViewPagerAdapter adapter = null;
        ViewPager mViewpager = null;

        return f;
    }

    public perfil3Fragment(){}

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_perfil3, container, false);
        ButterKnife.bind(this, rootView);
        home = (homeActivity) rootView.getContext();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.onBackPressed();
            }
        });

        mAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                boolean showTitle = mCollapsingToolbar.getHeight() + verticalOffset <= mToolbar.getHeight();
                boolean showTitle = mCollapsingToolbar.getHeight() + verticalOffset <= mToolbar.getHeight() * 2;
                //mNickname.setVisibility(showTitle ? View.VISIBLE : View.GONE);
            }
        });

        String usuario=getArguments().getString("usuario");

        if (home.oUsuarioSession.getUsuario()==usuario) cargar_datos_usuario(home.oUsuarioSession.getUsuarioSession());
        else cargar_usuario(usuario);

        return rootView;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void cargar_datos_usuario(final clsUsuario oUsuario){

        try {
            mTitle.setText(oUsuario.getNombre());
            mBanner.setImageURI(Uri.parse(ROOT_URL+"static-img/"+oUsuario.getBanner()));
            mAvatar.setImageURI(Uri.parse(IMG_URL+oUsuario.getAvatar()));

            String id = home.oUsuarioSession.getId();

            mPerfilAdd.setVisibility((!home.oUsuarioSession.getId().equalsIgnoreCase(oUsuario.getId().toString()) ? View.VISIBLE: View.GONE));
            mPerfilAdd.setImageResource((oUsuario.getIs_following() ? R.drawable.ic_person_del_white_24dp : R.drawable.ic_person_add_white_24dp));
            mPerfilAdd.setBackgroundTintList(ColorStateList.valueOf((oUsuario.getIs_following() ? getResources().getColor(R.color.perfil_del) : getResources().getColor(R.color.perfil_add))));

            mPerfilAdd.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    seguir(oUsuario.getUsuario());
                }
            });

            lblNumNakamas.setText(oUsuario.getNakamasN().toString());
            lblNumSiguiendo.setText(oUsuario.getSiguiendoN().toString());
            lblNumSeguidores.setText(oUsuario.getMesiguenN().toString());
            lblNumPublicaciones.setText(oUsuario.getPublicacionesN().toString());

            RecyclerViewFragment aux = RecyclerViewFragment.newInstance(home, "user", oUsuario.getUsuario());
            home.getFragmentManager().beginTransaction().add(R.id.lytPerfilListadoPublicaciones, aux).commit();

        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public void cargar_usuario(String usuario){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

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

        mPerfilAdd.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equalsIgnoreCase("followed")){
                        mPerfilAdd.setImageResource(R.drawable.ic_person_del_white_24dp);
                        mPerfilAdd.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.perfil_del)));
                    }else if (response.getString("status").equalsIgnoreCase("unfollowed")) {
                        mPerfilAdd.setImageResource(R.drawable.ic_person_add_white_24dp);
                        mPerfilAdd.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.perfil_add)));
                    }else{
                        Toast.makeText(getActivity(), response.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){ex.printStackTrace();}
                mPerfilAdd.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                mPerfilAdd.setEnabled(true);
            }
        }, ROOT_URL+ "api/user/" + usuario + "/follow");

        home.requestQueue.add(home.request);
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
