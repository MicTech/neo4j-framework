package com.graphaware.tx.writer;

import com.graphaware.tx.executor.batch.IterableInputBatchTransactionExecutor;
import com.graphaware.tx.executor.batch.UnitOfWork;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * {@link SingleThreadedWriter} that writes tasks in batches. This is more performant but dangerous,
 * since a single task's failure can roll back the whole batch. This is here for experiments, not for production.
 */
public class BatchWriter extends SingleThreadedWriter implements DatabaseWriter {

    private static final Logger LOG = LoggerFactory.getLogger(BatchWriter.class);
    private static final int BATCH_SIZE = 100;

    public BatchWriter(GraphDatabaseService database) {
        super(database);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> RunnableFuture<T> createTask(Callable<T> task) {
        return new FutureTask<>(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runOneIteration() throws Exception {
        logQueueSizeIfNeeded();

        if (queue.isEmpty()) {
            return;
        }

        List<Runnable> tasks = new LinkedList<>();
        queue.drainTo(tasks);

        new IterableInputBatchTransactionExecutor<>(database, BATCH_SIZE, tasks, new UnitOfWork<Runnable>() {
            @Override
            public void execute(GraphDatabaseService database, Runnable input, int batchNumber, int stepNumber) {
                try {
                    input.run();
                } catch (Exception e) {
                    LOG.warn("Execution threw and exception.", e);
                }
            }
        }).execute();
    }
}
