package es.jbr1989.anikkumoe.http;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.fragment.ImgViewFragment;
import es.jbr1989.anikkumoe.fragment.PublicacionesFragment;
import es.jbr1989.anikkumoe.fragment.perfil3Fragment;

/**
 * Created by jbr1989 on 13/09/2016.
 */
public class MyWebClient extends WebViewClient {

    private FragmentManager fragmentManager;
    private Context context;

    public MyWebClient(Context context, FragmentManager fragmentManager){
        this.context = context;
        this.fragmentManager=fragmentManager;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if(url.contains("hashtag:")){
            String hashtag = url.substring(url.lastIndexOf(":")+1);
            cargar_publicaciones("hashtag",hashtag);

            return false;
        }else if (url.contains("user:")) {
            String user = url.substring(url.lastIndexOf(":") + 1);
            cargar_perfil(user);

            return false;
        }else if (url.contains("img:")){
            String img = url.substring(url.indexOf(":") + 1);
            cargar_img(img);

            return true;
        }else{
            cargar_url(url);
            return true;
        }
    }

    public void cargar_publicaciones(String tipo, String valor){
        Bundle arguments = new Bundle();
        arguments.putString("tipo","hashtag");
        arguments.putString("valor", valor);

        Fragment fragment = PublicacionesFragment.newInstance(arguments);
        fragmentManager.beginTransaction().add(R.id.content, fragment).addToBackStack(null).commit();
    }

    public void cargar_perfil(String usuario){
        Bundle arguments = new Bundle();
        arguments.putString("usuario", usuario);

        Fragment fragment = perfil3Fragment.newInstance(arguments);
        fragmentManager.beginTransaction().add(R.id.content, fragment).addToBackStack(null).commit();
    }

    public void cargar_url(String url){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void cargar_img(String url){
        Bundle arguments = new Bundle();
        arguments.putString("imgUrl", url);

        Fragment fragment = ImgViewFragment.newInstance(arguments);
        fragmentManager.beginTransaction().add(R.id.content, fragment).addToBackStack(null).commit();
    }


}
