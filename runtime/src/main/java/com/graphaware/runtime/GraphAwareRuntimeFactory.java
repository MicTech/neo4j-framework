package com.graphaware.runtime;

import com.graphaware.runtime.config.DefaultRuntimeConfiguration;
import com.graphaware.runtime.config.RuntimeConfiguration;
import com.graphaware.runtime.manager.*;
import com.graphaware.runtime.metadata.BatchSingleNodeMetadataRepository;
import com.graphaware.runtime.metadata.ModuleMetadataRepository;
import com.graphaware.runtime.metadata.ProductionSingleNodeMetadataRepository;
import com.graphaware.tx.event.batch.api.TransactionSimulatingBatchInserter;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * Factory producing {@link GraphAwareRuntime}. This should be the only way a runtime is created.
 */
public final class GraphAwareRuntimeFactory {


    public static GraphAwareRuntime createRuntime(GraphDatabaseService database) {
        return createRuntime(database, DefaultRuntimeConfiguration.getInstance());
    }

    public static GraphAwareRuntime createRuntime(GraphDatabaseService database, RuntimeConfiguration configuration) {
        ModuleMetadataRepository repository = new ProductionSingleNodeMetadataRepository(database, configuration);
        TimerDrivenModuleManager timerDrivenModuleManager = new ProductionTimerDrivenModuleManager(database, repository);
        TxDrivenModuleManager txDrivenModuleManager = new ProductionTxDrivenModuleManager(database, repository);

        return new ProductionRuntime(database, txDrivenModuleManager, timerDrivenModuleManager);
    }

    public static GraphAwareRuntime createRuntime(TransactionSimulatingBatchInserter batchInserter) {
        return createRuntime(batchInserter, DefaultRuntimeConfiguration.getInstance());
    }

    public static GraphAwareRuntime createRuntime(TransactionSimulatingBatchInserter batchInserter, RuntimeConfiguration configuration) {
        ModuleMetadataRepository metadataRepository = new BatchSingleNodeMetadataRepository(batchInserter, configuration);
        TxDrivenModuleManager manager = new BatchModuleManager(batchInserter, metadataRepository);

        return new BatchInserterRuntime(batchInserter, manager);
    }

    private GraphAwareRuntimeFactory() {
    }
}
