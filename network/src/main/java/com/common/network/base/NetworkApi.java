package com.common.network.base;

import com.common.network.environment.IEnvironment;
import com.common.network.errorhandler.HttpErrorHandler;
import com.common.network.interceptor.CommonRequestInterceptor;
import com.common.network.interceptor.CommonResponseInterceptor;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetworkApi implements IEnvironment {
    private static INetworkRequiredInfo iNetworkRequiredInfo;
    private static HashMap<String,Retrofit> retrofitHashMap = new HashMap<>();
    private static boolean mIsOnLine = true;
    private OkHttpClient mOkHttpClient;
    private String mBaseUrl;

    protected NetworkApi() {
        if (!mIsOnLine) {
            mBaseUrl = getTest();
        }
        mBaseUrl = getOnline();
    }

    public static void init(INetworkRequiredInfo networkRequiredInfo) {
        iNetworkRequiredInfo = networkRequiredInfo;
//        mIsOnLine =
    }

    protected Retrofit getRetrofit(Class service) {
        if (retrofitHashMap.get(mBaseUrl + service.getName()) != null) {
            return retrofitHashMap.get(mBaseUrl + service.getName());
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(mBaseUrl);
        builder.client(getOkHttpClient());
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        Retrofit retrofit = builder.build();
        retrofitHashMap.put(mBaseUrl + service.getName(),retrofit);
        return retrofit;
    }

    private OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            if (getInterceptor() != null) {
                clientBuilder.addInterceptor(getInterceptor());
            }
            clientBuilder.addInterceptor(new CommonRequestInterceptor(iNetworkRequiredInfo));
            clientBuilder.addInterceptor(new CommonResponseInterceptor());
            if (iNetworkRequiredInfo != null && (iNetworkRequiredInfo.isDebug())) {
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                clientBuilder.addInterceptor(httpLoggingInterceptor);
            }
            mOkHttpClient = clientBuilder.build();
        }
        return mOkHttpClient;
    }

    public <T> ObservableTransformer<T,T> applySchedules(final Observer<T> observer) {
        return upstream -> {
            Observable<T> observable = upstream.subscribeOn(Schedulers.io());
            observable.observeOn(AndroidSchedulers.mainThread());
            observable.map(getAppErrorHandler());
            observable.subscribe(observer);
            observable.onErrorResumeNext(new HttpErrorHandler<T>());
            return null;
        };
    }

    protected abstract Interceptor getInterceptor();

    protected abstract <T> Function<T, T> getAppErrorHandler();
}
