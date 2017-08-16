package org.lockless.locks;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Distributes a fixed number of {@link ReentrantLock ReentrantLocks} to be run
 * concurrently. May be used to ensure a limited access of threads to a critical
 * section inside a thread.
 * <p>
 * Provides exclusive locks, which occupy all slots.
 * 
 * @apiNote Exclusive {@link ReentrantLock ReentrantLocks} may not traverse
 *          across Threads.
 * 
 * @author super-horst
 */
public final class ReentrantCountingLocks
        implements Locks<ReentrantLock>, ExclusiveLocks<ReentrantLock>, RevokingLocks<Thread> {

    private final AtomicReference<Thread>[]     threadRefs;
    private final Thread[]                      threads;
    private final ThreadRevokingReentrantLock[] locks;

    private final AtomicReference<Thread>   exclThread = new AtomicReference<>();
    private volatile ReentrantExclusiveLock exclLock;

    @SuppressWarnings("unchecked")
    public ReentrantCountingLocks(int slots) {
        this.threadRefs = (AtomicReference<Thread>[]) new AtomicReference[slots];
        this.threads = new Thread[slots];
        this.locks = new ThreadRevokingReentrantLock[slots];

        for (int i = 0; i < this.threadRefs.length; i++) {
            this.threadRefs[i] = new AtomicReference<>();
        }
    }

    @Override
    public ReentrantLock lock() {
        Thread t = Thread.currentThread();
        ThreadRevokingReentrantLock lock = null;

        int tIdx;
        if ((tIdx = Arrays.asList(this.threads).indexOf(t)) >= 0) {
            lock = this.locks[tIdx];
            lock.entered();
        } else {
            lock = insertNew(t);
        }

        return lock;
    }

    @Override
    public ReentrantLock exclusiveLock() {
        Thread t = Thread.currentThread();

        if (t.equals(this.exclThread.get())) {
            this.exclLock.entered();
            return this.exclLock;
        }

        while (!this.exclThread.compareAndSet(null, t)) {
            // do something else if you need to
            Thread.yield();
        }

        this.exclLock = new ReentrantExclusiveLock(new ExclusiveLock<ReentrantLock>(this.locks));

        int locksToAcquire = this.threadRefs.length;
        if (Arrays.asList(this.threads).indexOf(t) >= 0) {
            // reentrant condition
            locksToAcquire--;
        }

        for (int i = 0; i < locksToAcquire; i++) {
            insertNew(t);
        }

        return this.exclLock;
    }

    private ThreadRevokingReentrantLock insertNew(Thread t) {
        ThreadRevokingReentrantLock lock = null;

        boolean success = false;
        do {
            for (int i = 0; i < this.threadRefs.length; i++) {
                if (success = this.threadRefs[i].compareAndSet(null, t)) {
                    this.threads[i] = t;
                    this.locks[i] = lock = new ThreadRevokingReentrantLock(this);
                    break;
                }
            }
        } while (!success);

        return lock;
    }

    @Override
    public void revoke(Thread t) {
        int tIdx;
        if ((tIdx = Arrays.asList(this.threads).indexOf(t)) >= 0) {
            this.threads[tIdx] = null;
            this.locks[tIdx] = null;
            if (!this.threadRefs[tIdx].compareAndSet(t, null)) {
                throw new IllegalThreadStateException("illegal unlock");
            }
            this.exclThread.compareAndSet(t, null);
            // no need to reset this.exclLock
        } else {
            throw new IllegalThreadStateException("illegal unlock");
        }
    }
}
