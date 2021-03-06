package es.jbr1989.anikkumoe.ListAdapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

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
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.fragment.PublicacionesFragment;
import es.jbr1989.anikkumoe.fragment.SerieFragment;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.CustomRequest2;
import es.jbr1989.anikkumoe.object.clsPublicacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.ExpandedListView;
import es.jbr1989.anikkumoe.other.clsDate;
import es.jbr1989.anikkumoe.other.clsTexto;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class PublicacionListAdapter extends RecyclerView.Adapter<PublicacionListAdapter.ViewHolder> {

    //region VARIABLES
    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String IMG_URL = AppController.getInstance().getImg();
    public static final String SP_NAME = "Publicaciones";

    private Context context;
    private Long ultima_fecha;

    private ComentariosListAdapter oListadoComentarios;

    private ArrayList<clsPublicacion> oPublicaciones;

    private clsDate oDate = new clsDate();

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;
    public CustomRequest2 request2;

    public homeActivity home;

    //endregion

    //region CONSTRUCTOR

    public PublicacionListAdapter(Context context){
        this.context = context;
        this.home = (homeActivity) context;

        oUsuarioSession = new clsUsuarioSession(context);
        requestQueue = Volley.newRequestQueue(context);

        oPublicaciones=new ArrayList<clsPublicacion>();
    }

    public PublicacionListAdapter(Context context, ArrayList<clsPublicacion> oPublicaciones) {
        this.context = context;
        this.home = (homeActivity) context;

        oUsuarioSession = new clsUsuarioSession(context);
        requestQueue = Volley.newRequestQueue(context);

        this.oPublicaciones = oPublicaciones;

        oListadoComentarios= new ComentariosListAdapter(context);
    }
    //endregion

    //region PRINCIPALES

    @Override
    public PublicacionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publicacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PublicacionListAdapter.ViewHolder holder, final int position) {
        final clsPublicacion oPublicacion=oPublicaciones.get(position);
        cargar_publicacion(holder, oPublicacion, position);
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

    public int indexOf_id(Integer id_publicacion){
        for (int i=0; i<oPublicaciones.size(); i++){
            if (oPublicaciones.get(i).feed.getId().equals(id_publicacion)){
                return i;
            }
        }
        return -1;
    }

    //endregion

    public void setPublicacion(clsPublicacion oPublicacion, Integer position){
        //Integer pos = indexOf_id(id_publicacion);
        if(position>=0) oPublicaciones.set(position,oPublicacion);
        notifyItemChanged(position);
    }

    public void setPublicaciones(JSONArray response){

        clsPublicacion oPublicacion;
        int startIndex = oPublicaciones.size();

        try {
            //JSONArray jPublicaciones=response.getJSONArray("data");

            int num = response.length();
            for (int i=0; i<num;i++){
                oPublicacion= new clsPublicacion(response.getJSONObject(i));
                ultima_fecha=oPublicacion.getFecha();
                oPublicaciones.add(oPublicacion);
                //if(oPublicacion.nuevo()) nuevos+=1;
            }

            notifyItemRangeInserted(startIndex, num);

        }catch (JSONException ex){ex.printStackTrace();}

    }

    public void setPublicaciones2(JSONObject response){

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
    }


    // Guardar item cargado
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //public final CardView lytPublicacion;
        public final LinearLayout lytBody, lytSpoiler, lytAnime, lytComentar,lytComentarios, lytLikes, lytReplicas, lytReactionLike, lytReactionLove, lytReactionHaha, lytReactionWow, lytReactionSorry, lytReactionAnger, lytReactionReplicas;
        public final SimpleDraweeView imgAvatar, imgAvatarOri, imgAnime, imgBody;
        public final ImageView imgLike;
        public final WebView webBody;
        public final TextView txtNombre, txtFecha, txtUsuario, txtNombreOri, txtAnime, txtComentarios, txtReactionLike, txtReactionLove, txtReactionHaha, txtReactionWow, txtReactionSorry, txtReactionAnger, txtReactionReplicas, txtLike, txtMessage;
        public final ExpandedListView lstComentarios;
        public final ImageButton btnComentario;

        public ViewHolder(View itemView){
            super(itemView);

            //this.lytPublicacion = (CardView) itemView.findViewById(R.id.lytPublicacion);
            this.lytBody = (LinearLayout) itemView.findViewById(R.id.lytBody);
            this.lytSpoiler = (LinearLayout) itemView.findViewById(R.id.lytSpoiler);
            this.lytAnime = (LinearLayout) itemView.findViewById(R.id.lytAnime);
            this.lytComentar = (LinearLayout)  itemView.findViewById(R.id.lytComentar);
            this.lytComentarios = (LinearLayout)  itemView.findViewById(R.id.lytComentarios);
            this.lytReplicas = (LinearLayout)  itemView.findViewById(R.id.lytReplicas);
            this.lytLikes = (LinearLayout)  itemView.findViewById(R.id.lytLikes);

            this.imgAvatar = (SimpleDraweeView) itemView.findViewById(R.id.imgPubAvatar);
            this.imgAvatarOri = (SimpleDraweeView) itemView.findViewById(R.id.imgPubAvatarOri);
            this.webBody= (WebView) itemView.findViewById(R.id.webBody);
            this.imgBody = (SimpleDraweeView) itemView.findViewById(R.id.imgBody);
            this.txtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
            this.txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
            this.txtUsuario = (TextView) itemView.findViewById(R.id.txtUsuario);
            this.txtNombreOri = (TextView) itemView.findViewById(R.id.txtNombreOri);

            this.imgAnime = (SimpleDraweeView) itemView.findViewById(R.id.imgAnime);
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

    //region PUBLICACION

    public void cargar_publicacion(final ViewHolder holder, final clsPublicacion oPublicacion, final Integer position){

        if (oPublicacion.getType().equalsIgnoreCase("REP")) {
            holder.imgAvatarOri.setImageURI(Uri.parse(IMG_URL + oPublicacion.user.getAvatar()));
            holder.txtNombreOri.setText(oPublicacion.user.getHTMLNombre());
        }

        holder.imgAvatar.setImageURI(Uri.parse(IMG_URL + (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getAvatar() : oPublicacion.user_original.getAvatar())));
        holder.txtNombre.setText((!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getHTMLNombre() : oPublicacion.user_original.getHTMLNombre()));
        holder.txtUsuario.setText("@" + (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getHTMLUsuario() : oPublicacion.user_original.getHTMLUsuario()));

        holder.imgAvatarOri.setVisibility((!oPublicacion.getType().equalsIgnoreCase("REP") ? View.GONE : View.VISIBLE));
        holder.txtNombreOri.setVisibility((!oPublicacion.getType().equalsIgnoreCase("REP") ? View.GONE : View.VISIBLE));

        holder.txtFecha.setText(oDate.DateDiff(oPublicacion.feed.getFecha(), System.currentTimeMillis()));

        mostrar_body(holder, oPublicacion, oPublicacion.feed.getSpoiler());

        holder.lytAnime.setVisibility(View.GONE);

        if(oPublicacion.feed.getAnime()!=null){
            holder.imgAnime.setImageURI(Uri.parse("http://www.anilista.com/img/dir/anime/regular/"+oPublicacion.feed.anime.getId().toString()+".jpg"));
            holder.txtAnime.setText(oPublicacion.feed.anime.getTitulo());
            holder.lytAnime.setVisibility(View.VISIBLE);
        }

        holder.txtComentarios.setText(oPublicacion.feed.stats.getComments());

        holder.lytReplicas.setBackgroundColor((oPublicacion.getIs_replicated() ? context.getResources().getColor(R.color.btn_rep) : ((Integer.parseInt(oUsuarioSession.getId())!=oPublicacion.user.getId()) ? Color.TRANSPARENT : context.getResources().getColor(R.color.btn_dislike))));
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
                if (oPublicacion.getType().equalsIgnoreCase("REP")==Boolean.FALSE) {
                    home.cargar_perfil(oPublicacion.user.getUsuario());
                }else{
                    home.cargar_perfil(oPublicacion.user_original.getUsuario());
                }
            }
        });

        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (oPublicacion.getType().equalsIgnoreCase("REP")==Boolean.FALSE) {
                    home.cargar_perfil(oPublicacion.user.getUsuario());
                }else{
                    home.cargar_perfil(oPublicacion.user_original.getUsuario());
                }
            }
        });

        holder.txtNombreOri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                home.cargar_perfil(oPublicacion.user.getUsuario());
            }
        });

        holder.imgAvatarOri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                home.cargar_perfil(oPublicacion.user.getUsuario());
            }
        });

        holder.lytSpoiler.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mostrar_body(holder, oPublicacion, false);
            }
        });

        holder.lytAnime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Bundle arguments = new Bundle();
                arguments.putString("id", oPublicacion.feed.anime.getId().toString());

                Fragment fragment = SerieFragment.newInstance(arguments);

                if (context instanceof homeActivity) {
                    homeActivity feeds = (homeActivity) context;
                    feeds.switchContent(fragment);
                }

                //Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(ROOT_URL+"serie/" + oPublicacion.feed.anime.getId()));
                //v.getContext().startActivity(browserIntent1);

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
                intent.putExtra("position", position);
                //intent.putExtra("usuario", (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getUsuario() : oPublicacion.user_original.getUsuario()));
                ((Activity) context).startActivityForResult(intent, PublicacionesFragment.REACTION_REQUEST);
            }
        });

        if (Integer.parseInt(oUsuarioSession.getId())!=oPublicacion.user.getId()) {
            holder.lytReplicas.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!oPublicacion.getIs_replicated())
                        republicar(holder.lytReplicas, oPublicacion.feed.getId());
                }
            });
        }

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


    //endregion

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

    public void mostrar_body(final PublicacionListAdapter.ViewHolder holder, final clsPublicacion oPublicacion, Boolean ver_spoiler){

        if (!ver_spoiler){
            holder.lytSpoiler.setVisibility(View.GONE);
            holder.lytBody.setVisibility(View.VISIBLE);

            String html = "<!DOCTYPE html><html><body style=\"text-align:center;margin:0;\">"+ clsTexto.styles();

            if (oPublicacion.feed.getActivity_type().equalsIgnoreCase("null")) {

                html += "<div style=\"text-align:justify;margin: 10px 5px;padding: 0;\">" + oPublicacion.feed.getTextoHtml() + "</div>";

                if (!oPublicacion.feed.getImagen().equalsIgnoreCase("")) {
                    holder.imgBody.setVisibility(View.VISIBLE);

                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(Uri.parse(ROOT_URL + "static-img/" + oPublicacion.feed.getImagen()))
                            .setAutoPlayAnimations(true)
                            .build();

                    holder.imgBody.setController(controller);
                    holder.imgBody.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

                    holder.imgBody.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            home.webClient.cargar_img(ROOT_URL + "static-img/" + oPublicacion.feed.getImagen());
                        }
                    });
                    //html += "<a href=\"img:" + ROOT_URL + "static-img/" + oPublicacion.feed.getImagen() + "\"><img src=\"" + ROOT_URL + "static-img/" + oPublicacion.feed.getImagen() + "\" style=\"max-width:100%;\"></a>";
                }else if (!oPublicacion.feed.getVideo().equalsIgnoreCase("")) {
                    holder.imgBody.setVisibility(View.GONE);
                    html += "<a href=\"" + oPublicacion.feed.getVideo() + "\"><iframe src=\"http://www.youtube.com/embed/" + oPublicacion.feed.getIdVideo() + "\" type=\"text/html\" width=\"100%\"></iframe></a>";
                }
            }else if(oPublicacion.feed.getActivity_type().equalsIgnoreCase("waifu")){
                html += "<div style=\"padding: 10px; text-align: center\">" + oPublicacion.feed.activity_data.getHtml(oPublicacion.getFecha()) + "</div>";
                holder.imgBody.setVisibility(View.GONE);
            }

            html += "</body></html>";

            holder.webBody.setWebViewClient(home.webClient);
            //holder.webBody.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            holder.webBody.getSettings().setJavaScriptEnabled(true);
            //holder.webBody.getSettings().setLoadWithOverviewMode(true);
            holder.webBody.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);


        }else{
            holder.lytBody.setVisibility(View.GONE);
            holder.lytSpoiler.setVisibility(View.VISIBLE);
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
