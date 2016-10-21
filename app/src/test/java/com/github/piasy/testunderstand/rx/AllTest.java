package com.github.piasy.testunderstand.rx;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Piasy{github.com/Piasy} on 16/10/2016.
 */

public class AllTest {
    @Test
    public void all() {
        Observable.<Integer>empty()
                .all(integer -> integer % 2 == 0)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        System.out.println("onNext " + aBoolean);
                    }
                });
    }
}
