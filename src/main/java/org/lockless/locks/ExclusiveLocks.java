package org.lockless.locks;

/**
 * Distributing exclusive locks.
 * 
 * @author super-horst
 */
public interface ExclusiveLocks<L extends Lock> {

    /**
     * Exclusively reserve all possible locks from the underlying implementation
     * 
     * @return an exclusive lock object
     */
    L exclusiveLock();

}