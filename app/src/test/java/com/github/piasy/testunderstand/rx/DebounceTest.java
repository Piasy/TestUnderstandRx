package com.github.piasy.testunderstand.rx;

import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by Piasy{github.com/Piasy} on 4/6/16.
 */
public class DebounceTest {

    private Subject<Integer, Integer> mSource;
    private TestSubscriber<Timestamped<Timestamped<Integer>>> mSubscriber;

    @Before
    public void setUp() {
        mSource = PublishSubject.create();
        mSubscriber = new TestSubscriber<>();
    }

    @Test
    public void testDebounce() {
        setUp();

        mSource.subscribeOn(Schedulers.computation())
                .timestamp()
                .doOnNext(new Action1<Timestamped<Integer>>() {
                    @Override
                    public void call(Timestamped<Integer> item) {
                        System.out.println(
                                "" + item.getValue() + " emitted at " + item.getTimestampMillis());
                    }
                })
                .debounce(100, TimeUnit.MILLISECONDS)
                .timestamp()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("subscribed at " + System.currentTimeMillis());
                    }
                })
                .subscribe(mSubscriber);

        System.out.println("debounce:");
        doTest(mSource, mSubscriber);
    }

    @Test
    public void testThrottleFirst() {
        setUp();

        mSource.subscribeOn(Schedulers.computation())
                .timestamp()
                .doOnNext(new Action1<Timestamped<Integer>>() {
                    @Override
                    public void call(Timestamped<Integer> item) {
                        System.out.println(
                                "" + item.getValue() + " emitted at " + item.getTimestampMillis());
                    }
                })
                .throttleFirst(100, TimeUnit.MILLISECONDS)
                .timestamp()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("subscribed at " + System.currentTimeMillis());
                    }
                })
                .subscribe(mSubscriber);

        System.out.println("throttleFirst:");
        doTest(mSource, mSubscriber);
    }

    @Test
    public void testThrottleLast() {
        setUp();

        mSource.subscribeOn(Schedulers.computation())
                .timestamp()
                .doOnNext(new Action1<Timestamped<Integer>>() {
                    @Override
                    public void call(Timestamped<Integer> item) {
                        System.out.println(
                                "" + item.getValue() + " emitted at " + item.getTimestampMillis());
                    }
                })
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .timestamp()
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("subscribed at " + System.currentTimeMillis());
                    }
                })
                .subscribe(mSubscriber);

        System.out.println("throttleLast:");
        doTest(mSource, mSubscriber);
    }

    private void doTest(Subject<Integer, Integer> source,
            TestSubscriber<Timestamped<Timestamped<Integer>>> subscriber) {
        Utils.sleep(100);
        source.onNext(1);
        Utils.sleep(30);
        source.onNext(2);
        Utils.sleep(30);
        source.onNext(3);
        Utils.sleep(200);
        source.onCompleted();

        subscriber.awaitTerminalEvent();
        for (Timestamped<Timestamped<Integer>> item : subscriber.getOnNextEvents()) {
            System.out.println("" + item.getValue().getValue() + ", emitted at " +
                    item.getValue().getTimestampMillis() + ", received at " +
                    item.getTimestampMillis());
        }
    }
}
