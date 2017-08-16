package org.lockless.locks;

/**
 * Unlocks a number of locks at once.
 * 
 * @author super-horst
 */
final class ExclusiveLock<L extends Lock> implements Lock {

    private final L[] locks;

    ExclusiveLock(L[] locks) {
        this.locks = locks;
    }

    @Override
    public void unlock() {
        for (L lock : this.locks) {
            lock.unlock();
        }
    }
}
