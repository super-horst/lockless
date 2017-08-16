package org.lockless.locks;

/**
 * Thread local lock, which is distinct for the thread that acquired it.
 * Multiple lock entries are supported from the thread that acquired it.
 * 
 * @apiNote Exclusive {@link ReentrantLock ReentrantLocks} may not traverse
 *          across Threads.
 * 
 * @author super-horst
 */
public interface ReentrantLock extends Lock {

    /**
     * How often the current thread has entered this lock.
     * 
     * @return number of lock entries
     */
    int entries();

}
