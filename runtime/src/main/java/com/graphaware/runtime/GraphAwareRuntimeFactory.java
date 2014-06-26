package com.graphaware.runtime;

import com.graphaware.runtime.config.DefaultRuntimeConfiguration;
import com.graphaware.runtime.config.RuntimeConfiguration;
import com.graphaware.runtime.manager.*;
import com.graphaware.runtime.metadata.BatchSingleNodeModuleMetadataRepository;
import com.graphaware.runtime.metadata.ModuleMetadataRepository;
import com.graphaware.runtime.metadata.ProductionSingleNodeModuleMetadataRepository;
import com.graphaware.runtime.module.TxDrivenModule;
import com.graphaware.runtime.strategy.BatchSupportingTxDrivenModule;
import com.graphaware.tx.event.batch.api.TransactionSimulatingBatchInserter;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 */
public final class GraphAwareRuntimeFactory {

    public static GraphAwareRuntime createRuntime(GraphDatabaseService database) {
        return createRuntime(database, DefaultRuntimeConfiguration.getInstance());
    }

    public static GraphAwareRuntime createRuntime(GraphDatabaseService database, RuntimeConfiguration configuration) {
        ModuleMetadataRepository repository = new ProductionSingleNodeModuleMetadataRepository(database, configuration);
        TimerDrivenModuleManager timerDrivenModuleManager = new TimerDrivenModuleManagerImpl(repository, database);
        TransactionDrivenModuleManager<TxDrivenModule> transactionDrivenModuleManager = new ProductionTransactionDrivenModuleManager(repository, database);

        return new TimerAndTxDrivenRuntime(database, transactionDrivenModuleManager, timerDrivenModuleManager);
    }

    public static GraphAwareRuntime createRuntime(TransactionSimulatingBatchInserter batchInserter) {
        return createRuntime(batchInserter, DefaultRuntimeConfiguration.getInstance());
    }

    public static GraphAwareRuntime createRuntime(TransactionSimulatingBatchInserter batchInserter, RuntimeConfiguration configuration) {
        ModuleMetadataRepository metadataRepository = new BatchSingleNodeModuleMetadataRepository(batchInserter, configuration);
        TransactionDrivenModuleManager<BatchSupportingTxDrivenModule> manager = new BatchModuleManager(metadataRepository, batchInserter);

        return new BatchInserterBackedRuntime(batchInserter, configuration, manager);
    }

    private GraphAwareRuntimeFactory() {
    }
}
