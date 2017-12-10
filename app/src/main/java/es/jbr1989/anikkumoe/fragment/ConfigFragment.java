package es.jbr1989.anikkumoe.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.inscription.ChangeLogDialog;
import es.jbr1989.anikkumoe.inscription.CreditsDialog;

/**
 * Created by jbr1989 on 07/12/2015.
 */
public class ConfigFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private homeActivity home;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.configuracion);

        Preference changelog = (Preference) findPreference("changelog");
        changelog.setOnPreferenceClickListener(this);

        Preference about = (Preference) findPreference("about");
        about.setOnPreferenceClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        rootView.setBackgroundColor(getResources().getColor(android.R.color.white));

        home = (homeActivity) rootView.getContext();
        //home.setTitle(R.string.FragmentConfig);

        //home.setSupportActionBar((Toolbar) home.findViewById(R.id.toolbar));

        //home.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return rootView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {

        switch (key){
            case "notificacion_intervalo":
            case "notificacion_activo":
                home.notificationCron();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        boolean ok = false;

        switch(preference.getKey()){
            case "changelog":
                    final ChangeLogDialog changeLogDialog = new ChangeLogDialog(this.getActivity());
                    changeLogDialog.setStyle("h1 { margin-left: 10px; font-size: 12pt; color: #006b9a; margin-bottom: 0px;}"
                            + "li { margin-left: 0px; font-size: 12pt; padding-top: 10px; }"
                            + "ul { padding-left: 30px; margin-top: 0px; }"
                            + ".summary { margin-left: 10px; font-size: 10pt; color: #006b9a; margin-top: 5px; display: block; clear: left; }"
                            + ".date { margin-left: 10px; font-size: 9pt; color: #006b9a; margin-top: 5px; display: block; }");
                    changeLogDialog.show();
                break;

            case "about":
                //Launch change log dialog
                final CreditsDialog cd = new CreditsDialog(this.getActivity());
                cd.setStyle(""

                        + "body { font-size: 9pt; text-align: center; }"
                        + "h1 { margin-top: 20px; margin-bottom: 15px; margin-left: 0px; font-size: 1.7EM; text-align: center; color: #006b9a; }"
                        + "h2 { margin-top: 15px; margin-bottom: 5px; padding-left: 0px; margin-left: 0px; font-size: 1EM; }"
                        + "li { margin-left: 0px; font-size: 1EM; }"
                        + "ul { margin-top: 0px;   margin-bottom: 5px; padding-left: 0px; list-style-type: none; }"
                        + "a { color: #006b9a; }"
                        + "</style>");
                cd.show();
                break;

        }

        return false;
    }
}
