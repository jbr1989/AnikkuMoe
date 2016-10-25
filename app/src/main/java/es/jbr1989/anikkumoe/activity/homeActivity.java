package es.jbr1989.anikkumoe.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.flyco.systembar.SystemBarHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.NotifyService;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.fragment.ConfigFragment;
import es.jbr1989.anikkumoe.fragment.ListadoPublicacionesFragment;
import es.jbr1989.anikkumoe.fragment.UltimosMensajesFragment;
import es.jbr1989.anikkumoe.fragment.chatFragment;
import es.jbr1989.anikkumoe.fragment.nakamasFragment;
import es.jbr1989.anikkumoe.fragment.notificacionesFragment;
import es.jbr1989.anikkumoe.fragment.perfilFragment;
import es.jbr1989.anikkumoe.http.MyWebClient;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 05/12/2015.
 */
public class homeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String ROOT_URL = AppController.getInstance().getUrl();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Bind(R.id.nav_view) NavigationView mNavigationView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    public FragmentManager fragmentManager;
    public MyWebClient webClient;

    clsUsuarioSession oUsuarioSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        ButterKnife.bind(this);

        oUsuarioSession=new clsUsuarioSession(this);
        fragmentManager = getFragmentManager();
        webClient = new MyWebClient(fragmentManager);

        mNavigationView.setNavigationItemSelectedListener(this);

        View header = mNavigationView.getHeaderView(0);
        NetworkImageView mAvatar = (NetworkImageView) header.findViewById(R.id.menu_avatar);
        TextView mNombre = (TextView) header.findViewById(R.id.menu_nombre);
        TextView mUser = (TextView) header.findViewById(R.id.menu_user);

        mAvatar.setImageUrl(ROOT_URL+"/static-img/" + oUsuarioSession.getAvatar(), imageLoader);
        mNombre.setText(oUsuarioSession.getNombre());
        mUser.setText("@"+oUsuarioSession.getUsuario());

        int color = getResources().getColor(R.color.colorPrimary);
        //方法1:删除DrawerLayout所在布局中所有fitsSystemWindows属性,尤其是DrawerLayout的fitsSystemWindows属性
        SystemBarHelper.tintStatusBarForDrawer(this, mDrawerLayout, color);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            //displayView(0); //Seleccionar NOTIFICACIONES
            SharedPreferences Config = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            displayView(mNavigationView.getMenu().getItem(Integer.parseInt(Config.getString("fragment_defecto", "1"))).getItemId());
        }

    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displayView(item.getItemId());
        return true;
    }

    public void displayView(Integer id){

        Fragment fragment = null;
        Bundle arguments = new Bundle();
        String title = "";

        switch (id) {
            case R.id.nav_FragmentNotificacion:
                fragment = new notificacionesFragment();
                title=getResources().getString(R.string.FragmentNotificacion);
                break;
            case R.id.nav_FragmentResumen:
                arguments.putString("tipo", "resumen");
                fragment = ListadoPublicacionesFragment.newInstance(arguments);
                title=getResources().getString(R.string.FragmentResumen);
                break;
            case R.id.nav_FragmentExplorar:
                arguments.putString("tipo", "explorar");
                fragment = ListadoPublicacionesFragment.newInstance(arguments);
                title=getResources().getString(R.string.FragmentExplorar);
                break;
            case R.id.nav_FragmentMensajes:
                fragment= new UltimosMensajesFragment();
                title=getResources().getString(R.string.FragmentMensajes);
                break;
            case R.id.nav_FragmentChatGlobal:
                fragment = new chatFragment();
                title=getResources().getString(R.string.FragmentChatGlobal);
                break;
            case R.id.nav_FragmentNakamas:
                fragment = new nakamasFragment();
                title=getResources().getString(R.string.FragmentNakamas);
                break;
            case R.id.nav_FragmentPerfil:
                arguments.putString("usuario", oUsuarioSession.getUsuario());
                fragment = perfilFragment.newInstance(arguments);
                title=getResources().getString(R.string.FragmentPerfil);
                break;
            case R.id.nav_FragmentConfig:
                fragment = new ConfigFragment();
                title=getResources().getString(R.string.FragmentConfig);
                break;
            case R.id.nav_FragmentLogout:
                title=getResources().getString(R.string.FragmentLogout);

                oUsuarioSession.delUsuario();
                oUsuarioSession.setLogin(false);
                stopService(new Intent(this, NotifyService.class));

                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);

                finish();
                break;

            default:
                break;
        }

        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();

            // update selected item and title, then close the drawer
            setTitle(title);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // error in creating fragment
            Log.e("Ramiro", "MainActivity - Error cuando se creo el fragment");
        }

    }

    public void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } if (fragmentManager.getBackStackEntryCount() > 0 ){
            fragmentManager.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    public void switchContent(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
    }

    //MENU

    public void setRefreshActionButtonState(final boolean refreshing) {
        /*
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu.findItem(R.id.airport_menuRefresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
        */
    }

}