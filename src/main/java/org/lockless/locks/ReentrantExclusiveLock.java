package org.lockless.locks;

/**
 * Unlocks a number of locks at once.
 * 
 * @apiNote Exclusive reentrant locks may not traverse through Threads.
 * 
 * @author super-horst
 */
final class ReentrantExclusiveLock implements ReentrantLock {

    private int                                entryCount = 1;
    private final ExclusiveLock<ReentrantLock> lock;

    ReentrantExclusiveLock(ExclusiveLock<ReentrantLock> lock) {
        this.lock = lock;
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
            this.lock.unlock();
        }
    }

    @Override
    public int entries() {
        return this.entryCount;
    }
}
