package net.senneco.twitley.request;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 */
public class GsonRequest<T> extends Request<T> {
    private static final Gson GSON;
    private final String mToken;
    private final Class<T> mResponseClass;
    private final Response.Listener<T> mListener;

    static {
        GSON = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setFieldNamingStrategy(new CustomFieldNamingPolicy())
                .setPrettyPrinting()
                .setDateFormat("EEE MMM d HH:mm:ss Z yyyy")
                .serializeNulls()
                .create();
    }

    public GsonRequest(String token, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(token, Method.GET, url, clazz, listener, errorListener);
    }

    public GsonRequest(String token, int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        mToken = token;
        mResponseClass = clazz;
        mListener = listener;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", mToken);
        return headers;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            if (json.startsWith("[")) {
                json = String.format("{response:%s}", json);
            }

            return Response.success(GSON.fromJson(json, mResponseClass), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    static class CustomFieldNamingPolicy implements FieldNamingStrategy {
        @Override
        public String translateName(Field field) {
            String name = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(field);
            name = name.substring(2, name.length()).toLowerCase();
            return name;
        }
    }
}