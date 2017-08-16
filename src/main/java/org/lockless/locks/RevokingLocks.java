package org.lockless.locks;

/**
 * @author super-horst
 *
 * @param <T>
 *            type of the locked object
 */
interface RevokingLocks<T> {

    /**
     * Invalidate the lock for the given object.
     * 
     * @param obj
     *            object to revoke
     */
    void revoke(T obj);
}
