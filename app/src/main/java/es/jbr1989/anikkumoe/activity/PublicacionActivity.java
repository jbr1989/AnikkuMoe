package es.jbr1989.anikkumoe.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.ComentariosListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.fragment.perfilFragment;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.http.MyWebClient;
import es.jbr1989.anikkumoe.object.clsPublicacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.ExpandedListView;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 21/06/2016.
 */
public class PublicacionActivity extends Activity {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private clsPublicacion oPublicacion;
    private ComentariosListAdapter oListadoComentarios;

    private Integer id_publicacion;
    private Boolean comentarios;

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;
    public CustomRequest2 request2;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private clsDate oDate = new clsDate();

    public LinearLayout lytPublicacion, lytBody, lytSpoiler, lytAnime, lytComentar,lytComentarios, lytLikes, lytReplicas, lytReactionLike, lytReactionLove, lytReactionHaha, lytReactionWow, lytReactionSorry, lytReactionAnger, lytReactionReplicas;
    public NetworkImageView imgAvatar,imgAvatarOri,imgAnime;
    public ImageView imgLike;
    public WebView webBody;
    public TextView txtNombre, txtFecha, txtUsuario, txtNombreOri, txtBody, txtAnime, txtComentarios, txtReactionLike, txtReactionLove, txtReactionHaha, txtReactionWow, txtReactionSorry, txtReactionAnger, txtReactionReplicas, txtLike, txtMessage;
    public ExpandedListView lstComentarios;
    public ImageButton btnComentario;

    public FragmentManager fragmentManager;
    public MyWebClient webClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicacion_info);
        setTitle("Publicación");

        oUsuarioSession = new clsUsuarioSession(this);
        requestQueue = Volley.newRequestQueue(this);

        fragmentManager = getFragmentManager();
        webClient = new MyWebClient(fragmentManager);

        Intent i= getIntent();
        Bundle extras = i.getExtras();
        if(extras != null) {
            id_publicacion = extras.getInt("id_publicacion");
            comentarios=extras.getBoolean("ver_comentarios");
            //usuario = extras.getString("usuario");

            cargar_publicacion();
        }
    }

    public void cargar_objetos(){

        lytPublicacion = (LinearLayout) findViewById(R.id.lytPublicacion);
        lytBody = (LinearLayout) findViewById(R.id.lytBody);
        lytSpoiler = (LinearLayout) findViewById(R.id.lytSpoiler);
        lytAnime = (LinearLayout) findViewById(R.id.lytAnime);
        lytComentar = (LinearLayout)  findViewById(R.id.lytComentar);
        lytComentarios = (LinearLayout)  findViewById(R.id.lytComentarios);
        lytReplicas = (LinearLayout)  findViewById(R.id.lytReplicas);
        lytLikes = (LinearLayout)  findViewById(R.id.lytLikes);

        imgAvatar = (NetworkImageView) findViewById(R.id.imgPubAvatar);
        imgAvatarOri = (NetworkImageView) findViewById(R.id.imgPubAvatarOri);
        webBody= (WebView) findViewById(R.id.webBody);
        txtNombre = (TextView) findViewById(R.id.txtNombre);
        txtFecha = (TextView) findViewById(R.id.txtFecha);
        txtUsuario = (TextView) findViewById(R.id.txtUsuario);
        txtNombreOri = (TextView) findViewById(R.id.txtNombreOri);
        txtBody = (TextView) findViewById(R.id.txtBody);

        imgAnime = (NetworkImageView) findViewById(R.id.imgAnime);
        txtAnime=(TextView) findViewById(R.id.txtAnime);
        txtComentarios = (TextView) findViewById(R.id.txtComentarios);

        lytReactionLike = (LinearLayout) findViewById(R.id.lytReactionLike);
        lytReactionLove = (LinearLayout) findViewById(R.id.lytReactionLove);
        lytReactionHaha = (LinearLayout) findViewById(R.id.lytReactionHaha);
        lytReactionWow = (LinearLayout) findViewById(R.id.lytReactionWow);
        lytReactionSorry = (LinearLayout) findViewById(R.id.lytReactionSorry);
        lytReactionAnger = (LinearLayout) findViewById(R.id.lytReactionAnger);
        lytReactionReplicas = (LinearLayout) findViewById(R.id.lytReactionReplicas);

        txtReactionLike = (TextView) findViewById(R.id.txtReactionLike);
        txtReactionLove = (TextView) findViewById(R.id.txtReactionLove);
        txtReactionHaha = (TextView) findViewById(R.id.txtReactionHaha);
        txtReactionWow = (TextView) findViewById(R.id.txtReactionWow);
        txtReactionSorry = (TextView) findViewById(R.id.txtReactionSorry);
        txtReactionAnger = (TextView) findViewById(R.id.txtReactionAnger);
        txtReactionReplicas = (TextView) findViewById(R.id.txtReactionReplicas);

        imgLike=(ImageView) findViewById(R.id.imgLike);
        txtLike=(TextView)  findViewById(R.id.txtLike);

        lstComentarios= (ExpandedListView) findViewById(R.id.lstComentarios);
        btnComentario = (ImageButton) findViewById(R.id.btnComentario);
        txtMessage = (EditText) findViewById(R.id.txtMensaje);

    }

    public void cargar_vista(){

        if (oPublicacion.getType().equalsIgnoreCase("REP")) {
            imgAvatarOri.setImageUrl(ROOT_URL+"static-img/" + oPublicacion.user.getAvatar(), imageLoader);
            txtNombreOri.setText(oPublicacion.user.getHTMLNombre());
        }

        imgAvatar.setImageUrl(ROOT_URL+"static-img/" + (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getAvatar() : oPublicacion.user_original.getAvatar()), imageLoader);
        txtNombre.setText((!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getHTMLNombre() : oPublicacion.user_original.getHTMLNombre()));
        txtUsuario.setText("@" + (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getHTMLUsuario() : oPublicacion.user_original.getHTMLUsuario()));

        imgAvatarOri.setVisibility((!oPublicacion.getType().equalsIgnoreCase("REP") ? View.GONE : View.VISIBLE));
        txtNombreOri.setVisibility((!oPublicacion.getType().equalsIgnoreCase("REP") ? View.GONE : View.VISIBLE));

        txtFecha.setText(oDate.DateDiff(oPublicacion.feed.getFecha(), System.currentTimeMillis()));

        mostrar_body(oPublicacion, oPublicacion.feed.getSpoiler());

        lytAnime.setVisibility(View.GONE);

        if(oPublicacion.feed.getAnime()!=null){
            imgAnime.setImageUrl("http://www.anilista.com/img/dir/anime/regular/"+oPublicacion.feed.anime.getId().toString()+".jpg", imageLoader);
            txtAnime.setText(oPublicacion.feed.anime.getTitulo());
            lytAnime.setVisibility(View.VISIBLE);
        }

        txtComentarios.setText(oPublicacion.feed.stats.getComments());

        lytReplicas.setBackgroundColor((oPublicacion.getIs_replicated() ? getResources().getColor(R.color.btn_rep) : ((Integer.parseInt(oUsuarioSession.getId())!=oPublicacion.user.getId()) ? Color.TRANSPARENT : getResources().getColor(R.color.btn_dislike))));
        //lytLikes.setBackgroundColor((oPublicacion.getMy_reaction() ? context.getResources().getColor(R.color.btn_like) : Color.TRANSPARENT));

        lytReactionLike.setVisibility((oPublicacion.feed.stats.reactions.getLike()>0 ? View.VISIBLE : View.GONE));
        lytReactionLove.setVisibility((oPublicacion.feed.stats.reactions.getLove()>0 ? View.VISIBLE : View.GONE));
        lytReactionHaha.setVisibility((oPublicacion.feed.stats.reactions.getHaha()>0 ? View.VISIBLE : View.GONE));
        lytReactionWow.setVisibility((oPublicacion.feed.stats.reactions.getWow()>0 ? View.VISIBLE : View.GONE));
        lytReactionSorry.setVisibility((oPublicacion.feed.stats.reactions.getSorry()>0 ? View.VISIBLE : View.GONE));
        lytReactionAnger.setVisibility((oPublicacion.feed.stats.reactions.getAnger()>0 ? View.VISIBLE : View.GONE));
        lytReactionReplicas.setVisibility((Integer.parseInt(oPublicacion.feed.stats.getShared())>0 ? View.VISIBLE : View.GONE));


        txtReactionLike.setText(oPublicacion.feed.stats.reactions.getLike().toString());
        txtReactionLove.setText(oPublicacion.feed.stats.reactions.getLove().toString());
        txtReactionHaha.setText(oPublicacion.feed.stats.reactions.getHaha().toString());
        txtReactionWow.setText(oPublicacion.feed.stats.reactions.getWow().toString());
        txtReactionSorry.setText(oPublicacion.feed.stats.reactions.getSorry().toString());
        txtReactionAnger.setText(oPublicacion.feed.stats.reactions.getAnger().toString());
        txtReactionReplicas.setText(oPublicacion.feed.stats.getShared());

        lytComentarios.setVisibility(View.GONE);

        txtNombre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //cargar_perfil(oPublicacion.user.getNombre());
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //cargar_perfil(oPublicacion.user.getNombre());
            }
        });

        txtNombreOri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //cargar_perfil(oPublicacion.user_original.getNombre());
            }
        });

        imgAvatarOri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //cargar_perfil(oPublicacion.user_original.getNombre());
            }
        });

        lytSpoiler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mostrar_body(oPublicacion, false);
            }
        });

        lytAnime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(ROOT_URL+"serie/" + oPublicacion.feed.anime.getId()));
                v.getContext().startActivity(browserIntent1);

            }
        });

        lytComentar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ver_comentarios();
            }
        });

        lytLikes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(v.getContext(), ReactionActivity.class);
                intent.putExtra("id_publicacion", oPublicacion.feed.getId());
                //intent.putExtra("usuario", (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getUsuario() : oPublicacion.user_original.getUsuario()));
                startActivityForResult(intent, 1);
            }
        });

        if (Integer.parseInt(oUsuarioSession.getId())!=oPublicacion.user.getId()) {
            lytReplicas.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!oPublicacion.getIs_replicated())
                        republicar(lytReplicas, oPublicacion.feed.getId());
                }
            });
        }

        btnComentario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String messageText = txtMessage.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    Toast.makeText(getBaseContext(), "Escribe un comentario", Toast.LENGTH_SHORT).show();
                    return;
                }
                comentar(oPublicacion.feed.getId(),messageText);
            }
        });


        String my_reaction =oPublicacion.getMy_reaction();

        if (my_reaction.equalsIgnoreCase("like")) {
            imgLike.setImageResource(R.drawable.reaction_like_32);
            txtLike.setText("Me gusta");
        } else if (my_reaction.equalsIgnoreCase("love")){
            imgLike.setImageResource(R.drawable.reaction_love_32);
            txtLike.setText("Sugoi");
        } else if (my_reaction.equalsIgnoreCase("haha")){
            imgLike.setImageResource(R.drawable.reaction_haha_32);
            txtLike.setText("HaHa");
        } else if (my_reaction.equalsIgnoreCase("wow")){
            imgLike.setImageResource(R.drawable.reaction_wow_32);
            txtLike.setText("Ara ara");
        } else if (my_reaction.equalsIgnoreCase("sorry")){
            imgLike.setImageResource(R.drawable.reaction_sorry_32);
            txtLike.setText("Me entristece");
        } else if (my_reaction.equalsIgnoreCase("anger")) {
            imgLike.setImageResource(R.drawable.reaction_anger_32);
            txtLike.setText("Me enfada");
        }else{
            imgLike.setImageResource(R.drawable.icon_like);
            txtLike.setText("Me gusta");
        }

    }

    public void ver_comentarios(){
        if(lytComentarios.getVisibility()!=View.VISIBLE){
            oListadoComentarios= new ComentariosListAdapter(getBaseContext(),webClient);
            lstComentarios.setAdapter(oListadoComentarios);
            cargar_comentarios();
            lytComentarios.setVisibility(View.VISIBLE);
        }else{
            lytComentarios.setVisibility(View.GONE);
        }
    }

    public void cargar_publicacion(){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "publicaciones");
        //params.put("acc", "resumen");
        //params.put("publicacion", "0");
        //params.put("uid", "0");

        request = new CustomRequest(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                oPublicacion= new clsPublicacion(response);
                cargar_objetos();
                cargar_vista();
                if(comentarios) ver_comentarios();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/activity/"+id_publicacion.toString());

        requestQueue.add(request);
    }

    public void cargar_perfil(String usuario){
        Bundle arguments = new Bundle();
        arguments.putString("usuario", usuario);

        Fragment fragment = perfilFragment.newInstance(arguments);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    public void cargar_comentarios(){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        //params.put("mdl", "notificaciones");
        //params.put("acc", "obtener");

        request2 = new CustomRequest2(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                oListadoComentarios.setComentarios(response);
                oListadoComentarios.putConfigNewsCount();

                // setting the nav drawer list adapter
                oListadoComentarios.notifyDataSetChanged();

                lstComentarios.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/activity/"+id_publicacion.toString()+"/comment?page=0");

        requestQueue.add(request2);

    }

    public void comentar(Integer id_publicacion, String messageText){

        btnComentario.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("usus", "");
        params.put("texto", messageText);

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (oListadoComentarios.setNewComentarios(response)) oListadoComentarios.notifyDataSetChanged();
                txtMessage.setText("");
                btnComentario.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
                btnComentario.setEnabled(true);
            }
        }, ROOT_URL+ "api/user/activity/" + id_publicacion.toString() + "/comment");

        requestQueue.add(request);
    }

    public void mostrar_body(clsPublicacion oPublicacion, Boolean ver_spoiler){

        if (!ver_spoiler){
            lytSpoiler.setVisibility(View.GONE);
            lytBody.setVisibility(View.VISIBLE);

            String html="<!DOCTYPE html><html><body style=\"text-align:center;margin:0;\"><style>img{max-width:100%;}</style>";

            html+="<div style=\"text-align:left;border-left: 2px solid #026acb;margin: 10px 0;padding: 0 10px 0 5px;\">"+oPublicacion.feed.getTextoHtml()+"</div>";

            if (!oPublicacion.feed.getImagen().equalsIgnoreCase("")) html+= "<img src=\""+ROOT_URL+"static-img/" + oPublicacion.feed.getImagen()+"\" style=\"max-width:100%;\">";
            else if (!oPublicacion.feed.getVideo().equalsIgnoreCase("")) html+= "<a href=\""+oPublicacion.feed.getVideo()+"\"><iframe src=\"http://www.youtube.com/embed/"+oPublicacion.feed.getIdVideo()+"\" type=\"text/html\" width=\"100%\"></iframe></a>";

            html+="</body></html>";

            webBody.setWebViewClient(webClient);
            //holder.webBody.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webBody.getSettings().setJavaScriptEnabled(true);
            //holder.webBody.getSettings().setLoadWithOverviewMode(true);
            webBody.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);


        }else{
            lytBody.setVisibility(View.GONE);
            lytSpoiler.setVisibility(View.VISIBLE);
        }

    }

    public void republicar(final LinearLayout lytLikes, Integer id_publicacion){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();


        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("success").equalsIgnoreCase("true")){
                        Toast.makeText(getBaseContext(), "PUBLICACIÓN REPLICADA", Toast.LENGTH_SHORT).show();
                    }else{Toast.makeText(getBaseContext(), "PUBLICACIÓN NO REPLICADA", Toast.LENGTH_SHORT).show();}
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), "ERROR AL REPLICAR", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, ROOT_URL+"api/user/activity/"+id_publicacion.toString()+"/shares");

        requestQueue.add(request);
    }
}
