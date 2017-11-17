package es.jbr1989.anikkumoe.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by jbr1989 on 02/04/2017.
 */

public class CustomRequestImg extends Request<String> {

    private RequestQueue requestQueue;

    private MultipartEntity entity = new MultipartEntity();

    private static final String FILE_PART_NAME = "image";

    private final Listener<String> listener;
    private final File file;

    private Map<String, String> headers;
    private Map<String, String> params;

    public CustomRequestImg(RequestQueue requestQueue, Map<String, String> headers, Map<String, String> params, Listener<String> reponseListener, Response.ErrorListener errorListener, File file, String URL) {
        super(Method.POST, URL, errorListener);
        this.requestQueue = requestQueue;

        this.listener = reponseListener;
        this.file = file;
        if (!headers.isEmpty()) this.headers=headers;
        if (!params.isEmpty()) this.params = params;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        if (file!=null) entity.addPart(FILE_PART_NAME, new FileBody(file));
        try {
            for (String key : params.keySet()) {
                entity.addPart(key, new StringBody(params.get(key)));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }



    @Override
    public String getBodyContentType() {
        String bodyHeader=entity.getContentType().getValue();
        return bodyHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    /**
     * copied from Android StringRequest class
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }
}
