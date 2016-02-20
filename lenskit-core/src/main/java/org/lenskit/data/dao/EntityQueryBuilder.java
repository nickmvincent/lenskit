/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2014 LensKit Contributors.  See CONTRIBUTORS.md.
 * Work on LensKit has been funded by the National Science Foundation under
 * grants IIS 05-34939, 08-08692, 08-12148, and 10-17697.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.lenskit.data.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.lenskit.data.entities.Attribute;
import org.lenskit.data.entities.Entity;
import org.lenskit.data.entities.EntityType;
import org.lenskit.data.entities.TypedName;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Data query builder exposing a fluent interface.
 */
public class EntityQueryBuilder {
    private EntityType entityType;
    private final ImmutableList.Builder<Attribute<?>> filter;
    private final ImmutableList.Builder<SortKey> sortKey;

    /**
     * Construct a new data query builder.
     */
    public EntityQueryBuilder() {
        filter = ImmutableList.builder();
        sortKey = ImmutableList.builder();
    }

    /**
     * Set the entity type.
     * @param type The entity type.
     * @return The query builder (for chaining).
     */
    public EntityQueryBuilder setEntityType(EntityType type) {
        entityType = type;
        return this;
    }

    /**
     * Add a filter condition to the builder.
     * @param name The attribute to filter.
     * @param obj The object to test for equality.
     * @param <A> The attribute type.
     * @return The query builder (for chaining).
     */
    public <A> EntityQueryBuilder addFilterField(Attribute<A> attr, @Nonnull A obj) {
        return this.addFilterField(AttributeValue.create(attr, obj));
    }

    /**
     * Add a filter condition to the builder.
     * @param condition The condition to add.
     * @param <A> The attribute type.
     * @return The query builder (for chaining).
     */
    public <A> EntityQueryBuilder addFilterField(AttributeValue<?> condition) {
        filter.add(condition);
        return this;
    }

    /**
     * Add a filter condition to the builder.
     * @param conditions The conditions to add.
     * @return The query builder (for chaining).
     */
    public EntityQueryBuilder addFilterFields(List<AttributeValue<?>> conditions) {
        for (AttributeValue<?> c: conditions) {
            addFilterField(c);
        }
        return this;
    }

    /**
     * Add an ascending sort key.
     * @param attr The attribute to sort by.
     * @return The query builder (for chaining).
     */
    public EntityQueryBuilder addSortKey(Attribute<? extends Comparable> attr) {
        return addSortKey(attr, SortOrder.ASCENDING);
    }

    /**
     * Add a sort key.
     * @param name The attribute to sort by.
     * @param order The sort order.
     * @return The query builder (for chaining).
     */
    public EntityQueryBuilder addSortKey(Attribute<? extends Comparable> attr, SortOrder order) {
        return addSortKey(new SortKey(attr, order));
    }

    /**
     * Add a sort key.
     * @param key The sort key to add.
     * @return The query builder (for chaining).
     */
    public EntityQueryBuilder addSortKey(SortKey key) {
        sortKey.add(key);
        return this;
    }

    /**
     * Add multiple sort keys.
     * @param keys The sort key to add.
     * @return The query builder (for chaining).
     */
    public EntityQueryBuilder addSortKeys(Iterable<SortKey> keys) {
        for (SortKey k: keys) {
            addSortKey(k);
        }
        return this;
    }

    /**
     * Build the data query.
     * @return The data query.
     */
    public EntityQuery<Entity> build() {
        return buildWithView(Entity.class);
    }

    /**
     * Build the data query with a view type.
     * @return The data query.
     */
    public <E extends Entity> EntityQuery<E> buildWithView(Class<E> viewClass) {
        Preconditions.checkState(entityType != null, "no entity type specified");
        return new EntityQuery<>(entityType, filter.build(), sortKey.build(), viewClass);
    }
}
