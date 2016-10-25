package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

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
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 25/09/2016.
 */

public class perfil2Fragment extends Fragment {

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String LOGIN_URL = ROOT_URL+"core/";

    public RequestQueue requestQueue;
    public CustomRequest request;

    clsUsuarioSession oUsuarioSession;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private homeActivity home;
    private Context context;

    SimpleViewPagerAdapter adapter;

    //@Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.banner) NetworkImageView mBanner;
    @Bind(R.id.avatar) NetworkImageView mAvatar;
    @Bind(R.id.btnPerfilAdd) FloatingActionButton mPerfilAdd;

    @Bind(R.id.tabs) TabLayout mTabs;
    @Bind(R.id.viewpager) ViewPager mViewpager;
    @Bind(R.id.appbar) AppBarLayout mAppbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    //region CONSTRUCTOR

    public static final String TAG = "ExampleFragment";
    private perfil2Fragment.FragmentIterationListener mCallback = null;
    public interface FragmentIterationListener{
        public void onFragmentIteration(Bundle parameters);
    }

    public static perfil2Fragment newInstance(Bundle arguments){
        perfil2Fragment f = new perfil2Fragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }

    public perfil2Fragment(){}

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);
        ButterKnife.bind(this, rootView);
        context =rootView.getContext();
        home = (homeActivity) rootView.getContext();
        /*
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.toggleDrawer();
            }
        });
*/

        requestQueue = Volley.newRequestQueue(rootView.getContext());

        oUsuarioSession = new clsUsuarioSession(rootView.getContext());

        //home.setSupportActionBar(mToolbar);
        //home.getSupportActionBar().setTitle("");
        //home.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.onBackPressed();
            }
        });

        //SystemBarHelper.immersiveStatusBar(home, 0);
        //SystemBarHelper.setHeightAndPadding(home, mToolbar);

        adapter = new SimpleViewPagerAdapter(home.getSupportFragmentManager());

        mAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                boolean showTitle = mCollapsingToolbar.getHeight() + verticalOffset <= mToolbar.getHeight();
                boolean showTitle = mCollapsingToolbar.getHeight() + verticalOffset <= mToolbar.getHeight() * 2;
                //mNickname.setVisibility(showTitle ? View.VISIBLE : View.GONE);
            }
        });

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
            mBanner.setImageUrl(ROOT_URL+"static-img/"+oUsuario.getBanner(),imageLoader);
            mAvatar.setImageUrl(ROOT_URL+"static-img/"+oUsuario.getAvatar(),imageLoader);

            String id = oUsuarioSession.getId();

            mPerfilAdd.setVisibility((!oUsuarioSession.getId().equalsIgnoreCase(oUsuario.getId().toString()) ? View.VISIBLE: View.GONE));
            mPerfilAdd.setImageResource((oUsuario.getIs_following() ? R.drawable.ic_person_del_white_24dp : R.drawable.ic_person_add_white_24dp));
            mPerfilAdd.setBackgroundColor((oUsuario.getIs_following() ? getResources().getColor(R.color.perfil_del) : getResources().getColor(R.color.perfil_add)));

            mPerfilAdd.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    seguir(oUsuario.getUsuario());
                }
            });


            adapter.addFragment(RecyclerViewFragment.newInstance("Publicaciones",home.webClient,oUsuario.getUsuario()), "Publicaciones ("+oUsuario.getPublicacionesN().toString()+")");
            adapter.addFragment(GridViewFragment.newInstance("Nakamas",oUsuario.getUsuario()), "Nakamas ("+oUsuario.getNakamasN().toString()+")");
            adapter.addFragment(GridViewFragment.newInstance("Siguiendo",oUsuario.getUsuario()), "Siguiendo ("+oUsuario.getSiguiendoN().toString()+")");
            adapter.addFragment(GridViewFragment.newInstance("Seguidores",oUsuario.getUsuario()), "Seguidores ("+oUsuario.getMesiguenN().toString()+")");
            mViewpager.setAdapter(adapter);
            mTabs.setupWithViewPager(mViewpager,true);


        }catch (Exception ex){ex.printStackTrace();}
    }

    public void cargar_usuario(String usuario){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        request = new CustomRequest(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
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

        requestQueue.add(request);
    }

    public void seguir(final String usuario){

        mPerfilAdd.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equalsIgnoreCase("followed")){
                        mPerfilAdd.setImageResource(R.drawable.ic_person_del_white_24dp);
                        mPerfilAdd.setBackgroundColor(getResources().getColor(R.color.perfil_del));
                    }else if (response.getString("status").equalsIgnoreCase("unfollowed")) {
                        mPerfilAdd.setImageResource(R.drawable.ic_person_add_white_24dp);
                        mPerfilAdd.setBackgroundColor(getResources().getColor(R.color.perfil_add));
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

        requestQueue.add(request);
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
