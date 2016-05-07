package es.jbr1989.anikkumoe.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import es.jbr1989.anikkumoe.AppController;

/**
 * Created by Jaume on 04/12/2015.
 */
public class CustomRequest extends Request<JSONObject>{

    private static final String ROOT_URL = AppController.getInstance().getApiOld();
    private RequestQueue requestQueue;

    private Listener<JSONObject> listener;
    private Listener<JSONArray> listener2;
    private Map<String, String> headers;
    private Map<String, String> params;

    public CustomRequest(RequestQueue requestQueue, Map<String, String> headers, Map<String, String> params,Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, ROOT_URL, errorListener);
        this.requestQueue = requestQueue;

        this.listener = reponseListener;
        if (!headers.isEmpty()) this.headers=headers;
        if (!params.isEmpty()) this.params = params;
    }

    public CustomRequest(RequestQueue requestQueue, int method, Map<String, String> headers, Map<String, String> params,Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, ROOT_URL, errorListener);
        this.requestQueue = requestQueue;

        this.listener = reponseListener;
        if (!headers.isEmpty()) this.headers=headers;
        if (!params.isEmpty()) this.params = params;
    }

    public CustomRequest(RequestQueue requestQueue, int method, Map<String, String> headers, Map<String, String> params,Listener<JSONObject> reponseListener, ErrorListener errorListener, String URL) {
        super(method, URL, errorListener);
        this.requestQueue = requestQueue;

        this.listener = reponseListener;
        if (!headers.isEmpty()) this.headers=headers;
        if (!params.isEmpty()) this.params = params;
    }

    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }

}
