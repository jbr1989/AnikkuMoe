package es.jbr1989.anikkumoe.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.jbr1989.anikkumoe.AppController;
import es.jbr1989.anikkumoe.ListAdapter.Chat2ListAdapter;
import es.jbr1989.anikkumoe.R;
import es.jbr1989.anikkumoe.activity.homeActivity;
import es.jbr1989.anikkumoe.http.CustomRequest;

/**
 * Created by jbr1989 on 09/12/2015.
 */
public class chat2Fragment extends Fragment {

    private static final String API_OLD_URL = AppController.getInstance().getApiOld();

    private SuperRecyclerView mRecycler;
    private Chat2ListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    final Integer id_chat=1;

    private homeActivity home;
    @Bind(R.id.navigation) LinearLayout mNavigation;
    @Bind(R.id.avatar) SimpleDraweeView mAvatar;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.txtMensaje) TextView messageET;
    @Bind(R.id.btnComentario) ImageButton sendBtn;

    //region CONSTRUCTOR

    public chat2Fragment(){}
    public static chat2Fragment newInstance(Bundle arguments){
        chat2Fragment f = new chat2Fragment();
        if(arguments != null) f.setArguments(arguments);
        return f;
    }

    //endregion

    //El Fragment va a cargar su layout, el cual debemos especificar
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, rootView);
        home = (homeActivity) rootView.getContext();
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.toggleDrawer();
            }
        });
        mAvatar.setVisibility(View.GONE);
        mTitle.setText(R.string.FragmentChatGlobal);

        mRecycler = (SuperRecyclerView) rootView.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());

        mAdapter = new Chat2ListAdapter(rootView.getContext(), id_chat, mRecycler);

        mRecycler.setLayoutManager(mLayoutManager);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    Toast.makeText(getActivity(), "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(messageText);
            }
        });

        //if (intervalo!=0) timerHandler.postDelayed(onEverySecond, intervalo*1000);

        return rootView;
    }

    //La vista de layout ha sido creada y ya está disponible
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtener_chats();
    }



    // region CHATS

    public void obtener_chats(){
        mAdapter.clearChats();
        mAdapter.getChats(1);
    }


    public void sendMessage(String messageText){

        sendBtn.setEnabled(false);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Disposition", "form-data");
        headers.put("Authorization", "Bearer " + home.oUsuarioSession.getToken());

        Map<String, String> params = new HashMap<String, String>();
        params.put("mdl", "plugin");
        params.put("acc", "chatbox_message");
        params.put("id", "1");
        params.put("texto", messageText);

        home.request = new CustomRequest(home.requestQueue, Request.Method.POST, headers, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mAdapter.setNewChats(id_chat, response);
                clearMessage();
                sendBtn.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                sendBtn.setEnabled(true);
            }
        }, API_OLD_URL);

        home.requestQueue.add(home.request);
    }

    public void clearMessage(){
        messageET.setText("");
    }


    // end region

    //El fragment se ha adjuntado al Activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    //El Fragment ha sido creado
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //La vista ha sido creada y cualquier configuración guardada está cargada
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    //El Activity que contiene el Fragment ha terminado su creación
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //El Fragment ha sido quitado de su Activity y ya no está disponible
    @Override
    public void onDetach() {
        //mCallback = null;
        super.onDetach();
    }


    @Override
    public void onResume() {
        super.onResume();
        mAdapter.startHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.stopHandler();
    }


}
