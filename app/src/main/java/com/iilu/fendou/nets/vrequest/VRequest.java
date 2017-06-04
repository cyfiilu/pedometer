package com.iilu.fendou.nets.vrequest;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VRequest extends StringRequest {

	public VRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
		super(method, url, listener, errorListener);
		setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
	}
	
	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try{
			String jsonString = new String(response.data, "UTF-8");
			/**
			 * response.headers.put(HTTP.CONTENT_TYPE, response.headers.get("content-type"));
			 * String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			 */
			 return Response.success(new String(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
    	Map<String, String> headers = new HashMap<String, String>();
    	headers.put("Charset", "UTF-8");
    	headers.put("Content-Type", "application/x-javascript");
    	headers.put("Accept-Encoding", "gzip,deflate");
    	return headers;
    }

}
