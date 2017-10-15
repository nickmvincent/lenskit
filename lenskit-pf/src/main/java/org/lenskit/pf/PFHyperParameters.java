/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2016 LensKit Contributors.  See CONTRIBUTORS.md.
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
package org.lenskit.pf;

import org.lenskit.inject.Shareable;
import org.lenskit.mf.funksvd.FeatureCount;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Hyper-parameters of poisson factorization
 *
 */
@Shareable
@Immutable
public final class PFHyperParameters implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double userWeightShpPrior;
    private final double userActivityShpPrior;
    private final double userActivityPriorMean;
    private final double itemWeightShpPrior;
    private final double itemActivityShpPrior;
    private final double itemActivityPriorMean;
    private final int featureCount;


    @Inject
    public PFHyperParameters(@UserWeightShpPrior double a,
                             @UserActivityShpPrior double aP,
                             @UserActivityPriorMean double bP,
                             @ItemWeightShpPrior double c,
                             @ItemActivityShpPrior double cP,
                             @ItemActivityPriorMean double dP,
                             @FeatureCount int k) {
        userWeightShpPrior = a;
        userActivityShpPrior = aP;
        userActivityPriorMean = bP;
        itemWeightShpPrior = c;
        itemActivityShpPrior = cP;
        itemActivityPriorMean = dP;
        featureCount = k;
    }

    public double getUserWeightShpPrior() {
        return userWeightShpPrior;
    }

    public double getUserActivityShpPrior() {
        return userActivityShpPrior;
    }


    public double getUserActivityPriorMean() {
        return userActivityPriorMean;
    }

    public double getItemWeightShpPrior() {
        return itemWeightShpPrior;
    }

    public double getItemActivityShpPrior() {
        return itemActivityShpPrior;
    }

    public double getItemActivityPriorMean() {
        return itemActivityPriorMean;
    }

    public int getFeatureCount() {
        return featureCount;
    }
}
