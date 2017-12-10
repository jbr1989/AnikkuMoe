package es.jbr1989.anikkumoe.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

import es.jbr1989.anikkumoe.R;
import me.relex.photodraweeview.OnPhotoTapListener;
import me.relex.photodraweeview.OnViewTapListener;
import me.relex.photodraweeview.PhotoDraweeView;


/**
 * Created by jbr1989 on 27/09/2017.
 */

public class ImgViewFragment extends Fragment implements View.OnClickListener {

    private static final String BUNDLE_STATE = "ImageViewState";

    private String imgUrl;

    private View rootView;
    private Context context;

    private Bitmap result;

    public ProgressDialog progressDialog;
    private PhotoDraweeView mPhotoDraweeView;

    //region CONSTRUCTOR

    public ImgViewFragment(){}

    public static ImgViewFragment newInstance(Bundle arguments){
        ImgViewFragment f = new ImgViewFragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }

    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments()!=null) imgUrl= getArguments().getString("imgUrl");

        rootView = inflater.inflate(R.layout.fragment_imgview, container, false);
        context = rootView.getContext();

        //new SetImgView().execute(imgUrl);

        mPhotoDraweeView = (PhotoDraweeView) rootView.findViewById(R.id.photo_drawee_view);
        mPhotoDraweeView.setPhotoUri(Uri.parse(imgUrl));
        mPhotoDraweeView.setDrawingCacheEnabled(true);

        mPhotoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override public void onPhotoTap(View view, float x, float y) {
                Animatable animatable = mPhotoDraweeView.getController().getAnimatable();
                if (animatable != null) {
                    if (!animatable.isRunning()){ animatable.start();} else {animatable.stop();};
                }

                //Toast.makeText(view.getContext(), "onPhotoTap :  x =  " + x + ";" + " y = " + y, Toast.LENGTH_SHORT).show();
            }
        });
        mPhotoDraweeView.setOnViewTapListener(new OnViewTapListener() {
            @Override public void onViewTap(View view, float x, float y) {
                //Toast.makeText(view.getContext(), "onViewTap", Toast.LENGTH_SHORT).show();
            }
        });

        mPhotoDraweeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                //Toast.makeText(v.getContext(), "onLongClick", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        //imgView.setOnClickListener(this);

        rootView.findViewById(R.id.btnWallpaper).setOnClickListener(this);
        rootView.findViewById(R.id.rotate).setOnClickListener(this);

        return rootView;
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imageView:
                RelativeLayout toolbarBottom = (RelativeLayout)rootView.findViewById(R.id.toolbar_bottom);

                /*TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
                animate.setDuration(500);
                animate.setFillAfter(true);
                toolbarBottom.startAnimation(animate);*/

                toolbarBottom.setVisibility((toolbarBottom.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
                break;
            case R.id.btnWallpaper:
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                try {
                    //mPhotoDraweeView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    //mPhotoDraweeView.layout(0, 0, mPhotoDraweeView.getMeasuredWidth(), mPhotoDraweeView.getMeasuredHeight());

                    mPhotoDraweeView.buildDrawingCache(true);
                    Bitmap bitmap = Bitmap.createBitmap(mPhotoDraweeView.getDrawingCache()); //,0,0,MyDevice.getInstance(),mPhotoDraweeView.getHeight());

                    DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    // get the height and width of screen
                    int height = metrics.heightPixels;
                    int width = metrics.widthPixels;

                    Bitmap fondo = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    wallpaperManager.setWallpaperOffsetSteps(1, 1);
                    wallpaperManager.setBitmap(fondo);
                    wallpaperManager.suggestDesiredDimensions(width, height);
                    Toast.makeText(context, getResources().getString(R.string.wallpaper_ok), Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Toast.makeText(context, getResources().getString(R.string.wallpaper_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rotate:
                mPhotoDraweeView.setRotation((mPhotoDraweeView.getRotation() + 90) % 360);
                mPhotoDraweeView.update(mPhotoDraweeView.getWidth(),mPhotoDraweeView.getHeight());
                break;
        }
    }
}
