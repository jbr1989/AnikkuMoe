package es.jbr1989.anikkumoe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 01/06/2016.
 */
public class NuevaPublicacionActivity extends Activity {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;

    public EditText txtPublicacion;
    public Button btnAddPublicacion;
    public CheckBox chkSpoiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_publicacion);
        setTitle("Nueva publicación");

        oUsuarioSession = new clsUsuarioSession(getBaseContext());
        requestQueue = Volley.newRequestQueue(getBaseContext());

        txtPublicacion = (EditText) findViewById(R.id.txtPublicacion);
        btnAddPublicacion = (Button) findViewById(R.id.btnAddPublicacion);
        chkSpoiler = (CheckBox)  findViewById(R.id.chkSpoiler);

        btnAddPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String publicacionText = txtPublicacion.getText().toString();
                if (TextUtils.isEmpty(publicacionText)) {
                    Toast.makeText(getBaseContext(), "Publicación vacía", Toast.LENGTH_SHORT).show();
                    return;
                }
                addPublicacion(publicacionText);
            }
        });
    }

    public void addPublicacion(String publicacionText){

        btnAddPublicacion.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("texto", publicacionText);
        params.put("spoiler", String.valueOf(chkSpoiler.isChecked()));
        params.put("tipo", "1");
        //params.put("tags", "1");
        //params.put("usus", "1");
        //params.put("video", "1");
        //params.put("id_serie", "1");

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) Toast.makeText(getBaseContext(), "PUBLICADO", Toast.LENGTH_SHORT).show();
                    finish();
                }catch (JSONException ex){ex.printStackTrace();}

                btnAddPublicacion.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
                btnAddPublicacion.setEnabled(true);
            }
        }, ROOT_URL+"api/user/activity");

        requestQueue.add(request);
    }

}
