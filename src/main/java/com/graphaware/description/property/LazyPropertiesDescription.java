/*
 * Copyright (c) 2013 GraphAware
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

package com.graphaware.description.property;

import com.graphaware.description.predicate.Predicate;
import org.neo4j.graphdb.PropertyContainer;

import static com.graphaware.description.predicate.Predicates.equalTo;
import static com.graphaware.description.predicate.Predicates.undefined;

/**
 * The most specific {@link PropertiesDescription} of a {@link PropertyContainer} that lazily consults the underlying
 * {@link PropertyContainer} and returns only predicates of type {@link com.graphaware.description.predicate.EqualTo}.
 * For keys that don't have a corresponding property defined on the {@link PropertyContainer},
 * {@link com.graphaware.description.predicate.Undefined} is returned.
 */
public class LazyPropertiesDescription extends BasePropertiesDescription implements PropertiesDescription {

    private final PropertyContainer propertyContainer;

    /**
     * Construct a new properties description as the most specific description of the given property container.
     *
     * @param propertyContainer to construct the most specific properties description from.
     */
    public LazyPropertiesDescription(PropertyContainer propertyContainer) {
        this.propertyContainer = propertyContainer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Predicate get(String key) {
        if (!propertyContainer.hasProperty(key)) {
            return undefined();
        }

        return equalTo(propertyContainer.getProperty(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<String> getKeys() {
        return propertyContainer.getPropertyKeys();
    }
}
