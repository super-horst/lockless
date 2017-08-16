package org.lockless.locks;

final class ThreadRevokingReentrantLock implements ReentrantLock {

    private int                         entryCount;
    private final RevokingLocks<Thread> locks;

    ThreadRevokingReentrantLock(RevokingLocks<Thread> locks) {
        this(locks, 1);
    }

    ThreadRevokingReentrantLock(RevokingLocks<Thread> locks, int entryCount) {
        this.locks = locks;
        this.entryCount = entryCount;
    }

    /**
     * @apiNote {@link ReentrantLock ReentrantLocks} may not traverse through
     *          threads!
     */
    void entered() {
        this.entryCount++;
    }

    @Override
    public void unlock() {
        if (--this.entryCount == 0) {
            this.locks.revoke(Thread.currentThread());
        }
    }

    @Override
    public int entries() {
        return this.entryCount;
    }

}
