package es.jbr1989.anikkumoe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.vansuita.library.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsImagen;

/**
 * Created by jbr1989 on 01/06/2016.
 */
public class NuevaPublicacionActivity extends AppCompatActivity implements IPickResult {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;

    public EditText txtPublicacion;
    public Button btnAddPublicacion;
    public CheckBox chkSpoiler;
    public ImageButton btnFoto, btnGaleria, btnVideo, btnDelVideo;
    public ImageView imgNewImagen;
    public TextView lblNewVideo;

    public RelativeLayout lytMultimedia;

    private MagicalCamera magicalCamera;
    private int RESIZE_PHOTO_PIXELS_PERCENTAGE=3000;

    private ProgressDialog loading;

    public Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_publicacion);
        setTitle("Nueva publicación");

        oUsuarioSession = new clsUsuarioSession(getBaseContext());
        requestQueue = Volley.newRequestQueue(getBaseContext());

        lytMultimedia = (RelativeLayout) findViewById(R.id.lytMultimedia);

        txtPublicacion = (EditText) findViewById(R.id.txtPublicacion);
        btnAddPublicacion = (Button) findViewById(R.id.btnAddPublicacion);
        chkSpoiler = (CheckBox)  findViewById(R.id.chkSpoiler);
        btnFoto = (ImageButton) findViewById(R.id.btnFoto);
        btnGaleria = (ImageButton) findViewById(R.id.btnGaleria);
        btnVideo = (ImageButton) findViewById(R.id.btnVideo);
        imgNewImagen = (ImageView) findViewById(R.id.imgNewPubImagen);
        lblNewVideo = (TextView) findViewById(R.id.lblNewPubVideo);
        btnDelVideo = (ImageButton) findViewById(R.id.btnDelPubVideo);

        imageBitmap = null;

        magicalCamera = new MagicalCamera(this, RESIZE_PHOTO_PIXELS_PERCENTAGE);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clsImagen.camera(NuevaPublicacionActivity.this);
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clsImagen.galeria(NuevaPublicacionActivity.this);
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(v.getContext(), addVideoActivity.class);
                //intent.putExtra("id_publicacion", oPublicacion.feed.getId());
                //intent.putExtra("usuario", (!oPublicacion.getType().equalsIgnoreCase("REP") ? oPublicacion.user.getUsuario() : oPublicacion.user_original.getUsuario()));
                startActivityForResult(intent, 2);
            }
        });

        btnDelVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lblNewVideo.setText("");
                btnDelVideo.setVisibility(View.GONE);
                lytMultimedia.setVisibility(View.GONE);
            }
        });

        btnAddPublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String publicacionText = txtPublicacion.getText().toString();
                if (TextUtils.isEmpty(publicacionText)) {
                    Toast.makeText(getBaseContext(), "Publicación vacía", Toast.LENGTH_SHORT).show();
                    return;
                }
                addPublicacion(publicacionText);
                //addPublicacion2(publicacionText);
            }
        });
    }

    @Override
    public void onPickImageResult(Bitmap bitmap) {
        //TODO: use bitmap.
        imgNewImagen.setImageBitmap(bitmap);
        lytMultimedia.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imgNewImagen.setImageBitmap(imageBitmap);
            lytMultimedia.setVisibility(View.VISIBLE);
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri imageUri = (Uri) data.getData();

            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgNewImagen.setImageBitmap(imageBitmap);
                lytMultimedia.setVisibility(View.VISIBLE);
            }catch (Exception e) {}
        }

/*
        if (data!=null) {
            if (requestCode != 2) {
                //call this method ever
                //magicalCamera.resultPhoto(requestCode, resultCode, data,true);

                imgNewImagen.setImageBitmap(magicalCamera.getMyPhoto());
                lytMultimedia.setVisibility(View.VISIBLE);

                //byte[] Bites = MagicalCamera.bitmapToBytes(magicalCamera.getMyPhoto(),MagicalCamera.PNG);
                //String base64 = MagicalCamera.bytesToStringBase64(Bites);

                //base64="";
                /*
                if(magicalCamera.savePhotoInMemoryDevice(magicalCamera.getMyPhoto(),"myPhotoName", MagicalCamera.JPEG, true)){
                    Toast.makeText(this, "The photo is save in device, please check this", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Sorry your photo dont write in devide, please contact with fabian7593@gmail and say this error", Toast.LENGTH_SHORT).show();
                }


            } else if (data.hasExtra("link")) {
                String video = data.getStringExtra("link");
                if (!video.isEmpty()) {
                    lblNewVideo.setText(video);
                    btnDelVideo.setVisibility(View.VISIBLE);
                    lytMultimedia.setVisibility(View.VISIBLE);
                }
            }
        }
        */
    }

    public void addPublicacion(String publicacionText){

        btnAddPublicacion.setEnabled(false);

        Integer tipo =0;
        //loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        String video = lblNewVideo.getText().toString();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        if(imageBitmap!=null){

            //loading.setMessage("Convirtiendo a bytes");
            //byte[] bites = MagicalCamera.bitmapToBytes(magicalCamera.getMyPhoto(),MagicalCamera.JPEG);
            //loading.setMessage("Convirtiendo a base64");
            String base64 = clsImagen.encodeToBase64(imageBitmap); //MagicalCamera.bytesToStringBase64(bites);

            //Converting Bitmap to String
            //String image = getStringImage(magicalCamera.getMyPhoto());

            //Adding parameters
            params.put("filename","prova.jpg");
            params.put("file", base64);
            //loading.setMessage("Enviando...");

            tipo=2;
        }

        if (!video.isEmpty()){ tipo=3; params.put("video", video);}

        params.put("texto", publicacionText);
        params.put("spoiler", String.valueOf(chkSpoiler.isChecked()));
        params.put("tipo", tipo.toString());
        //params.put("file", getStringImage(magicalCamera.getMyPhoto()));
        //params.put("tags", "1");
        //params.put("usus", "1");

        //params.put("id_serie", "1");

        request = new CustomRequest(requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //loading.dismiss();
                    if (response.getBoolean("success")) Toast.makeText(getBaseContext(), "PUBLICADO", Toast.LENGTH_SHORT).show();
                    finish();
                }catch (JSONException ex){ex.printStackTrace();}

                btnAddPublicacion.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
                btnAddPublicacion.setEnabled(true);
            }
        }, ROOT_URL+"api/user/activity");

        requestQueue.add(request);
    }

    private void addPublicacion2(final String publicacionText){
        //Showing the progress dialog
        loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ROOT_URL+"api/user/activity",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getBaseContext(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getBaseContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //Creating parameters
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Content-Disposition", "form-data");
                headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

                //returning parameters
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                if(magicalCamera.getMyPhoto()!=null){

                    loading.setMessage("Convirtiendo a bytes");
                    byte[] bites = MagicalCamera.bitmapToBytes(magicalCamera.getMyPhoto(),MagicalCamera.PNG);
                    loading.setMessage("Convirtiendo a base64");
                    String base64 = MagicalCamera.bytesToStringBase64(bites);
                    loading.setMessage("Enviando...");
                    //Converting Bitmap to String
                    //String image = getStringImage(magicalCamera.getMyPhoto());

                    //Adding parameters
                    params.put("file", "data:image/jpg;base64," + base64);
                }


                //Getting Image Name
                //String name = editTextName.getText().toString().trim();

                params.put("texto", publicacionText);
                params.put("spoiler", String.valueOf(chkSpoiler.isChecked()));
                params.put("tipo", "1");
                //params.put("tags", "1");
                //params.put("usus", "1");
                //params.put("video", "1");
                //params.put("id_serie", "1");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        encodedImage = "data:image/jpg;base64," + encodedImage;

        return encodedImage;
    }

}
