package es.jbr1989.anikkumoe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.NotifyService;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuario;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    private static final String API_URL = AppController.getInstance().getApi();

    clsUsuarioSession oUsuarioSession;
    SharedPreferences Config;

    ProgressDialog pDialog;

    public RequestQueue requestQueue;
    public CustomRequest request;

    private EditText txtUser, txtPassword;
    private TextView lblRecuperar, lblRegistro;
    private Button btnLogin;
    private String user, password;
    private TextView txtError;

    private com.basgeekball.awesomevalidation.AwesomeValidation mAwesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("LOAD", "HOLA");

        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        lblRecuperar = (TextView) findViewById(R.id.lblRecuperar);
        lblRegistro=(TextView) findViewById(R.id.lblRegistro);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        lblRecuperar.setOnClickListener(this);
        lblRegistro.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        oUsuarioSession = new clsUsuarioSession(this);
        Config = PreferenceManager.getDefaultSharedPreferences(this);


        clearValidation();
        mAwesomeValidation = new AwesomeValidation(BASIC);
        addValidation(this);
    }

    private void clearValidation() {
        if (mAwesomeValidation != null) {
            mAwesomeValidation.clear();
        }
    }

    private void addValidation(final Activity activity) {
        mAwesomeValidation.addValidation(activity, R.id.txtUser,RegexTemplate.NOT_EMPTY, R.string.validate_user_empty);
        mAwesomeValidation.addValidation(activity, R.id.txtPassword, RegexTemplate.NOT_EMPTY, R.string.validate_pass_empty);
    }

    @Override
    public void onClick (View v){
        switch(v.getId()){
            case R.id.btnLogin:
                Log.d("CLICK", "HOLA");

                user = txtUser.getText().toString();
                password = txtPassword.getText().toString();

                //new AttemptLogin().execute();

                if (mAwesomeValidation.validate()) login();

                break;
            case R.id.lblRecuperar:
            case R.id.lblRegistro:

                Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(ROOT_URL+"login"));
                startActivity(browserIntent1);

                break;
        }
    }


    @Override
    protected void onStart() {
        if (oUsuarioSession.getLogin()==true) {
            Intent i = new Intent(MainActivity.this, homeActivity.class);
            finish();
            startActivity(i);
        }
        super.onStart();
    }

    private void login(){

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Comprobando...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        Map<String, String> params = new HashMap<String, String>();
        params.put("usuario", user);
        params.put("password", password);

        requestQueue = Volley.newRequestQueue(this);
        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            try {
                String token = response.getString("token");
                oUsuarioSession.login(user, token);
                datos_usuario(user, token);
            }
            catch (JSONException ex) {}

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Usuario y contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        },ROOT_URL+"api/authenticate" );

        requestQueue.add(request);

    }

    public void datos_usuario(String usuario, String token){

        pDialog.setMessage("Cargando usuario...");
        pDialog.show();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + token);

        Map<String, String> params = new HashMap<String, String>();

        requestQueue = Volley.newRequestQueue(this);
        request = new CustomRequest(requestQueue, Request.Method.GET, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                clsUsuario oUsuario = new clsUsuario();
                oUsuario.setUsuario(response);
                oUsuarioSession.setUsuario(oUsuario);
                oUsuarioSession.setLogin(true);

                Intent i = new Intent(MainActivity.this, homeActivity.class);
                finish();
                startActivity(i);

                if (Config.getBoolean("notificacion_activo",true))
                    startService(new Intent(MainActivity.this, NotifyService.class));

                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, ROOT_URL+"api/user/"+usuario+"/page");

        requestQueue.add(request);
    }


}
