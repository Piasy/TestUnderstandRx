package com.github.piasy.testunderstand.rx;

/**
 * Created by Piasy{github.com/Piasy} on 4/6/16.
 */
public final class Utils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
