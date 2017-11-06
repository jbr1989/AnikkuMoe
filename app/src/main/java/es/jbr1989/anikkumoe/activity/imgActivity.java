package es.jbr1989.anikkumoe.activity;

import android.app.Fragment;
import android.app.ListActivity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.jbr1989.anikkumoe.ListAdapter.OptionsListAdapter;
import es.jbr1989.anikkumoe.ListAdapter.ReactionListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.fragment.ImgViewFragment;
import es.jbr1989.anikkumoe.fragment.SerieFragment;
import es.jbr1989.anikkumoe.fragment.chat2PrivadoFragment;
import es.jbr1989.anikkumoe.object.clsNakama;

import static es.jbr1989.anikkumoe.R.string.url;
import static es.jbr1989.anikkumoe.activity.ReactionActivity.RESULT_ID;
import static es.jbr1989.anikkumoe.activity.ReactionActivity.RESULT_REACTION;

/**
 * Created by jbr1989 on 27/09/2017.
 */

public class imgActivity extends ListActivity implements AdapterView.OnItemClickListener {


    private List<ImgOpcion> opcionesList;
    private String[] opcionesNames;
    private String[] opcionesValues;

    private Bundle extras;
    private String imgUrl;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getBaseContext();

        Intent i= getIntent();
        extras = i.getExtras();
        if(extras != null) imgUrl = extras.getString("imgUrl");

        cargarOpciones();
        ArrayAdapter<ImgOpcion> adapter = new OptionsListAdapter(this, opcionesList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String value = opcionesValues[position];

        if (value.equalsIgnoreCase("wallpaper")){
            new SetWallpaperTask().execute(imgUrl);
        }else{

            Bundle arguments = new Bundle();
            arguments.putString("imgUrl", imgUrl);

            Fragment fragment = ImgViewFragment.newInstance(arguments);

            if (context instanceof homeActivity) {
                homeActivity feeds = (homeActivity) context;
                feeds.switchContent(fragment);
            }
        }
        finish();
    }


    private void cargarOpciones(){
        opcionesList = new ArrayList<ImgOpcion>();
        opcionesNames = getResources().getStringArray(R.array.imgview_actions_names);
        opcionesValues = getResources().getStringArray(R.array.imgview_actions_values);

        for(int i = 0; i < opcionesNames.length; i++){
            opcionesList.add(new ImgOpcion(opcionesNames[i], opcionesValues[i]));
        }
    }

    public class ImgOpcion {
        private String name;
        private String code;
        public ImgOpcion(String name, String code){
            this.name = name;
            this.code = code;
        }
        public String getName() {
            return name;
        }
        public String getCode() {
            return code;
        }
    }


    public class SetWallpaperTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap result= null;
            String imgUrl = params[0];
            try {
                result = Picasso.with(getApplicationContext())
                        .load(imgUrl)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(result);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute (Bitmap result) {
            super.onPostExecute(result);

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
            try {
                wallpaperManager.setBitmap(result);
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Set wallpaper successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(getApplicationContext(), "Set wallpaper successfully", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();

            //progressDialog = new ProgressDialog(MainActivity.this);
            //progressDialog.setMessage("Please wait...");
            //progressDialog.setCancelable(false);
            //progressDialog.show();
        }
    }
}
