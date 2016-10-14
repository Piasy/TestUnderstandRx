package com.github.piasy.testunderstand.rx;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Piasy{github.com/Piasy} on 5/22/16.
 */

public class SchedulerTest {
    @Test
    public void testSubscribeOnTogether() {
        Observable.defer(() -> Observable.from(createInts()))
                .filter(this::odd)
                .map(this::square)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(this::print);
        Utils.sleep(2000);
    }

    @Test
    public void testSubscribeOnSplit() {
        Observable.defer(() -> Observable.from(createInts()))
                .subscribeOn(Schedulers.io())
                .filter(this::odd)
                .map(this::square)
                .observeOn(Schedulers.computation())
                .subscribe(this::print);
        Utils.sleep(2000);
    }

    @Test
    public void testZip() {
        Observable<Integer> odd = Observable.defer(() -> Observable.from(createInts()))
                .filter(this::odd)
                .map(this::square);
        Observable<Integer> even = Observable.defer(() -> Observable.from(createInts()))
                .filter(this::even)
                .map(this::square);
        Observable.zip(odd, even, this::add)
                .map(this::triple)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(this::print);
        Utils.sleep(2000);
    }

    @Test
    public void testZip2() {
        Observable<Integer> odd = Observable.defer(() -> Observable.from(createInts()))
                .filter(this::odd)
                .map(this::square)
                .subscribeOn(Schedulers.newThread());
        Observable<Integer> even = Observable.defer(() -> Observable.from(createInts()))
                .filter(this::even)
                .map(this::square);
        Observable.zip(odd, even, this::add)
                .map(this::triple)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(this::print);
        Utils.sleep(2000);
    }

    @Test
    public void testZip3() {
        Observable<Integer> odd = Observable.defer(() -> Observable.from(createInts()))
                .filter(this::odd)
                .map(this::square)
                .subscribeOn(Schedulers.io());
        Observable<Integer> even = Observable.defer(() -> Observable.from(createInts()))
                .filter(this::even)
                .map(this::square)
                .subscribeOn(Schedulers.io());
        Observable.zip(odd, even, this::add)
                .map(this::triple)
                .observeOn(Schedulers.computation())
                .subscribe(this::print);
        Utils.sleep(2000);
    }

    @Test
    public void testZip4() {
        Observable<Integer> odd = Observable
                .<Integer>create(subscriber -> {
                    System.out.println("create 1 from " + Thread.currentThread().getName());
                    subscriber.onNext(1);
                    subscriber.onCompleted();
                })
                .observeOn(Schedulers.computation());
        Observable<Integer> even = Observable
                .<Integer>create(subscriber -> {
                    System.out.println("create 2 from " + Thread.currentThread().getName());
                    subscriber.onNext(2);
                    subscriber.onCompleted();
                })
                .observeOn(Schedulers.computation());
        Observable.zip(odd, even, this::add)
                .observeOn(Schedulers.io())
                .map(this::triple)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(this::print);
        Utils.sleep(2000);
    }

    private List<Integer> createInts() {
        System.out.println("createInts from " + Thread.currentThread().getName());
        return Arrays.asList(1, 2, 3, 4, 5);
    }

    private boolean odd(Integer i) {
        System.out.println("odd " + i + " from " + Thread.currentThread().getName());
        return i % 2 == 1;
    }

    private boolean even(Integer i) {
        System.out.println("even " + i + " from " + Thread.currentThread().getName());
        return i % 2 == 0;
    }

    private int square(Integer i) {
        System.out.println("square " + i + " from " + Thread.currentThread().getName());
        return i * i;
    }

    private int triple(Integer i) {
        System.out.println("triple " + i + " from " + Thread.currentThread().getName());
        return i * 3;
    }

    private int add(Integer i1, Integer i2) {
        System.out.println(
                "add " + i1 + " and " + i2 + " from " + Thread.currentThread().getName());
        return i1 + i2;
    }

    private void print(Integer i) {
        System.out.println("print " + i + " from " + Thread.currentThread().getName());
    }
}
