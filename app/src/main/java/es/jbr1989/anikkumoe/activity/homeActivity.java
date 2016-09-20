package es.jbr1989.anikkumoe.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import es.jbr1989.anikkumoe.ListAdapter.NotificacionListAdapter;
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
import es.jbr1989.anikkumoe.menu.NavDrawerItem;
import es.jbr1989.anikkumoe.menu.NavDrawerListAdapter;
import es.jbr1989.anikkumoe.object.clsUsuarioSession;

/**
 * Created by jbr1989 on 05/12/2015.
 */
public class homeActivity extends AppCompatActivity implements View.OnClickListener {

    clsUsuarioSession oUsuarioSession;
    NotificacionListAdapter oListadoNotificaciones;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList navDrawerItems;
    private NavDrawerListAdapter adapter;

    public FragmentManager fragmentManager;
    public Menu optionsMenu;
    public Integer id_menu=0;

    public MyWebClient webClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        oUsuarioSession=new clsUsuarioSession(this);
        oListadoNotificaciones= new NotificacionListAdapter(this);

        mTitle = mDrawerTitle = getTitle();

        fragmentManager = getFragmentManager();
        webClient = new MyWebClient(fragmentManager);

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList();

        // agregar un nuevo item al menu deslizante
        // Notificaciones
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(Integer.parseInt("0"), -1), true, oListadoNotificaciones.getConfigNewsCount().toString()));
        // Resumen
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(Integer.parseInt("1"), -1)));
        // Novedades
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(Integer.parseInt("2"), -1)));
        // Mensajes Privados
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(Integer.parseInt("3"), -1), true, "0"));
        // Chat global
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(Integer.parseInt("4"), -1), true, "0"));
        // Nakamas conectados
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(Integer.parseInt("5"), -1), true, "0"));
        // Perfil
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(Integer.parseInt("6"), -1)));
        // Configuración
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(Integer.parseInt("7"), -1)));
        // Logout
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(Integer.parseInt("8"), -1)));


        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems);
        mDrawerList.setAdapter(adapter);


        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibili
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            //displayView(0); //Seleccionar NOTIFICACIONES
            SharedPreferences Config = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            displayView(Integer.parseInt(Config.getString("fragment_defecto", "1")));
        }

    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position,long id) {
            // display view for selected nav drawer item
            id_menu=0;
            displayView(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        if (id_menu!=0) getMenuInflater().inflate(id_menu, menu);
        return true;
    }

 /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Called when invalidateOptionsMenu() is triggered

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
*/
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        Bundle arguments = new Bundle();

        switch (position) {
            case 0:
                fragment = new notificacionesFragment();
                break;
            case 1:
                arguments.putString("tipo", "resumen");
                fragment = ListadoPublicacionesFragment.newInstance(arguments);
                break;
            case 2:
                arguments.putString("tipo", "explorar");
                fragment = ListadoPublicacionesFragment.newInstance(arguments);
                break;
            case 3:
                fragment= new UltimosMensajesFragment();
                break;
            case 4:
                fragment = new chatFragment();
                break;
            case 5:
                fragment = new nakamasFragment();
                break;
            case 6:
                arguments.putString("usuario", oUsuarioSession.getUsuario());
                fragment = perfilFragment.newInstance(arguments);
                break;
            case 7:
                fragment = new ConfigFragment();
                break;
            case 8:
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
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("Ramiro", "MainActivity - Error cuando se creo el fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onClick (View v){
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0 ){
            fragmentManager.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    public void switchContent(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
    }

    //MENU

    public void setRefreshActionButtonState(final boolean refreshing) {
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
    }

    //REACTIONS

}