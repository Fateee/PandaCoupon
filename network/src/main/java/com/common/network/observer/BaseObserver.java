package com.common.network.observer;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;

public abstract class BaseObserver<T> implements Observer<T> {
    @Override
    public void onNext(@NonNull T t) {
        onSuccess(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onFailure(e);
    }

    protected abstract void onSuccess(T t);

    protected abstract void onFailure(Throwable e);
}
