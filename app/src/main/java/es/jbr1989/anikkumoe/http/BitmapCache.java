package es.jbr1989.anikkumoe.http;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Jaume on 04/12/2015.
 */
public class BitmapCache extends LruCache implements ImageLoader.ImageCache {

    public BitmapCache(int maxSize){
        super(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url){
        return (Bitmap) get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap){
        put(url,bitmap);
    }

}
