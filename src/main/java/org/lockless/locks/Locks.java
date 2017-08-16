package org.lockless.locks;

/**
 * Distributing {@link Lock Locks}
 * 
 * @author super-horst
 */
@FunctionalInterface
public interface Locks<L extends Lock> {

    /**
     * Receive a ready-to-be-unlocked {@link Lock}
     * 
     * @return a lock
     */
    L lock();
}
