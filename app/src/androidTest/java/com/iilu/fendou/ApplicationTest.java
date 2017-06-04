package com.iilu.fendou;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.iilu.fendou.nets.loopj.AsyncHttpClient;
import com.iilu.fendou.nets.loopj.AsyncHttpResponseHandler;
import com.iilu.fendou.nets.loopj.RequestParams;

import org.apache.http.Header;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    private void AsyncHttpClient_Test_1() {
        String url = "";
        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxConnections(10000);
        client.post(url, params, new AsyncHttpResponseHandler(){
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, String content) {
                super.onFailure(statusCode, headers, error, content);
            }
        });
    }


    private void AsyncHttpClient_Test_2() {
        String url = "";
        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();

        com.loopj.android.http.AsyncHttpClient  client = new com.loopj.android.http.AsyncHttpClient();
        client.setMaxConnections(10000);
        client.post(url, params, new com.loopj.android.http.AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                super.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                super.onFailure(statusCode, headers, responseBody, error);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onRetry() {
                super.onRetry();
            }
        });


    }
}