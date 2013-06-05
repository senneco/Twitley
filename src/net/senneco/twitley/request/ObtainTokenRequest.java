package net.senneco.twitley.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by senneco on 21.05.13.
 */
public class ObtainTokenRequest extends JsonObjectRequest {

    /**
     * TODO: set Base64 encoded bearer token credentials
     * More info here: https://dev.twitter.com/docs/auth/application-only-auth
     */
    private static final String BASIC_AUTH = "";
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/x-www-form-urlencoded;charset=%s", PROTOCOL_CHARSET);

    private final OnTokenObtainListener mListener;

    public ObtainTokenRequest(OnTokenObtainListener listener, ErrorListener errorListener) {
        super(Method.POST, Urls.OBTAIN_TOKEN_URL, null, null, errorListener);

        mListener = listener;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        String token = response.optString("token_type");
        token = response.length() == 0 ? "" : (token.substring(0, 1).toUpperCase() + token.substring(1));
        token += " " + response.opt("access_token");

        mListener.onTokenObtain(token);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + BASIC_AUTH);
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        String requestBody = "grant_type=client_credentials";

        try {
            return requestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, PROTOCOL_CHARSET);
            return null;
        }
    }

    public interface OnTokenObtainListener {
        void onTokenObtain(String token);
    }
}
