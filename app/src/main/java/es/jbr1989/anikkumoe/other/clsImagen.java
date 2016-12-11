package es.jbr1989.anikkumoe.other;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by jbr1989 on 07/11/2016.
 */

public class clsImagen {

    private AppCompatActivity activity;

    public clsImagen(AppCompatActivity activity){
        this.activity=activity;
    }

    public static void camera(AppCompatActivity activity){
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if(takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, 1);
        }
    }

    public static void galeria(AppCompatActivity activity){
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setType("image/*");
        activity.startActivityForResult(intent, 2);
    }

    public static String encodeToBase64(Bitmap image)
    {
        return encodeToBase64(image, Bitmap.CompressFormat.JPEG, 100);
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
