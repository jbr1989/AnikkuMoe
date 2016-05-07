package es.jbr1989.anikkumoe.menu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.jbr1989.anikkumoe.R;


/**
 * Created by jbr1989 on 05/12/2015.
 */
public class Seccion extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.seccion, container, false);

        return rootView;
    }
}