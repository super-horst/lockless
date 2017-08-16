package org.lockless.locks;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Simple lock, is either locked or unlocked.
 * 
 * @author super-horst
 */
public final class BinaryLocks implements Locks<Lock> {

    private static final Object UNLOCKED = new Object();
    private static final Object LOCKED   = new Object();

    private final AtomicReference<Object> ref  = new AtomicReference<>(UNLOCKED);
    private final Lock                    lock = () -> this.ref.set(UNLOCKED);

    @Override
    public Lock lock() {
        while (!this.ref.compareAndSet(UNLOCKED, LOCKED)) {
            // do something else if you need to
            Thread.yield();
        }

        return this.lock;
    }
}
