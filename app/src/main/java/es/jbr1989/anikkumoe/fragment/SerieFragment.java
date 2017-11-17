package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
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
import es.jbr1989.anikkumoe.object.clsSerie;

/**
 * Created by jbr1989 on 02/04/2016.
 */
public class SerieFragment extends Fragment  {

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String API_OLD_URL= AppController.getInstance().getApiOld();

    private homeActivity home;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Bind(R.id.appbar) AppBarLayout mAppbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.banner) NetworkImageView mBanner;
    @Bind(R.id.avatar) SimpleDraweeView mAvatar;
    @Bind(R.id.txtDescr) TextView mDescr;

    public String id_serie;

    //region CONSTRUCTOR

    public SerieFragment(){}

    public static SerieFragment newInstance(Bundle arguments){
        SerieFragment f = new SerieFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_serie, container, false);
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

        if (getArguments()!=null) id_serie= getArguments().getString("id");
        cargar_serie();

        return rootView;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void cargar_serie(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "interes");
        params.put("acc", "serie");
        params.put("id", id_serie);

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    clsSerie oSerie= new clsSerie(response.getJSONObject("data"));

                    mTitle.setText(oSerie.getTitulo());
                    mAvatar.setImageURI(Uri.parse(oSerie.getPortada()));
                    mDescr.setText(oSerie.getDescr());

                    RecyclerViewFragment aux = RecyclerViewFragment.newInstance(home, "serie", id_serie.toString());
                    home.getFragmentManager().beginTransaction().add(R.id.lytSerieListadoPublicaciones, aux).commit();
                }
                catch (JSONException ex){ex.printStackTrace();}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home.setRefreshActionButtonState(false);
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, API_OLD_URL);

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
