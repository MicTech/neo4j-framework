/*
 * Copyright (c) 2015 GraphAware
 *
 * This file is part of GraphAware.
 *
 * GraphAware is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.graphaware.tx.event;

import com.graphaware.common.policy.BaseNodeInclusionPolicy;
import com.graphaware.common.policy.InclusionPolicies;
import com.graphaware.common.policy.none.IncludeNoRelationships;
import com.graphaware.tx.event.improved.api.FilteredTransactionData;
import com.graphaware.tx.event.improved.api.ImprovedTransactionData;
import com.graphaware.tx.event.improved.api.LazyTransactionData;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.test.TestGraphDatabaseFactory;

import static com.graphaware.common.util.DatabaseUtils.registerShutdownHook;

/**
 * Only for documentation. If you need to change this class, change the code in README.md as well please.
 */
public class JustForDocs {

    private void justForDocs() {
        GraphDatabaseService database = new TestGraphDatabaseFactory().newImpermanentDatabase();
        registerShutdownHook(database);

        database.registerTransactionEventHandler(new TransactionEventHandler<Object>() {
            @Override
            public Object beforeCommit(TransactionData data) throws Exception {
                ImprovedTransactionData improvedTransactionData = new LazyTransactionData(data);

                //have fun here with improvedTransactionData!

                return null;
            }

            @Override
            public void afterCommit(TransactionData data, Object state) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterRollback(TransactionData data, Object state) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    private void justForDocs2() {
        GraphDatabaseService database = new TestGraphDatabaseFactory().newImpermanentDatabase();
        registerShutdownHook(database);

        database.registerTransactionEventHandler(new TransactionEventHandler.Adapter<Object>() {
            @Override
            public Object beforeCommit(TransactionData data) throws Exception {
                InclusionPolicies inclusionPolicies = InclusionPolicies.all()
                        .with(new BaseNodeInclusionPolicy() {
                            @Override
                            public boolean include(Node node) {
                                return node.getProperty("name", "default").equals("Two");
                            }
                        })
                        .with(IncludeNoRelationships.getInstance());

                ImprovedTransactionData improvedTransactionData
                        = new FilteredTransactionData(new LazyTransactionData(data), inclusionPolicies);

                //have fun here with improvedTransactionData!

                return null;
            }
        });
    }
}
