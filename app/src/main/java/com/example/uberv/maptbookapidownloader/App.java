package com.example.uberv.maptbookapidownloader;

import android.app.Application;

import com.example.uberv.maptbookapidownloader.Utils.AuthenticationUtils;
import com.example.uberv.maptbookapidownloader.api.MaptService;
import com.example.uberv.maptbookapidownloader.logging.DevelopmentTree;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class App extends Application {

    private static Retrofit sRetrofit;
    private static MaptService sMaptService;

    @Override
    public void onCreate() {
        super.onCreate();

        AuthenticationUtils.initSharedPref(this.getApplicationContext());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        sRetrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Constants.MAPT_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sMaptService = sRetrofit.create(MaptService.class);

        Timber.plant(new DevelopmentTree());
    }

    public static Retrofit getRetrofit() {
        return sRetrofit;
    }

    public static MaptService getMaptService() {
        return sMaptService;
    }
}
