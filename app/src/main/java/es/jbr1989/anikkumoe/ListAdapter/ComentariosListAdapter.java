package es.jbr1989.anikkumoe.ListAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.object.clsChat;
import es.jbr1989.anikkumoe.object.clsComentario;
import es.jbr1989.anikkumoe.object.clsPublicacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsDate;

/**
 * Created by jbr1989 on 16/05/2016.
 */
public class ComentariosListAdapter extends BaseAdapter {


    //region VARIABLES

    private static final String ROOT_URL = AppController.getInstance().getUrl();

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

    //endregion

    //region CONSTRUCTOR

    public ComentariosListAdapter(Context context){
        this.context = context;

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

        NetworkImageView imgAvatar;
        TextView txtUsuario, txtNombre, txtFecha;
        WebView webBody;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comentario, parent, false);
            viewHolder =new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else{
            viewHolder  = (ViewHolder) convertView.getTag();
        }

        final clsComentario oComentario=oComentarios.get(position);

        viewHolder.imgAvatar.setImageUrl(ROOT_URL + "/static-img/" + oComentario.user.getAvatar(), imageLoader);
        viewHolder.txtUsuario.setText(oComentario.user.getUsuario());
        viewHolder.txtNombre.setText(oComentario.user.getNombre());

        viewHolder.txtFecha.setText(oDate.DateDiff(oComentario.getFecha(), System.currentTimeMillis()));
        viewHolder.txtBody.setText(oComentario.getHTMLTexto());
        //viewHolder.webBody.loadDataWithBaseURL(null, oComentario.getHTMLTexto(), "text/html", "UTF-8", null);
        //viewHolder.webBody.setBackgroundColor(0x00000000);

        viewHolder.txtUsuario.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrlUser(oComentario)));
                v.getContext().startActivity(browserIntent1);
            }
        });

        viewHolder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(getUrlUser(oComentario)));
                v.getContext().startActivity(browserIntent1);
            }
        });

        return convertView;
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
        public final NetworkImageView imgAvatar;
        public final TextView txtUsuario, txtBody, txtNombre, txtFecha;

        public ViewHolder(View v){
            this.imgAvatar = (NetworkImageView) v.findViewById(R.id.ImgAvatar);
            this.txtUsuario = (TextView) v.findViewById(R.id.txtUsuario);
            this.txtBody = (TextView) v.findViewById(R.id.txtBody);
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
