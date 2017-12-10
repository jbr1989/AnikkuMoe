package es.jbr1989.anikkumoe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
import es.jbr1989.anikkumoe.object.clsUsuario;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.sqlite.ConfigSQLite;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    clsUsuarioSession oUsuarioSession;

    Context ctx;

    ProgressDialog pDialog;

    public RequestQueue requestQueue;
    public CustomRequest request;

    private EditText txtUser, txtPassword;
    private CheckBox chkRecordarPassword, chkLoginAutomatico;
    private TextView lblRecuperar, lblRegistro;
    private Button btnLogin;
    private String user, password;
    private TextView txtError;

    public SharedPreferences Config;
    public ConfigSQLite oConfigSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx=this;

        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        chkLoginAutomatico = (CheckBox) findViewById(R.id.chkLoginAutomatico);
        chkRecordarPassword = (CheckBox) findViewById(R.id.chkRecordarPassword);
        lblRecuperar = (TextView) findViewById(R.id.lblRecuperar);
        lblRegistro=(TextView) findViewById(R.id.lblRegistro);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        chkRecordarPassword.setOnClickListener(this);
        lblRecuperar.setOnClickListener(this);
        lblRegistro.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        oUsuarioSession = new clsUsuarioSession(this);

        Config = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        oConfigSQL = new ConfigSQLite(ctx);

        if (Config.getBoolean("login_recordar_password", false)){
            txtUser.setText(oConfigSQL.getConfig("user"));
            txtPassword.setText(oConfigSQL.getConfig("password"));

            chkRecordarPassword.setChecked(true);
        }else{
            chkRecordarPassword.setChecked(false);
            chkLoginAutomatico.setEnabled(false);
        }

        chkLoginAutomatico.setChecked(chkLoginAutomatico.isEnabled() && Config.getBoolean("login_automatico", false));
    }

    @Override
    public void onClick (View v){
        switch(v.getId()){
            case R.id.btnLogin:
                user = txtUser.getText().toString();
                password = txtPassword.getText().toString();

                SharedPreferences.Editor editor=Config.edit();
                editor.putBoolean("login_recordar_password", chkRecordarPassword.isChecked());
                editor.putBoolean("login_automatico", chkLoginAutomatico.isChecked());
                editor.commit();

                if (user.length()==0 || password.length()==0) Toast.makeText(MainActivity.this, "Usuario y contraseña obligatorios", Toast.LENGTH_SHORT).show();
                else login(user, password, true);

                break;
            case R.id.chkRecordarPassword:
                chkLoginAutomatico.setEnabled(chkRecordarPassword.isChecked());
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

    public void login(final String user, final String password, final Boolean iniciar){

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
                datos_usuario(user, token, iniciar);
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

    public void datos_usuario(final String usuario, final String token, final Boolean iniciar){

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

                oConfigSQL.setConfig("user", txtUser.getText().toString());
                oConfigSQL.setConfig("password", (chkRecordarPassword.isChecked() ? txtPassword.getText().toString() : ""));

                if (iniciar) {
                    Intent i = new Intent(MainActivity.this, homeActivity.class);
                    finish();
                    startActivity(i);
                }

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
