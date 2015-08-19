package com.piasy.testunderstand.rx;

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.observers.TestSubscriber;

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

        final int mock = 5;
        Mockito.when(mMockIntProducer.produce()).thenReturn(mock);
        Observable<Integer> observable = Observable.just(mMockIntProducer.produce());

        Mockito.verify(mMockIntProducer, Mockito.only()).produce();
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
                Mockito.verify(mMockIntProducer, Mockito.only()).produce();
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
        Mockito.when(mMockIntProducer.produce()).thenReturn(mock);

        Mockito.verify(mMockIntProducer, Mockito.never()).produce();
        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(mMockIntProducer.produce());
                subscriber.onCompleted();
            }
        });
        Mockito.verify(mMockIntProducer, Mockito.never()).produce();

        TestSubscriber<Integer> subscriber1 = new TestSubscriber<>();
        observable.subscribe(subscriber1);
        subscriber1.awaitTerminalEvent();
        subscriber1.assertCompleted();
        subscriber1.assertNoErrors();
        subscriber1.assertValue(mock);
        Mockito.verify(mMockIntProducer).produce();

        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>();
        observable.subscribe(subscriber2);
        subscriber2.awaitTerminalEvent();
        subscriber2.assertCompleted();
        subscriber2.assertNoErrors();
        subscriber2.assertValue(mock);
        Mockito.verify(mMockIntProducer, Mockito.times(2)).produce();
    }

    @Test
    public void verifyDeferAsCold() {
        final int mock = 5;
        Mockito.when(mMockIntProducer.produce()).thenReturn(mock);

        Mockito.verify(mMockIntProducer, Mockito.never()).produce();
        Observable<Integer> observable = Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return Observable.just(mMockIntProducer.produce());
            }
        });
        Mockito.verify(mMockIntProducer, Mockito.never()).produce();

        TestSubscriber<Integer> subscriber1 = new TestSubscriber<>();
        observable.subscribe(subscriber1);
        subscriber1.awaitTerminalEvent();
        subscriber1.assertCompleted();
        subscriber1.assertNoErrors();
        subscriber1.assertValue(mock);
        Mockito.verify(mMockIntProducer).produce();

        TestSubscriber<Integer> subscriber2 = new TestSubscriber<>();
        observable.subscribe(subscriber2);
        subscriber2.awaitTerminalEvent();
        subscriber2.assertCompleted();
        subscriber2.assertNoErrors();
        subscriber2.assertValue(mock);
        Mockito.verify(mMockIntProducer, Mockito.times(2)).produce();
    }

    private static class MockIntProducer {

        public int produce() {
            // won't really call
            return 0;
        }
    }
}
