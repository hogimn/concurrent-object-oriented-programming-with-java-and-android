package edu.vandy.simulator.managers.palantiri.spinLockHashMap;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * This class emulates a "compare and swap"-style spin lock with
 * recursive semantics and should be implemented by students taking
 * the class for graduate credit.
 */
public class ReentrantSpinLock
        implements CancellableLock {
    /**
     * Define an AtomicReference that's used as the basis for an
     * atomic compare-and-swap.  The default state of the spinlock
     * should be "unlocked".
     */
    // TODO -- you fill in here.
    private final AtomicReference<Thread> mOwner = new AtomicReference<>();

    /**
     * Count the number of times the owner thread has recursively
     * acquired the lock.
     */
    // TODO -- you fill in here.
    private int recursiveCount;

    /**
     * @return The current recursion count.
     */
    public int getRecursionCount() {
        // TODO -- you fill in here replacing this statement with your solution.
        return recursiveCount;
    }

    /**
     * Acquire the lock only if it is free at the time of invocation.
     * Acquire the lock if it is available and returns immediately
     * with the value true. If the lock is not available then this
     * method will return immediately with the value false.
     */
    @Override
    public boolean tryLock() {
        // Try to set mOwner's value to the thread (true), which
        // succeeds iff its current value is null (false).
        // TODO -- you fill in here replacing this statement with your solution.
        return mOwner.compareAndSet(null, Thread.currentThread());
    }

    /**
     * Acquire the lock. If the lock is not available then the current
     * thread spins until the lock has been acquired.
     *
     * @param isCancelled Supplier that is called to see if the attempt
     *                    to lock should be abandoned due to a pending
     *                    shutdown operation.
     * @throws CancellationException Thrown only if a pending shutdown
     *                               operation is has been detected by calling the isCancelled supplier.
     */
    @Override
    public void lock(Supplier<Boolean> isCancelled)
            throws CancellationException {
        // If the current thread owns the lock simply increment the
        // recursion count.  Otherwise, loop trying to set mOwner's
        // value to the current thread reference, which succeeds iff
        // its current value is null.  Each iteration should also
        // check if a shutdown has been requested and if so throw a
        // cancellation exception.  
        // TODO -- you fill in here.
        if (mOwner.get() == Thread.currentThread()) {
            recursiveCount++;
            return;
        }

        while (!(mOwner.get() == null && tryLock())) {
            if (isCancelled.get()) {
                throw new CancellationException(
                        "isCancelled returns true.");
            }
            Thread.yield();
        }
    }

    /**
     * Release the lock.  Throws IllegalMonitorStateException if
     * the calling thread doesn't own the lock.
     */
    @Override
    public void unlock() {
        // If the current owner is trying to unlock then simply
        // decrement the recursion count if it's > 0.  Otherwise,
        // atomically release the lock that's currently held by
        // mOwner. If the lock owner is not the current thread,
        // then throw IllegalMonitorStateException.

        // TODO -- you fill in here.
        if (mOwner.get() == Thread.currentThread()) {
            if (recursiveCount > 0) {
                recursiveCount--;
                return;
            }
            mOwner.set(null);
        } else {
            throw new IllegalMonitorStateException(
                    "Current thread has not acquired lock.");
        }
    }
}
