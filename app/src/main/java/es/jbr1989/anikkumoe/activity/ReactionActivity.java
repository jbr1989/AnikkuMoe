package es.jbr1989.anikkumoe.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.ReactionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 21/04/2016.
 */
public class ReactionActivity extends ListActivity {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    public static String RESULT_ID="id";
    public static String RESULT_POSITION= "position";
    public static String RESULT_REACTION = "reaction";

    public String[] countrynames, countrycodes;
    private TypedArray imgs;
    private List<Country> countryList;

    private Integer id_publicacion;
    private Integer position;

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;

    public Intent returnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oUsuarioSession = new clsUsuarioSession(this);
        requestQueue = Volley.newRequestQueue(this);

        populateCountryList();
        ArrayAdapter<Country> adapter = new ReactionListAdapter(this, countryList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country c = countryList.get(position);

                like(c.getCode());
                //Toast.makeText(view.getContext(), "CODE: "+c.getCode(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(view.getContext(), "ID:"+id_publicacion.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        Intent i= getIntent();
        Bundle extras = i.getExtras();
        if(extras != null) {
            id_publicacion = extras.getInt("id_publicacion");
            position= extras.getInt("position");
        }
    }

    private void populateCountryList() {
        countryList = new ArrayList<Country>();
        countrynames = getResources().getStringArray(R.array.reactions_names);
        countrycodes = getResources().getStringArray(R.array.reactions_values);
        imgs = getResources().obtainTypedArray(R.array.reactions_icons);
        for(int i = 0; i < countrycodes.length; i++){
            countryList.add(new Country(countrynames[i], countrycodes[i], imgs.getDrawable(i)));
        }
    }

    public class Country {
        private String name;
        private String code;
        private Drawable flag;
        public Country(String name, String code, Drawable flag){
            this.name = name;
            this.code = code;
            this.flag = flag;
        }
        public String getName() {
            return name;
        }
        public Drawable getFlag() {
            return flag;
        }
        public String getCode() {
            return code;
        }
    }

    public void like(final String code){

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("reaction", code);

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("success").equalsIgnoreCase("true")){
                        Toast.makeText(ReactionActivity.this, "PUNTUACION CORRECTA", Toast.LENGTH_SHORT).show();

                        returnIntent = new Intent();
                        returnIntent.putExtra(RESULT_ID, id_publicacion);
                        returnIntent.putExtra(RESULT_POSITION, position);
                        returnIntent.putExtra(RESULT_REACTION, code);

                        imgs.recycle(); //recycle images

                        setResult(RESULT_OK, returnIntent);
                        finish();

                    }else{Toast.makeText(ReactionActivity.this, "PUNTUACION INCORRECTA", Toast.LENGTH_SHORT).show();}
                } catch (JSONException e) {
                    setResult(RESULT_CANCELED);
                    Toast.makeText(ReactionActivity.this, "ERROR AL PUNTUAR", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReactionActivity.this, "ERROR AL PUNTUAR", Toast.LENGTH_SHORT).show();
            }
        }, ROOT_URL+"api/user/activity/"+id_publicacion.toString()+"/likes");

        requestQueue.add(request);
    }

}
