package edu.vandy.simulator.managers.beings.completionService;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import edu.vandy.simulator.Controller;
import edu.vandy.simulator.managers.beings.BeingManager;
import edu.vandy.simulator.utils.Assignment;

import static edu.vandy.simulator.utils.ExceptionUtils.rethrowRunnable;
import static edu.vandy.simulator.utils.ExceptionUtils.rethrowSupplier;

/**
 * This BeingManager implementation uses the Java
 * ExecutorCompletionService to create a cached pool of Java threads
 * that run being simulations.
 */
@SuppressWarnings("ThrowFromFinallyBlock")
public class ExecutorCompletionServiceMgr
        extends BeingManager<BeingCallable> {
    /**
     * Used for Android debugging.
     */
    private final static String TAG =
            ExecutorCompletionServiceMgr.class.getName();

    /**
     * The ExecutorService contains a cached pool of threads.
     */
    // TODO -- you fill in here.
    private ExecutorService mExecutorService;

    /**
     * The CompletionService that's associated with the
     * ExecutorService above.
     */
    // TODO -- you fill in here.
    private CompletionService<BeingCallable> mExecutorCompletionService;

    /**
     * Default constructor.
     */
    public ExecutorCompletionServiceMgr() {
    }

    /**
     * Resets the fields to their initial values and tells all beings
     * to reset themselves.
     */
    @Override
    public void reset() {
        super.reset();
    }

    /**
     * Abstract method that BeingManagers implement to return a new
     * BeingCallable instance.
     *
     * @return A new typed Being instance.
     */
    @Override
    public BeingCallable newBeing() {
        // Return a new BeingCallable instance.
        // TODO -- you fill in here replacing this statement with your solution.
        return new BeingCallable(this);
    }

    /**
     * This entry point method is called by the Simulator framework to
     * start the being gazing simulation.
     **/
    @Override
    public void runSimulation() {
        // Call a method that uses the ExecutorService to create/start
        // a pool of threads that represent the beings in this
        // simulation.
        // TODO -- you fill in here.
        beginBeingThreadPool();

        // Call a method that waits for all futures to complete.
        // TODO -- you fill in here.
        awaitCompletionOfFutures();

        // Call this class's shutdownNow() method to cleanly shutdown
        // the executor service.
        // TODO -- you fill in here.
        shutdownNow();
    }

    /**
     * This factory method creates a cached thread pool executor
     * service.
     *
     * @return A cached thread pool executor
     */
    public ExecutorService createExecutorService() {
        // All STUDENTS:
        // Create an ExecutorService instance that contains a cached
        // thread pool.

        // TODO -- you fill in here replacing this statement with your solution.
        return Executors.newCachedThreadPool();
    }

    /**
     * This factory method creates an ExecutorCompletionService
     * that's associated with the created ExecutorService.
     *
     * @return An instance of ExecutorCompletionService
     */
    public CompletionService<BeingCallable> createExecutorCompletionService(
            ExecutorService executorService) {
        // TODO -- you fill in here replacing this statement with your solution.
        return new ExecutorCompletionService<>(executorService);
    }

    /**
     * Use the ExecutorService to create/start a pool of threads that
     * represent the beings in this simulation.
     */
    public void beginBeingThreadPool() {
        // Create an ExecutorService instance that contains a cached
        // pool of threads.  Call the BeingManager.getBeings() method
        // to iterate through the BeingCallables and submit each
        // BeingCallable to the ExecutorCompletionService.

        // TODO -- you fill in here.
        mExecutorService = createExecutorService();
        mExecutorCompletionService = createExecutorCompletionService(mExecutorService);
        getBeings().forEach(mExecutorCompletionService::submit);
    }

    /**
     * Wait for all the futures to complete.
     */
    public void awaitCompletionOfFutures() {
        // All STUDENTS:
        // Repeatedly call ExecutorCompletionService take() method
        // to block and wait for each BeingFuture to become available
        // and then get each future's result keeping track of how many
        // are not null. Catch any checked exceptions that may be thrown
        // and wrap and rethrow it as a unchecked RuntimeException.

        // GRADUATE STUDENTS:
        // Implement the same logic as above, but use the
        // ExceptionUtils rethrowSupplier() method to avoid the need
        // for a try/catch block to handle checked exceptions. Make
        // sure that your solution does not contain any try/catch
        // blocks.  Also, make sure to use Java streams, method
        // references, and/or lambda expressions where ever possible.

        int processed = 0;

        try {
            if (Assignment.isUndergraduate()) {
                // Loop through all the beings.
                // TODO -- you fill in here.
                try {
                    for (int i = 0; i < getBeingCount(); i++) {
                        Future<BeingCallable> future = mExecutorCompletionService.take();
                        if (future.get() != null)
                            processed++;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (Assignment.isGraduate()) {
                // Loop through all the beings.
                // TODO -- you fill in here.
                processed = (int) IntStream.rangeClosed(1, getBeingCount())
                        .mapToObj(__ -> rethrowSupplier(() ->
                                mExecutorCompletionService.take()).get())
                        .filter(future -> rethrowSupplier(() ->
                                future.get() != null).get())
                        .count();
            }
        } finally {
            // Ensure that everything worked as expected.
            if (processed != getBeingCount())
                throw new RuntimeException("expected " + getBeingCount() + " but got " + processed);

            Controller.log(TAG
                    + ": awaitCompletionOfFutures: "
                    + "processed" +
                    + processed
                    + "  of "
                    + getBeingCount()
                    + " beings.");
        }
    }

    /**
     * Called to terminate the executor service. This method should
     * only return after all threads have been terminated and all
     * resources cleaned up.
     */
    @Override
    public void shutdownNow() {
        Controller.log(TAG + ": shutdownNow: entered");

        // Shutdown the executor *now*.
        // TODO -- you fill in here.
        mExecutorService.shutdownNow();

        // Wait for all the threads to terminate.
        // TODO -- you fill in here.
        try {
            mExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Controller.log(TAG + ": shutdownNow: exited with "
                + getRunningBeingCount() + "/"
                + getBeingCount() + " running beings.");
    }
}
