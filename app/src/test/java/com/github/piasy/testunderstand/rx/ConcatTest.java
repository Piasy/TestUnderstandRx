package com.github.piasy.testunderstand.rx;

import android.util.Log;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by Piasy{github.com/Piasy} on 7/4/16.
 */

public class ConcatTest {
    @Test
    public void testConcatWithError() {
        TestSubscriber<Integer> subscriber = new TestSubscriber<>(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("subscriber.onCompleted #" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("subscriber.onError #" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("subscriber.onNext: " + integer + " #" + Thread.currentThread().getName());
            }
        });
        Observable.concat(
                local()
                        .doOnSubscribe(() -> System.out.println("local().doOnSubscribe #" + Thread.currentThread().getName()))
                        .doOnNext(integer -> System.out.println("local().doOnNext: " + integer + " #" + Thread.currentThread().getName()))
                        .doOnCompleted(() -> System.out.println("local().doOnCompleted #" + Thread.currentThread().getName()))
                        .doOnUnsubscribe(() -> System.out.println("local().doOnUnsubscribe #" + Thread.currentThread().getName()))
                        .doOnError(e -> System.out.println("local().doOnError #" + Thread.currentThread().getName())),
                remote()
                        .doOnSubscribe(() -> System.out.println("remote().doOnSubscribe #" + Thread.currentThread().getName()))
                        .doOnNext(integer -> System.out.println("remote().doOnNext: " + integer + " #" + Thread.currentThread().getName()))
                        .doOnCompleted(() -> System.out.println("remote().doOnCompleted #" + Thread.currentThread().getName()))
                        .doOnUnsubscribe(() -> System.out.println("remote().doOnUnsubscribe #" + Thread.currentThread().getName()))
                        .doOnError(e -> System.out.println("remote().doOnError #" + Thread.currentThread().getName())))
                .doOnSubscribe(() -> System.out.println("concat().doOnSubscribe #" + Thread.currentThread().getName()))
                .doOnNext(integer -> System.out.println("concat().doOnNext: " + integer + " #" + Thread.currentThread().getName()))
                .doOnCompleted(() -> System.out.println("concat().doOnCompleted #" + Thread.currentThread().getName()))
                .doOnUnsubscribe(() -> System.out.println("concat().doOnUnsubscribe #" + Thread.currentThread().getName()))
                .doOnError(e -> System.out.println("concat().doOnError #" + Thread.currentThread().getName()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent();
        subscriber.assertValues(1, 2, 3);
        subscriber.assertError(RuntimeException.class);
    }

    private static Observable<Integer> local() {
        return Observable.just(1, 2, 3);
    }

    private static Observable<Integer> remote() {
        return Observable.error(new RuntimeException("Error"));
    }

    @Test
    public void test2() {
        Observable<Integer> local = Observable.just(1, 2, 3);
        Observable<Integer> remote2 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onError(new RuntimeException("Boom!!!"));
            }
        });
        Observable.concat(local, remote2)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
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
                });
    }
}
