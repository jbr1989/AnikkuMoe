package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.object.clsComentario;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 16/05/2016.
 */
public class ComentariosListAdapter extends BaseAdapter {


    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String IMG_URL = AppController.getInstance().getImg();

    public static final String SP_NAME = "Comentarios";
    public Integer pos;

    private Context context;
    private Long fecha;
    private Long ultima_fecha;

    private clsUsuarioSession oUsuarioSession;
    private ArrayList<clsComentario> oComentarios;
    private Map<String, String> MSG_NOTIFICACION;

    private clsDate oDate = new clsDate();
    private Integer nuevos;

    private SharedPreferences ComentariosConfig;
    private SharedPreferences.Editor ComentariosConfigEditor;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public homeActivity home;

    //endregion

    //region CONSTRUCTOR

    public ComentariosListAdapter(Context context){
        this.context = context;
        this.home = (homeActivity) context;

        oUsuarioSession = new clsUsuarioSession(context);

        oComentarios=new ArrayList<clsComentario>();
        nuevos=0;
        ultima_fecha=null;

        ComentariosConfig = context.getSharedPreferences(SP_NAME, 0);
        ComentariosConfigEditor = ComentariosConfig.edit();
    }

    //endregion

    @Override
    public int getCount() { return oComentarios.size();}

    @Override
    public Object getItem(int position) {
        return oComentarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comentario, parent, false);
            viewHolder =new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else{
            viewHolder  = (ViewHolder) convertView.getTag();
        }

        final clsComentario oComentario=oComentarios.get(position);

        viewHolder.imgAvatar.setImageURI(Uri.parse(IMG_URL + oComentario.user.getAvatar()));
        viewHolder.txtUsuario.setText(oComentario.user.getUsuario());
        viewHolder.txtNombre.setText(oComentario.user.getNombre());

        viewHolder.txtFecha.setText(oDate.DateDiff(oComentario.getFecha(), System.currentTimeMillis()));
        viewHolder.txtBody.setText(oComentario.getHTMLTexto());
        viewHolder.txtBody.setMovementMethod(LinkMovementMethod.getInstance());
        //viewHolder.webBody.loadDataWithBaseURL(null, oComentario.getHTMLTexto(), "text/html", "UTF-8", null);
        //viewHolder.webBody.setBackgroundColor(0x00000000);

        //mostrar_body(viewHolder,oComentario);

        viewHolder.txtUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                home.cargar_perfil(oComentario.user.getUsuario());
            }
        });

        viewHolder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                home.cargar_perfil(oComentario.user.getUsuario());
            }
        });

        return convertView;
    }

    public void mostrar_body(final ComentariosListAdapter.ViewHolder holder, clsComentario oComentario){

            String html="<!DOCTYPE html><html><body style=\"text-align:center;margin:0;\"><style>img{max-width:100%;}</style>";

            html+="<div style=\"text-align:left;border-left: 2px solid #026acb;margin: 10px 0;padding: 0 10px 0 5px;\">"+oComentario.getHTMLTexto()+"</div>";

            html+="</body></html>";

            holder.webBody.setWebViewClient(home.webClient);
            holder.webBody.getSettings().setJavaScriptEnabled(true);
            holder.webBody.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }


    public String getUrlUser(clsComentario oComentario){
        return ROOT_URL+"user/" + oComentario.user.getUsuario();
    }

    public void setComentarios(JSONArray response){

        clsComentario oComentario;

        try {
            //JSONArray jNotificaciones=response.getJSONArray("data");

            int num = response.length();
            for (int i=0; i<num;i++){
                oComentario= new clsComentario(response.getJSONObject(i));
                oComentarios.add(oComentario);
                //if(oComentario.nuevo()) nuevos+=1;
            }

        }catch (JSONException ex){ex.printStackTrace();}
    }

    public boolean setNewComentarios(JSONObject response){

        clsComentario oComentario;

        oComentario= new clsComentario(response);
        //ultima_fecha=oComentario.getEnviado13();
        oComentarios.add(oComentario);

        return true;
    }



    public void clearComentarios(){
        oComentarios.clear();
        nuevos=0;
    }

    // Guardar item cargado
    private static class ViewHolder {
        public final SimpleDraweeView imgAvatar;
        public final TextView txtUsuario, txtBody, txtNombre, txtFecha;
        public final WebView webBody;

        public ViewHolder(View v){
            this.imgAvatar = (SimpleDraweeView) v.findViewById(R.id.ImgAvatar);
            this.txtUsuario = (TextView) v.findViewById(R.id.txtUsuario);
            this.txtBody = (TextView) v.findViewById(R.id.txtBody);
            this.webBody = (WebView) v.findViewById(R.id.webBody);
            this.txtNombre = (TextView) v.findViewById(R.id.txtNombre);
            this.txtFecha = (TextView) v.findViewById(R.id.txtFecha);
        }

    }


    //region CONFIGURACION

    public void putConfigNewsCount(){
        ComentariosConfigEditor.putInt("nuevos", this.getCount());
        ComentariosConfigEditor.commit();
    }

    public Integer getConfigNewsCount(){
        return ComentariosConfig.getInt("nuevos",0);
    }

    //endregion

}
