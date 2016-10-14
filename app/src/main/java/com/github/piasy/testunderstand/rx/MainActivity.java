package com.github.piasy.testunderstand.rx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Observable<Integer> local = Observable.just(1, 2, 3);
        Observable<Integer> remote = Observable.error(new RuntimeException("Boom!!!"));
        Observable.concat(local.doOnSubscribe(() -> System.out.println(
                "local().doOnSubscribe #" + Thread.currentThread().getName()))
                        .doOnNext(integer -> System.out.println(
                                "local().doOnNext: " + integer + " #" + Thread.currentThread()
                                        .getName()))
                        .doOnCompleted(() -> System.out.println(
                                "local().doOnCompleted #" + Thread.currentThread().getName()))
                        .doOnUnsubscribe(() -> System.out.println(
                                "local().doOnUnsubscribe #" + Thread.currentThread().getName()))
                        .doOnError(e -> System.out.println(
                                "local().doOnError #" + Thread.currentThread().getName())),
                remote.doOnSubscribe(() -> System.out.println(
                        "remote().doOnSubscribe #" + Thread.currentThread().getName()))
                        .doOnNext(integer -> System.out.println(
                                "remote().doOnNext: " + integer + " #" + Thread.currentThread()
                                        .getName()))
                        .doOnCompleted(() -> System.out.println(
                                "remote().doOnCompleted #" + Thread.currentThread().getName()))
                        .doOnUnsubscribe(() -> System.out.println(
                                "remote().doOnUnsubscribe #" + Thread.currentThread().getName()))
                        .doOnError(e -> System.out.println(
                                "remote().doOnError #" + Thread.currentThread().getName())))
                .doOnSubscribe(() -> System.out.println(
                        "concat().doOnSubscribe #" + Thread.currentThread().getName()))
                .doOnNext(integer -> System.out.println(
                        "concat().doOnNext: " + integer + " #" + Thread.currentThread().getName()))
                .doOnCompleted(() -> System.out.println(
                        "concat().doOnCompleted #" + Thread.currentThread().getName()))
                .doOnUnsubscribe(() -> System.out.println(
                        "concat().doOnUnsubscribe #" + Thread.currentThread().getName()))
                .doOnError(e -> System.out.println(
                        "concat().doOnError #" + Thread.currentThread().getName()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }
                });*/

        Observable.just("Hello world")
                .subscribe(word -> {
                    System.out.println("got " + word + " @ "
                            + Thread.currentThread().getName());
                });

        Observable.just("Hello world")
                .map(String::length)
                .subscribe(word -> {
                    System.out.println("got " + word + " @ "
                            + Thread.currentThread().getName());
                });

        Observable.just("Hello world")
                .map(String::length)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(len -> {
                    System.out.println("got " + len + " @ "
                            + Thread.currentThread().getName());
                });
    }
}
