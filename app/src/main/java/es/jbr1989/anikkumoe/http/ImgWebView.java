package es.jbr1989.anikkumoe.http;

import android.content.Context;
import android.os.Environment;
import android.webkit.WebView;

import java.io.File;

import es.jbr1989.anikkumoe.AppController;

/**
 * Created by jbr1989 on 10/03/2016.
 */
public class ImgWebView extends WebView {

    private static final String ROOT_URL = AppController.getInstance().getUrl();

    public ImgWebView(Context context) {
        super(context);
    }

    public void LoadImg(String img){

        String root = Environment.getExternalStorageDirectory().toString();
        File DirRoot = new File(root);
        if (!DirRoot.exists()) DirRoot.mkdirs();

        File ImgRoot = new File(DirRoot, img);
        String html="";

        if (ImgRoot.exists()){
            html= "<!DOCTYPE html><html><body style = \"text-align:center;margin:0;\"><img src=\"file://" + ImgRoot.getAbsolutePath()+"\" width=\"100%\"></body></html>";
        }else{
            html= "<!DOCTYPE html><html><body style = \"text-align:center;margin:0;\"><img src=\""+ ROOT_URL+"/static-img/" + img+"\" width=\"100%\"></body></html>";
        }

        super.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

    }
}
