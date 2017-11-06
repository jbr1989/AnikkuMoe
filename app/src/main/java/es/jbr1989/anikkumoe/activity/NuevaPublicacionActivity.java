package es.jbr1989.anikkumoe.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
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
import com.frosquivel.magicalcamera.MagicalPermissions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.MetadataChangeSet;
import com.vansuita.library.IPickResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.http.CustomRequest;
import es.jbr1989.anikkumoe.http.CustomRequestImg;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;
import es.jbr1989.anikkumoe.other.clsImagen;

/**
 * Created by jbr1989 on 01/06/2016.
 */
public class NuevaPublicacionActivity extends AppCompatActivity
        implements IPickResult, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;



    private static final String ROOT_URL = AppController.getInstance().getUrl();

    private clsUsuarioSession oUsuarioSession;

    public RequestQueue requestQueue;
    public CustomRequest request;
    public CustomRequestImg requestImg;

    public File file;

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

    private Context ctx;

    private Bitmap mBitmapToSave;

    String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_publicacion);
        setTitle("Nueva publicación");

        ctx=this;

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

        mBitmapToSave = null;


        MagicalPermissions magicalPermissions = new MagicalPermissions(this, permissions);

        magicalCamera = new MagicalCamera(this,RESIZE_PHOTO_PIXELS_PERCENTAGE, magicalPermissions);
/*
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magicalCamera.takePhoto();
                //clsImagen.camera(NuevaPublicacionActivity.this);
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magicalCamera.selectedPicture("my_header_name");
                //clsImagen.galeria(NuevaPublicacionActivity.this);
            }
        });
        */
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

       //getCompartir();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case 0:
            case 1:
                //CALL THIS METHOD EVER
                magicalCamera.resultPhoto(requestCode, resultCode, data);

                //this is for rotate picture in this method
                //magicalCamera.resultPhoto(requestCode, resultCode, data, MagicalCamera.ORIENTATION_ROTATE_180);

                //with this form you obtain the bitmap (in this example set this bitmap in image view)
                imgNewImagen.setImageBitmap(magicalCamera.getPhoto());
                lytMultimedia.setVisibility(View.VISIBLE);

                //if you need save your bitmap in device use this method and return the path if you need this
                //You need to send, the bitmap picture, the photo name, the directory name, the picture type, and autoincrement photo name if           //you need this send true, else you have the posibility or realize your standard name for your pictures.
                String path = magicalCamera.savePhotoInMemoryDevice(magicalCamera.getPhoto(),"myPhotoName","myDirectoryName", MagicalCamera.JPEG, true);

                if(path != null){
                    Toast.makeText(NuevaPublicacionActivity.this, "The photo is save in device, please check this path: " + path, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(NuevaPublicacionActivity.this, "Sorry your photo dont write in devide, please contact with fabian7593@gmail and say this error", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onPickImageResult(Bitmap bitmap) {
        //TODO: use bitmap.
        imgNewImagen.setImageBitmap(bitmap);
        lytMultimedia.setVisibility(View.VISIBLE);
    }

    public void addPublicacion(String publicacionText){

        btnAddPublicacion.setEnabled(false);

        Integer tipo =1;
        //loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        String video = lblNewVideo.getText().toString();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        if(mBitmapToSave!=null){

            //loading.setMessage("Convirtiendo a bytes");
            //byte[] bites = MagicalCamera.bitmapToBytes(magicalCamera.getMyPhoto(),MagicalCamera.JPEG);
            //loading.setMessage("Convirtiendo a base64");
            String base64 = clsImagen.encodeToBase64(mBitmapToSave); //MagicalCamera.bytesToStringBase64(bites);

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

        //requestQueue.add(request);

        saveFileToDrive();
    }
/*
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

    private void addPublicacion3(final String publicacionText){

        btnAddPublicacion.setEnabled(false);

        Integer tipo =1;
        //loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        String video = lblNewVideo.getText().toString();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();

        if (!video.isEmpty()){ tipo=3; params.put("video", video);}

        params.put("texto", publicacionText);
        params.put("spoiler", String.valueOf(chkSpoiler.isChecked()));
        params.put("tipo", tipo.toString());
        //params.put("file", getStringImage(magicalCamera.getMyPhoto()));
        params.put("tags", "");
        params.put("usus", "");

        //params.put("id_serie", "1");

        requestImg = new CustomRequestImg(requestQueue, headers, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //try {
                //loading.dismiss();
                //if (response.getBoolean("success")) Toast.makeText(getBaseContext(), "PUBLICADO", Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(), s , Toast.LENGTH_LONG).show();
                finish();
                //}catch (JSONException ex){ex.printStackTrace();}

                btnAddPublicacion.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
                btnAddPublicacion.setEnabled(true);
            }
        }, file, ROOT_URL+"api/user/activity");

        requestQueue.add(requestImg);


    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        encodedImage = "data:image/jpg;base64," + encodedImage;

        return encodedImage;
    }

    public byte[] getByteImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
*/
    // COMPARTIR
/*
    public void getCompartir(){
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            txtPublicacion.setText(sharedText);
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }
*/
    // DRIVE

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            //showErrorDialog(result.getErrorCode());
            Toast.makeText(getApplicationContext(), result.getErrorCode(), Toast.LENGTH_LONG).show();

            mResolvingError = true;
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        //Log.i(TAG, "API client connected.");

    }


    @Override
    public void onConnectionSuspended(int cause) {
        Toast.makeText(getApplicationContext(), "GoogleApiClient connection suspended", Toast.LENGTH_LONG).show();
    }

    private void saveFileToDrive() {

        final Bitmap image = magicalCamera.getPhoto();
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {

                        if (!result.getStatus().isSuccess()) {
                            Log.i("ERROR", "Failed to create new contents.");
                            return;
                        }

                        OutputStream outputStream = result.getDriveContents().getOutputStream();
                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.i("ERROR", "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle("Android Photo.png").build();
                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(intentSender, 1, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("ERROR", "Failed to launch file chooser.");
                        }
                    }
                });
    }

}
