/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2012 Regents of the University of Minnesota and contributors
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
package org.grouplens.lenskit.vectors;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import static org.junit.Assert.assertNull;

import it.unimi.dsi.fastutil.doubles.DoubleRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.grouplens.common.test.MoreMatchers.closeTo;
import static org.grouplens.common.test.MoreMatchers.notANumber;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public abstract class SparseVectorTestCommon {
    /**
     * Create an empty vector.
     * @return An empty sparse vector.
     */
    protected abstract SparseVector emptyVector();
    
    /**
     * @return A singleton rating vector mapping 5 to PI.
     */
    protected abstract SparseVector singleton();
    
    /**
     * Construct a simple rating vector with three ratings.
     * @return A rating vector mapping {3, 7, 8} to {1.5, 3.5, 2}.
     */
    protected abstract SparseVector simpleVector();
    
    /**
     * Construct a simple rating vector with three ratings.
     * @return A rating vector mapping {3, 5, 8} to {2, 2.3, 1.7}.
     */
    protected abstract SparseVector simpleVector2();
    
    @Test
    public void testDot() {
        assertThat(emptyVector().dot(emptyVector()), closeTo(0));
        assertThat(emptyVector().dot(simpleVector()), closeTo(0));
        assertThat(singleton().dot(simpleVector()), closeTo(0));
        assertThat(singleton().dot(simpleVector().immutable()), closeTo(0));
        assertThat(simpleVector().dot(singleton()), closeTo(0));
        assertThat(simpleVector().dot(simpleVector2()), closeTo(6.4));
        assertThat(simpleVector().dot(simpleVector2().immutable()), closeTo(6.4));
    }
    
    @Test
    public void testCountCommonKeys() {
        assertThat(emptyVector().countCommonKeys(emptyVector()),
                   equalTo(0));
        assertThat(emptyVector().countCommonKeys(simpleVector()),
                   equalTo(0));
        assertThat(simpleVector().countCommonKeys(singleton()),
                   equalTo(0));
        assertThat(simpleVector2().countCommonKeys(singleton()),
                   equalTo(1));
        assertThat(simpleVector().countCommonKeys(simpleVector2()),
                   equalTo(2));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#get(long)}.
     */
    @Test
    public void testGet() {
        assertThat(emptyVector().get(5), notANumber());
        SparseVector v = singleton();
        assertThat(v.get(5), closeTo(Math.PI));
        assertThat(v.get(2), notANumber());
    
        v = simpleVector();
        assertThat(v.get(7), closeTo(3.5));
        assertThat(v.get(7), closeTo(3.5));
        assertThat(v.get(3), closeTo(1.5));
        assertThat(v.get(8), closeTo(2));
        assertThat(v.get(1), notANumber());
        assertThat(v.get(4), notANumber());
        assertThat(v.get(9), notANumber());
        assertThat(v.get(42), notANumber());
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#get(long, double)}.
     */
    @Test
    public void testGetWithDft() {
        assertThat(emptyVector().get(5, Double.NaN), notANumber());
        assertThat(emptyVector().get(5, -1), closeTo(-1));
        SparseVector v = singleton();
        assertThat(v.get(5, -1), closeTo(Math.PI));
        assertThat(v.get(2, -1), closeTo(-1));
    
        v = simpleVector();
        assertThat(v.get(7, -1), closeTo(3.5));
        assertThat(v.get(3, -1), closeTo(1.5));
        assertThat(v.get(8, -1), closeTo(2));
        assertThat(v.get(1, -1), closeTo(-1));
        assertThat(v.get(4, 42), closeTo(42));
        assertThat(v.get(9, -7), closeTo(-7));
        assertThat(v.get(42, Math.E), closeTo(Math.E));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#containsKey(long)}.
     */
    @Test
    public void testContainsId() {
        assertFalse(emptyVector().containsKey(5));
        assertFalse(emptyVector().containsKey(42));
        assertFalse(emptyVector().containsKey(-1));
    
        assertTrue(singleton().containsKey(5));
        assertFalse(singleton().containsKey(3));
        assertFalse(singleton().containsKey(7));
    
        assertFalse(simpleVector().containsKey(1));
        assertFalse(simpleVector().containsKey(5));
        assertFalse(simpleVector().containsKey(42));
        assertTrue(simpleVector().containsKey(3));
        assertTrue(simpleVector().containsKey(7));
        assertTrue(simpleVector().containsKey(8));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#iterator()}.
     */
    @Test
    public void testIterator() {
        assertFalse(emptyVector().iterator().hasNext());
        try {
            emptyVector().iterator().next();
            fail("iterator.next() should throw exception");
        } catch (NoSuchElementException x) {
            /* no-op */
        }
    
        Iterator<VectorEntry> iter = singleton().iterator();
        assertTrue(iter.hasNext());
        VectorEntry e = iter.next();
        assertFalse(iter.hasNext());
        assertEquals(5, e.getKey());
        assertThat(e.getValue(), closeTo(Math.PI));
        try {
            iter.next();
            fail("iter.next() should throw exception");
        } catch (NoSuchElementException x) {
            /* no-op */
        }
    
        VectorEntry[] entries =
                Iterators.toArray(simpleVector().iterator(),
                                  VectorEntry.class);
        assertEquals(3, entries.length);
        assertEquals(3, entries[0].getKey());
        assertEquals(7, entries[1].getKey());
        assertEquals(8, entries[2].getKey());
        assertThat(entries[0].getValue(), closeTo(1.5));
        assertThat(entries[1].getValue(), closeTo(3.5));
        assertThat(entries[2].getValue(), closeTo(2));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#fastIterator()}.
     */
    @Test
    public void testFastIterator() {
        assertFalse(emptyVector().fastIterator().hasNext());
        try {
            emptyVector().fastIterator().next();
            fail("iterator.next() should throw exception");
        } catch (NoSuchElementException x) {
            /* no-op */
        }
    
        Iterator<VectorEntry> iter = singleton().fastIterator();
        assertTrue(iter.hasNext());
        VectorEntry e = iter.next();
        assertFalse(iter.hasNext());
        assertEquals(5, e.getKey());
        assertThat(e.getValue(), closeTo(Math.PI));
        try {
            iter.next();
            fail("iter.next() should throw exception");
        } catch (NoSuchElementException x) {
            /* no-op */
        }
    
        Long[] keys = Iterators.toArray(
                Iterators.transform(simpleVector().fastIterator(),
                                    new Function<VectorEntry,Long>() {
                                        @Override
                                        public Long apply(VectorEntry e) {
                                            return e.getKey();
                                        }
                                    }), Long.class);
        assertThat(keys, equalTo(new Long[]{3l, 7l, 8l}));
    }
    @Test
    public void testVectorsGetPairedValues() {
        Iterator noPairs = Vectors.pairedIterator(emptyVector(), simpleVector());
        assertFalse(noPairs.hasNext());
        assertNull(noPairs.next());
        Iterator<Vectors.EntryPair> pairIter = Vectors.pairedIterator(simpleVector(), simpleVector2());
        assertTrue(pairIter.hasNext());
        Vectors.EntryPair pair = pairIter.next();
        assertTrue(pair.getKey() == 3);
        assertTrue(pair.getValue1() == 1.5);
        assertTrue(pair.getValue2() == 2.0);
        assertTrue(pairIter.hasNext());
        Vectors.EntryPair lastPair = pair;
        pair = pairIter.next();
        // normal iteration returns new objects
        assertTrue(lastPair.getKey() == 3);
        assertTrue(lastPair.getValue1() == 1.5);
        assertTrue(lastPair.getValue2() == 2.0);
        assertTrue(pair.getKey() == 8);
        assertTrue(pair.getValue1() == 2.0);
        assertTrue(pair.getValue2() == 1.7);
        assertFalse(pairIter.hasNext());
        assertNull(pairIter.next());
    }
    @Test
    public void testVectorsGetPairedValuesFast() {
        Iterator noPairs = Vectors.pairedIteratorFast(emptyVector(), simpleVector());
        assertFalse(noPairs.hasNext());
        assertNull(noPairs.next());
        Iterator<Vectors.EntryPair> pairIter = Vectors.pairedIteratorFast(simpleVector(), simpleVector2());
        assertTrue(pairIter.hasNext());
        Vectors.EntryPair pair = pairIter.next();
        assertTrue(pair.getKey() == 3);
        assertTrue(pair.getValue1() == 1.5);
        assertTrue(pair.getValue2() == 2.0);
        assertTrue(pairIter.hasNext());
        Vectors.EntryPair lastPair = pair;
        pair = pairIter.next();
        // fast iteration modifies and returns the same object
        assertFalse(lastPair.getKey() == 3);
        assertFalse(lastPair.getValue1() == 1.5);
        assertFalse(lastPair.getValue2() == 2.0);
        assertTrue(pair.getKey() == 8);
        assertTrue(pair.getValue1() == 2.0);
        assertTrue(pair.getValue2() == 1.7);
        assertFalse(pairIter.hasNext());
        assertNull(pairIter.next());
    }
    @Test
    public void testFast() {
        assertThat(emptyVector().fast(), notNullValue());
    }
    @Test
    public void testKeysSet() {
        LongSortedSet set = emptyVector().keySet();
        assertTrue(set.isEmpty());
    
        long[] keys = singleton().keySet().toLongArray();
        assertThat(keys, equalTo(new long[]{5}));
    
        keys = simpleVector().keySet().toLongArray();
        assertThat(keys, equalTo(new long[]{3, 7, 8}));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#values()}.
     */
    @Test
    public void testValues() {
        assertTrue(emptyVector().values().isEmpty());
    
        double[] vals = singleton().values().toDoubleArray();
        assertThat(vals.length, equalTo(1));
        assertThat(vals[0], closeTo(Math.PI));
    
        DoubleRBTreeSet s = new DoubleRBTreeSet(simpleVector().values());
        assertThat(s, equalTo(new DoubleRBTreeSet(new double[]{1.5, 3.5, 2})));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#size()}.
     */
    @Test
    public void testSize() {
        assertThat(emptyVector().size(), equalTo(0));
        assertThat(singleton().size(), equalTo(1));
        assertThat(simpleVector().size(), equalTo(3));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        assertTrue(emptyVector().isEmpty());
        assertFalse(singleton().isEmpty());
        assertFalse(simpleVector().isEmpty());
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#norm()}.
     */
    @Test
    public void testNorm() {
        assertThat(emptyVector().norm(), closeTo(0));
        assertThat(singleton().norm(), closeTo(Math.PI));
        assertThat(simpleVector().norm(), closeTo(4.301162634));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#sum()}.
     */
    @Test
    public void testSum() {
        assertThat(emptyVector().sum(), closeTo(0));
        assertThat(singleton().sum(), closeTo(Math.PI));
        assertThat(simpleVector().sum(), closeTo(7));
    }
    /**
     * Test method for {@link org.grouplens.lenskit.vectors.MutableSparseVector#mean()}.
     */
    @Test
    public void testMean() {
        assertThat(emptyVector().mean(), closeTo(0));
        assertThat(singleton().mean(), closeTo(Math.PI));
        assertThat(simpleVector().mean(), closeTo(7.0/3));
    }
    @Test
    public void testSortedKeys() {
        assertArrayEquals(new long[]{3,8,7}, simpleVector().keysByValue().toLongArray());
        assertArrayEquals(new long[]{7,8,3}, simpleVector().keysByValue(true).toLongArray());
        assertArrayEquals(new long[]{5,3,8}, simpleVector2().keysByValue(true).toLongArray());
    }
}
