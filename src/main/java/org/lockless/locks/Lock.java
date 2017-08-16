package org.lockless.locks;

/**
 * Locks are instantiated locked.
 * <p>
 * For convenience they may be used using try-with-resource:
 * 
 * <pre>
 * try (Lock lock = Locks.lock()) {
 *     // do your thing
 * }
 * </pre>
 * <p>
 * Depending an the underlying implementation it's possible to receive multiple
 * locks.
 * 
 * @author super-horst
 */
@FunctionalInterface
public interface Lock extends AutoCloseable {

    @Override
    default void close() {
        unlock();
    }

    /**
     * Free this lock
     */
    void unlock();

}
