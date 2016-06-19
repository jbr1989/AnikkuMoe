package es.jbr1989.anikkumoe.ListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.ReactionActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsPublicacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.ExpandedListView;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class PublicacionListAdapter extends RecyclerView.Adapter<PublicacionListAdapter.ViewHolder> {

    //region VARIABLES
    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String API_URL = AppController.getInstance().getApi();
    public static final String SP_NAME = "Publicaciones";
    public Integer pos;

    private Context context;
    private Long ultima_fecha;

    private ComentariosListAdapter oListadoComentarios;

    private ArrayList<clsPublicacion> oPublicaciones;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private SharedPreferences PublicacionesConfig;
    private SharedPreferences.Editor PublicacionesConfigEditor;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;
    public CustomRequest2 request2;

    //endregion

    //region CONSTRUCTOR

    public PublicacionListAdapter(Context context){
        this.context = context;

        oUsuarioSession = new clsUsuarioSession(context);
        requestQueue = Volley.newRequestQueue(context);

        oPublicaciones=new ArrayList<clsPublicacion>();
        nuevos=0;

        PublicacionesConfig = context.getSharedPreferences(SP_NAME, 0);
        PublicacionesConfigEditor = PublicacionesConfig.edit();

    }

    public PublicacionListAdapter(Context context, ArrayList<clsPublicacion> oPublicaciones) {
        this.context = context;

        oUsuarioSession = new clsUsuarioSession(context);
        requestQueue = Volley.newRequestQueue(context);

        this.oPublicaciones = oPublicaciones;
        nuevos=0;

        PublicacionesConfig = context.getSharedPreferences(SP_NAME, 0);
        PublicacionesConfigEditor = PublicacionesConfig.edit();

        oListadoComentarios= new ComentariosListAdapter(context);

    }
    //endregion

    //region PRINCIPALES

    @Override
    public PublicacionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PublicacionListAdapter.ViewHolder holder, final int position) {

        final clsPublicacion oPublicacion=oPublicaciones.get(position);

        if (oPublicacion.getType().equalsIgnoreCase("REP")) {
            holder.imgAvatarOri.setImageUrl(ROOT_URL+"static-img/" + oPublicacion.user.getAvatar(), imageLoader);
            holder.txtNombreOri.setText(oPublicacion.user.getHTMLNombre());
        }

        holder.imgAvatar.setImageUrl(ROOT_URL+"static-img/" + (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getAvatar() : oPublicacion.user_original.getAvatar()), imageLoader);
        holder.txtNombre.setText((!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getHTMLNombre() : oPublicacion.user_original.getHTMLNombre()));
        holder.txtUsuario.setText("@" + (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getHTMLUsuario() : oPublicacion.user_original.getHTMLUsuario()));

        holder.imgAvatarOri.setVisibility((!oPublicacion.getType().equalsIgnoreCase("REP") ? View.GONE : View.VISIBLE));
        holder.txtNombreOri.setVisibility((!oPublicacion.getType().equalsIgnoreCase("REP") ? View.GONE : View.VISIBLE));

        holder.txtFecha.setText(oDate.DateDiff(oPublicacion.feed.getFecha(), System.currentTimeMillis()));

        mostrar_body(holder, oPublicacion, oPublicacion.feed.getSpoiler());

        holder.lytAnime.setVisibility(View.GONE);

        if(oPublicacion.feed.getAnime()!=null){
            holder.imgAnime.setImageUrl("http://www.anilista.com/img/dir/anime/regular/"+oPublicacion.feed.anime.getId().toString()+".jpg", imageLoader);
            holder.txtAnime.setText(oPublicacion.feed.anime.getTitulo());
            holder.lytAnime.setVisibility(View.VISIBLE);
        }

        holder.txtComentarios.setText(oPublicacion.feed.stats.getComments());

        holder.lytReplicas.setBackgroundColor((oPublicacion.getIs_replicated() ? context.getResources().getColor(R.color.btn_rep) : Color.TRANSPARENT));
        //holder.lytLikes.setBackgroundColor((oPublicacion.getMy_reaction() ? context.getResources().getColor(R.color.btn_like) : Color.TRANSPARENT));

        holder.lytReactionLike.setVisibility((oPublicacion.feed.stats.reactions.getLike()>0 ? View.VISIBLE : View.GONE));
        holder.lytReactionLove.setVisibility((oPublicacion.feed.stats.reactions.getLove()>0 ? View.VISIBLE : View.GONE));
        holder.lytReactionHaha.setVisibility((oPublicacion.feed.stats.reactions.getHaha()>0 ? View.VISIBLE : View.GONE));
        holder.lytReactionWow.setVisibility((oPublicacion.feed.stats.reactions.getWow()>0 ? View.VISIBLE : View.GONE));
        holder.lytReactionSorry.setVisibility((oPublicacion.feed.stats.reactions.getSorry()>0 ? View.VISIBLE : View.GONE));
        holder.lytReactionAnger.setVisibility((oPublicacion.feed.stats.reactions.getAnger()>0 ? View.VISIBLE : View.GONE));
        holder.lytReactionReplicas.setVisibility((Integer.parseInt(oPublicacion.feed.stats.getShared())>0 ? View.VISIBLE : View.GONE));


        holder.txtReactionLike.setText(oPublicacion.feed.stats.reactions.getLike().toString());
        holder.txtReactionLove.setText(oPublicacion.feed.stats.reactions.getLove().toString());
        holder.txtReactionHaha.setText(oPublicacion.feed.stats.reactions.getHaha().toString());
        holder.txtReactionWow.setText(oPublicacion.feed.stats.reactions.getWow().toString());
        holder.txtReactionSorry.setText(oPublicacion.feed.stats.reactions.getSorry().toString());
        holder.txtReactionAnger.setText(oPublicacion.feed.stats.reactions.getAnger().toString());
        holder.txtReactionReplicas.setText(oPublicacion.feed.stats.getShared());

        holder.lytComentarios.setVisibility(View.GONE);

        holder.txtNombre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(oPublicacion.getUrlUser(ROOT_URL)));
                v.getContext().startActivity(browserIntent1);
            }
        });

        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(oPublicacion.getUrlUser(ROOT_URL)));
                v.getContext().startActivity(browserIntent1);
            }
        });

        holder.txtNombreOri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(ROOT_URL+"user/" + oPublicacion.user_original.getUsuario()));
                v.getContext().startActivity(browserIntent1);
            }
        });

        holder.imgAvatarOri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(ROOT_URL+"user/" + oPublicacion.user_original.getUsuario()));
                v.getContext().startActivity(browserIntent1);
            }
        });

        holder.lytSpoiler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mostrar_body(holder, oPublicacion, false);
            }
        });

        holder.lytAnime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(ROOT_URL+"serie/" + oPublicacion.feed.anime.getId()));
                v.getContext().startActivity(browserIntent1);

            }
        });

        holder.lytComentar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(holder.lytComentarios.getVisibility()!=View.VISIBLE){
                    oListadoComentarios= new ComentariosListAdapter(context);
                    holder.lstComentarios.setAdapter(oListadoComentarios);
                    cargar_comentarios(holder.lstComentarios, oPublicacion.feed.getId());
                    holder.lytComentarios.setVisibility(View.VISIBLE);
                }else{
                    holder.lytComentarios.setVisibility(View.GONE);
                }
            }
        });

        holder.lytLikes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(v.getContext(), ReactionActivity.class);
                intent.putExtra("id_publicacion", oPublicacion.feed.getId());
                //intent.putExtra("usuario", (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getUsuario() : oPublicacion.user_original.getUsuario()));
                ((Activity) context).startActivityForResult(intent, 1);
            }
        });

        holder.lytReplicas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!oPublicacion.getIs_replicated()) republicar(holder.lytReplicas, position, oPublicacion.feed.getId());
            }
        });

        holder.btnComentario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String messageText = holder.txtMessage.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    Toast.makeText(context, "Escribe un comentario", Toast.LENGTH_SHORT).show();
                    return;
                }
                comentar(holder, oPublicacion.feed.getId(),messageText);
            }
        });


        String my_reaction =oPublicacion.getMy_reaction();

        if (my_reaction.equalsIgnoreCase("like")) {
            holder.imgLike.setImageResource(R.drawable.reaction_like_32);
            holder.txtLike.setText("Me gusta");
        } else if (my_reaction.equalsIgnoreCase("love")){
            holder.imgLike.setImageResource(R.drawable.reaction_love_32);
            holder.txtLike.setText("Sugoi");
        } else if (my_reaction.equalsIgnoreCase("haha")){
            holder.imgLike.setImageResource(R.drawable.reaction_haha_32);
            holder.txtLike.setText("HaHa");
        } else if (my_reaction.equalsIgnoreCase("wow")){
            holder.imgLike.setImageResource(R.drawable.reaction_wow_32);
            holder.txtLike.setText("Ara ara");
        } else if (my_reaction.equalsIgnoreCase("sorry")){
            holder.imgLike.setImageResource(R.drawable.reaction_sorry_32);
            holder.txtLike.setText("Me entristece");
        } else if (my_reaction.equalsIgnoreCase("anger")) {
            holder.imgLike.setImageResource(R.drawable.reaction_anger_32);
            holder.txtLike.setText("Me enfada");
        }else{
            holder.imgLike.setImageResource(R.drawable.icon_like);
            holder.txtLike.setText("Me gusta");
        }



    }

    @Override
    public int getItemCount() {
        return oPublicaciones.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(clsPublicacion oPublicacion) {
        insert(oPublicacion, oPublicaciones.size());
    }

    public void insert(clsPublicacion oPublicacion, int position) {
        oPublicaciones.add(position, oPublicacion);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        oPublicaciones.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = oPublicaciones.size();
        oPublicaciones.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(clsPublicacion[] oPubs) {
        int startIndex = oPublicaciones.size();
        oPublicaciones.addAll(startIndex, Arrays.asList(oPubs));
        notifyItemRangeInserted(startIndex, oPubs.length);
    }

    //endregion

    //region GETTER

    public Object getItem(int position) {
        return oPublicaciones.get(position);
    }

    public Long get_UltimaFecha() {
        return ultima_fecha;
    }

    //endregion

    public void setPublicaciones(JSONObject response){

        clsPublicacion oPublicacion;
        int startIndex = oPublicaciones.size();

        try {
            JSONArray jPublicaciones=response.getJSONArray("data");

            int num = jPublicaciones.length();
            for (int i=0; i<num;i++){
                oPublicacion= new clsPublicacion(jPublicaciones.getJSONObject(i));
                ultima_fecha=oPublicacion.getFecha();
                oPublicaciones.add(oPublicacion);
                //if(oPublicacion.nuevo()) nuevos+=1;
            }

            notifyItemRangeInserted(startIndex, num);

        }catch (JSONException ex){ex.printStackTrace();}

    }

    public void clearPublicaciones(){
        oPublicaciones.clear();
        nuevos=0;
    }


    // Guardar item cargado
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout lytPublicacion, lytBody, lytSpoiler, lytAnime, lytComentar,lytComentarios, lytLikes, lytReplicas, lytReactionLike, lytReactionLove, lytReactionHaha, lytReactionWow, lytReactionSorry, lytReactionAnger, lytReactionReplicas;
        public final NetworkImageView imgAvatar,imgAvatarOri,imgAnime;
        public final ImageView imgLike;
        public final WebView webBody;
        public final TextView txtNombre, txtFecha, txtUsuario, txtNombreOri, txtBody, txtAnime, txtComentarios, txtReactionLike, txtReactionLove, txtReactionHaha, txtReactionWow, txtReactionSorry, txtReactionAnger, txtReactionReplicas, txtLike, txtMessage;
        public final ExpandedListView lstComentarios;
        public final ImageButton btnComentario;

        public ViewHolder(View itemView){
            super(itemView);

            this.lytPublicacion = (LinearLayout) itemView.findViewById(R.id.lytPublicacion);
            this.lytBody = (LinearLayout) itemView.findViewById(R.id.lytBody);
            this.lytSpoiler = (LinearLayout) itemView.findViewById(R.id.lytSpoiler);
            this.lytAnime = (LinearLayout) itemView.findViewById(R.id.lytAnime);
            this.lytComentar = (LinearLayout)  itemView.findViewById(R.id.lytComentar);
            this.lytComentarios = (LinearLayout)  itemView.findViewById(R.id.lytComentarios);
            this.lytReplicas = (LinearLayout)  itemView.findViewById(R.id.lytReplicas);
            this.lytLikes = (LinearLayout)  itemView.findViewById(R.id.lytLikes);

            this.imgAvatar = (NetworkImageView) itemView.findViewById(R.id.imgPubAvatar);
            this.imgAvatarOri = (NetworkImageView) itemView.findViewById(R.id.imgPubAvatarOri);
            this.webBody= (WebView) itemView.findViewById(R.id.webBody);
            this.txtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            this.txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
            this.txtUsuario = (TextView) itemView.findViewById(R.id.txtUsuario);
            this.txtNombreOri = (TextView) itemView.findViewById(R.id.txtNombreOri);
            this.txtBody = (TextView) itemView.findViewById(R.id.txtBody);

            this.imgAnime = (NetworkImageView) itemView.findViewById(R.id.imgAnime);
            this.txtAnime=(TextView) itemView.findViewById(R.id.txtAnime);
            this.txtComentarios = (TextView) itemView.findViewById(R.id.txtComentarios);

            this.lytReactionLike = (LinearLayout) itemView.findViewById(R.id.lytReactionLike);
            this.lytReactionLove = (LinearLayout) itemView.findViewById(R.id.lytReactionLove);
            this.lytReactionHaha = (LinearLayout) itemView.findViewById(R.id.lytReactionHaha);
            this.lytReactionWow = (LinearLayout) itemView.findViewById(R.id.lytReactionWow);
            this.lytReactionSorry = (LinearLayout) itemView.findViewById(R.id.lytReactionSorry);
            this.lytReactionAnger = (LinearLayout) itemView.findViewById(R.id.lytReactionAnger);
            this.lytReactionReplicas = (LinearLayout) itemView.findViewById(R.id.lytReactionReplicas);

            this.txtReactionLike = (TextView) itemView.findViewById(R.id.txtReactionLike);
            this.txtReactionLove = (TextView) itemView.findViewById(R.id.txtReactionLove);
            this.txtReactionHaha = (TextView) itemView.findViewById(R.id.txtReactionHaha);
            this.txtReactionWow = (TextView) itemView.findViewById(R.id.txtReactionWow);
            this.txtReactionSorry = (TextView) itemView.findViewById(R.id.txtReactionSorry);
            this.txtReactionAnger = (TextView) itemView.findViewById(R.id.txtReactionAnger);
            this.txtReactionReplicas = (TextView) itemView.findViewById(R.id.txtReactionReplicas);

            this.imgLike=(ImageView) itemView.findViewById(R.id.imgLike);
            this.txtLike=(TextView)  itemView.findViewById(R.id.txtLike);

            this.lstComentarios= (ExpandedListView) itemView.findViewById(R.id.lstComentarios);
            this.btnComentario = (ImageButton) itemView.findViewById(R.id.btnComentario);
            this.txtMessage = (EditText) itemView.findViewById(R.id.txtMensaje);
        }
    }

    //region COMENTARIOS

    public void cargar_comentarios(final ExpandedListView lstComentarios, Integer id_publicacion){

        oListadoComentarios.clearComentarios();

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

                //lstComentarios.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/user/activity/"+id_publicacion.toString()+"/comment?page=0");

        requestQueue.add(request2);

    }

    public void comentar(final ViewHolder holder, Integer id_publicacion, String messageText){

        holder.btnComentario.setEnabled(false);

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
                holder.txtMessage.setText("");
                holder.btnComentario.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                holder.btnComentario.setEnabled(true);
            }
        }, ROOT_URL+ "api/user/activity/" + id_publicacion.toString() + "/comment");

        requestQueue.add(request);
    }
/*
    public void clearMessage(){
        messageText.setText("");
    }
*/
    //endregion

    //region OPCIONES PUBLICACION

    public void mostrar_body(final PublicacionListAdapter.ViewHolder holder, clsPublicacion oPublicacion, Boolean ver_spoiler){

        if (!ver_spoiler){
            holder.lytSpoiler.setVisibility(View.GONE);
            holder.lytBody.setVisibility(View.VISIBLE);

            holder.txtBody.setText(oPublicacion.feed.getHTMLTexto());
            holder.webBody.setVisibility((!oPublicacion.feed.getImagen().equalsIgnoreCase("") || !oPublicacion.feed.getVideo().equalsIgnoreCase("") ? View.VISIBLE : View.GONE));

            if (!oPublicacion.feed.getImagen().equalsIgnoreCase("")) {
                String htnlString = "<!DOCTYPE html><html><body style=\"text-align:center;margin:0;\"><img src=\""+ROOT_URL+"static-img/" + oPublicacion.feed.getImagen()+"\" style=\"max-width:100%;\"></body></html>";
                holder.webBody.loadDataWithBaseURL(null, htnlString, "text/html", "UTF-8", null);
                holder.webBody.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }else if (!oPublicacion.feed.getVideo().equalsIgnoreCase("")){
                String htnlString = "<!DOCTYPE html><html><body style=\"text-align:center;margin:0;\"><a href=\""+oPublicacion.feed.getVideo()+"\"><iframe src=\"http://www.youtube.com/embed/"+oPublicacion.feed.getIdVideo()+"\" type=\"text/html\" width=\"100%\"></iframe></a></body></html>";
                holder.webBody.getSettings().setPluginState(WebSettings.PluginState.ON);
                holder.webBody.getSettings().setJavaScriptEnabled(true);
                //holder.webBody.getSettings().setUseWideViewPort(true);
                holder.webBody.getSettings().setLoadWithOverviewMode(true);
                holder.webBody.loadDataWithBaseURL(null, htnlString, "text/html", "UTF-8", null);
            }
        }else{
            holder.lytBody.setVisibility(View.GONE);
            holder.lytSpoiler.setVisibility(View.VISIBLE);
        }

    }

    public void republicar(final LinearLayout lytLikes, final Integer position, Integer id_publicacion){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();


        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("success").equalsIgnoreCase("true")){
                        Toast.makeText(context, "PUBLICACIÓN REPLICADA", Toast.LENGTH_SHORT).show();
                    }else{Toast.makeText(context, "PUBLICACIÓN NO REPLICADA", Toast.LENGTH_SHORT).show();}
                } catch (JSONException e) {
                    Toast.makeText(context, "ERROR AL REPLICAR", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, ROOT_URL+"api/user/activity/"+id_publicacion.toString()+"/shares");

        requestQueue.add(request);
    }

    //enregion

}
