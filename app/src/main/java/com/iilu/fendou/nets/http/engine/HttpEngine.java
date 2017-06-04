package com.iilu.fendou.nets.http.engine;

import com.iilu.fendou.configs.AppConfig;
import com.iilu.fendou.nets.http.UrlContanier;
import com.iilu.fendou.nets.http.api.HttpService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * http封装库，作为引擎提供okhttp、rxjava与retrofit的封装
 */
public class HttpEngine {

    private static final int DEFAULT_TIMEOUT = 5;
    private final String BASE_URL = UrlContanier.mobile_base;
    private Retrofit mRetrofit;
    private HttpService mHttpService;
    private static HttpEngine INSTANCE;

    public static HttpEngine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpEngine();
        }
        return INSTANCE;
    }

    public HttpService getHttpService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(logging);
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mRetrofit = new Retrofit.Builder()
                .client(httpClientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("clientVersion", AppConfig.CLIENTVERSION)
                                .addHeader("clientName", AppConfig.CLIENTNAME)
                                .addHeader("accessToken", AppConfig.ACCESSTOKEN)
                                .addHeader("version", AppConfig.HTTP_VERSION)
                                .build();
                        return chain.proceed(request);
                    }

                }).build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mHttpService = mRetrofit.create(HttpService.class);
        return mHttpService;
    }
}
