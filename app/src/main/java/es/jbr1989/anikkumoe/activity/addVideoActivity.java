package es.jbr1989.anikkumoe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import es.jbr1989.anikkumoe.R;

/**
 * Created by jbr1989 on 16/06/2016.
 */


public class addVideoActivity extends Activity {

    public TextView txtVideo;
    public Button btnAddVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_video);

        txtVideo = (EditText) findViewById(R.id.txtVideo);
        btnAddVideo= (Button) findViewById(R.id.btnAddVideo);

        btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video = txtVideo.getText().toString();
                if (TextUtils.isEmpty(video)) {
                    Toast.makeText(getBaseContext(), "Enlace vacío", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Pattern p1 = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?(?:youtu\\.be\\/|youtube\\.com\\/(?:embed\\/|v\\/|watch\\?v=|watch\\?.+&v=))((\\w|-){11})(?:\\S+)?$");
                    if(!p1.matcher(video).find()){
                        Toast.makeText(getBaseContext(), "No es un vídeo de Youtube", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("link",video);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            }
        });

    }



}
