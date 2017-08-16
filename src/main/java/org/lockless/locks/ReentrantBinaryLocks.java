package org.lockless.locks;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Reentrant locks
 * 
 * @apiNote Exclusive {@link ReentrantLock ReentrantLocks} may not traverse
 *          across Threads.
 * 
 * @author super-horst
 */
public final class ReentrantBinaryLocks implements Locks<ReentrantLock>, RevokingLocks<Thread> {

    private final AtomicReference<Thread>     ref  = new AtomicReference<>();
    private final ThreadRevokingReentrantLock lock = new ThreadRevokingReentrantLock(this, 0);

    @Override
    public ReentrantLock lock() {
        Thread t = Thread.currentThread();

        if (!t.equals(this.ref.get())) {
            while (!this.ref.compareAndSet(null, t)) {
                // do something else if you need to
                Thread.yield();
            }
        }

        this.lock.entered();
        return this.lock;
    }

    @Override
    public void revoke(Thread t) {
        if (!this.ref.compareAndSet(t, null)) {
            throw new IllegalThreadStateException("illegal unlock");
        }
    }
}
