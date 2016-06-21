package es.jbr1989.anikkumoe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsPublicacion;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 21/06/2016.
 */
public class PublicacionActivity extends Activity {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private clsPublicacion oPublicacion;

    private Integer id_publicacion;
    private String usuario;

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publicacion);
        setTitle("Publicación");

        oUsuarioSession = new clsUsuarioSession(this);
        requestQueue = Volley.newRequestQueue(this);

        Intent i= getIntent();
        Bundle extras = i.getExtras();
        if(extras != null) {
            id_publicacion = extras.getInt("id_publicacion");
            usuario = extras.getString("usuario");



        }
    }
}
