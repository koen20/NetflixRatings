package nl.koenhabets.netflixratings;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ImdbRatingRequest extends Request<String> {
    private static String url = "http://www.omdbapi.com/?apikey=9c57024e&t=";

    private Response.Listener<String> responListener;

    public ImdbRatingRequest(String movie,
                             Response.Listener<String> responseListener,
                             Response.ErrorListener errorListener) {

        super(Method.GET, url + URLEncoder.encode(movie), errorListener);

        this.responListener = responseListener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String data;
        try {
            data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            data = new String(response.data);
        }
        return Response.success(data, null);
    }

    @Override
    protected void deliverResponse(String response) {
        responListener.onResponse(response);
    }
}
