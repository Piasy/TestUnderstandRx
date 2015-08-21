package com.github.piasy.testunderstand.rx;

import android.support.annotation.NonNull;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

/**
 * Created by Piasy{github.com/Piasy} on 15/8/13.
 */
public class HotColdObservableTest {

    private MockIntProducer mMockIntProducer;

    @Before
    public void setUp() {
        mMockIntProducer = Mockito.mock(MockIntProducer.class);
    }

    @Test
    public void verifyJustAsHotInValid() {
        // NOTE!!!
        // invalid test case, mMockIntProducer.produce() will be evaluated before call
        // Observable.just, so of course MockIntProducer::produce will be called immediately
        // at Observable.just, so does the Observable.from

        // given
        final int mock = 5;
        given(mMockIntProducer.produce()).willReturn(mock);
        Observable<Integer> observable = Observable.just(mMockIntProducer.produce());

        then(mMockIntProducer).should(only()).produce();
        observable.subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Assert.assertEquals(integer.intValue(), mock);
                then(mMockIntProducer).should(only()).produce();
            }
        });
    }

    @Test
    public void verifyJustAsCold() {
        final Integer intHolder = 5;
        Observable<Integer> observable = Observable.just(intHolder);

        TestSubscriber<Integer> subscriber1 = new TestSubscriber<>();
        observable.subscribe(subscriber1);
        subscriber1.awaitTerminalEvent();
        subscriber1.assertCompleted();
        subscriber1.assertNoErrors();
        subscriber1.assertValue(intHolder);

        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>();
        observable.subscribe(subscriber2);
        subscriber2.awaitTerminalEvent();
        subscriber2.assertCompleted();
        subscriber2.assertNoErrors();
        subscriber2.assertValue(intHolder);
    }

    @Test
    public void verifyFromAsCold() {
        final List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        Observable<Integer> observable = Observable.from(integers);

        TestSubscriber<Integer> subscriber1 = new TestSubscriber<>();
        observable.subscribe(subscriber1);
        subscriber1.awaitTerminalEvent();
        subscriber1.assertCompleted();
        subscriber1.assertNoErrors();
        subscriber1.assertReceivedOnNext(integers);

        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>();
        observable.subscribe(subscriber2);
        subscriber2.awaitTerminalEvent();
        subscriber2.assertCompleted();
        subscriber2.assertNoErrors();
        subscriber2.assertReceivedOnNext(integers);
    }

    @Test
    public void verifyCreateAsCold() {
        final int mock = 5;
        given(mMockIntProducer.produce()).willReturn(mock);

        then(mMockIntProducer).should(never()).produce();
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(mMockIntProducer.produce());
                subscriber.onCompleted();
            }
        });
        then(mMockIntProducer).should(never()).produce();

        TestSubscriber<Integer> subscriber1 = new TestSubscriber<>();
        observable.subscribe(subscriber1);
        subscriber1.awaitTerminalEvent();
        subscriber1.assertCompleted();
        subscriber1.assertNoErrors();
        subscriber1.assertValue(mock);
        then(mMockIntProducer).should().produce();

        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>();
        observable.subscribe(subscriber2);
        subscriber2.awaitTerminalEvent();
        subscriber2.assertCompleted();
        subscriber2.assertNoErrors();
        subscriber2.assertValue(mock);
        then(mMockIntProducer).should(times(2)).produce();
    }

    @Test
    public void verifyDeferAsCold() {
        final int mock = 5;
        given(mMockIntProducer.produce()).willReturn(mock);

        then(mMockIntProducer).should(never()).produce();
        Observable<Integer> observable = Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return Observable.just(mMockIntProducer.produce());
            }
        });
        then(mMockIntProducer).should(never()).produce();

        TestSubscriber<Integer> subscriber1 = new TestSubscriber<>();
        observable.subscribe(subscriber1);
        subscriber1.awaitTerminalEvent();
        subscriber1.assertCompleted();
        subscriber1.assertNoErrors();
        subscriber1.assertValue(mock);
        then(mMockIntProducer).should().produce();

        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>();
        observable.subscribe(subscriber2);
        subscriber2.awaitTerminalEvent();
        subscriber2.assertCompleted();
        subscriber2.assertNoErrors();
        subscriber2.assertValue(mock);
        then(mMockIntProducer).should(times(2)).produce();
    }

    @Test
    public void verifyReplayAsHot() {
        given(mMockIntProducer.produce()).willReturn(0, 1, 2, 3, 4, 5, 6, 7);

        then(mMockIntProducer).should(never()).produce();
        ConnectableObservable<Integer> observable =
                Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        for (int i = 0; i < 8; i++) {
                            subscriber.onNext(mMockIntProducer.produce());
                            sleep(1000);
                        }
                        subscriber.onCompleted();
                    }
                }).replay();
        sleep(2000);
        then(mMockIntProducer).should(never()).produce();

        TestSubscriber<Integer> subscriber1 = new TestSubscriber<>(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("subscriber1 complete");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer i) {
                System.out.println("subscriber1: " + i);
            }
        });
        observable.subscribe(subscriber1);
        observable.connect();

        sleep(2500);
        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("\tsubscriber2 complete");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer i) {
                System.out.println("\tsubscriber2: " + i);
            }
        });
        observable.subscribe(subscriber2);

        subscriber1.awaitTerminalEvent();
        subscriber2.awaitTerminalEvent();
        subscriber1.assertCompleted();
        subscriber1.assertNoErrors();
        subscriber2.assertCompleted();
        subscriber2.assertNoErrors();

        List<Integer> values1 = subscriber1.getOnNextEvents();
        List<Integer> values2 = subscriber2.getOnNextEvents();
        Assert.assertEquals(8, values1.size());
        // ~~subscriber1 not unsubscribe,~~ so subscriber2 will receive all items emitted by the
        // source observable
        //Assert.assertEquals(6, values2.size());
        Assert.assertEquals(8, values2.size());
        for (int i = 0; i < values1.size(); i++) {
            Assert.assertEquals(i, values1.get(i).intValue());
        }
        for (int i = 0; i < values2.size(); i++) {
            //Assert.assertEquals(i + 2, values1.get(i).intValue());
            Assert.assertEquals(i, values2.get(i).intValue());
        }

        then(mMockIntProducer).should(times(8)).produce();
    }

    @Test
    public void verifyReplayAsHotWithUnsubscribe() {
        given(mMockIntProducer.produce()).willReturn(0, 1, 2, 3, 4, 5, 6, 7);

        then(mMockIntProducer).should(never()).produce();
        ConnectableObservable<Integer> observable =
                Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        for (int i = 0; i < 8; i++) {
                            subscriber.onNext(mMockIntProducer.produce());
                            sleep(1000);
                        }
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.from(new Executor() {
                    @Override
                    public void execute(@NonNull Runnable command) {
                        new Thread(command).start();
                    }
                })).replay();
        sleep(2000);
        then(mMockIntProducer).should(never()).produce();

        TestSubscriber<Integer> subscriber1 = new TestSubscriber<>(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("subscriber1 complete" + System.currentTimeMillis());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer i) {
                System.out.println("subscriber1: " + i + " " + System.currentTimeMillis());
            }
        });
        Subscription subscription1 = observable.subscribe(subscriber1);
        observable.connect();

        sleep(2500);
        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("\tsubscriber2 complete" + System.currentTimeMillis());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer i) {
                System.out.println("\tsubscriber2: " + i + " " + System.currentTimeMillis());
            }
        });

        // the order doesn't matter
        subscription1.unsubscribe();
        observable.subscribe(subscriber2);
        // no need to call again
        //observable.connect();

        subscriber2.awaitTerminalEvent();
        subscriber1.assertUnsubscribed();
        //subscriber1.assertCompleted();  // not completed
        subscriber1.assertNoErrors();
        subscriber2.assertCompleted();
        subscriber2.assertNoErrors();

        List<Integer> values1 = subscriber1.getOnNextEvents();
        List<Integer> values2 = subscriber2.getOnNextEvents();
        // subscriber1 unsubscribed, ~~but will still receive all items~~, won't receive item anymore
        Assert.assertEquals(3, values1.size());
        // subscriber1 unsubscribed, but subscriber2 still will receive all items emitted by the
        // source observable
        //Assert.assertEquals(6, values2.size());
        Assert.assertEquals(8, values2.size());
        for (int i = 0; i < values1.size(); i++) {
            Assert.assertEquals(i, values1.get(i).intValue());
        }
        for (int i = 0; i < values2.size(); i++) {
            //Assert.assertEquals(i + 2, values1.get(i).intValue());
            Assert.assertEquals(i, values2.get(i).intValue());
        }

        then(mMockIntProducer).should(times(8)).produce();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private interface MockIntProducer {
        int produce();
    }
}
