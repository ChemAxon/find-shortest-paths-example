/*
 * Copyright 2018 ChemAxon Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package example;

import chemaxon.struc.Molecule;
import java.util.Arrays;

/**
 * Find shortest paths from a central atom.
 *
 * Note that this is a naive implementation having O(n^2) scaling.
 *
 * @author Gabor Imre
 */
public class FindShortestPaths {
    /**
     * Flag to mark uninitialized values in {@link #shortestDistanceFromA1}.
     */
    private static final int UNKNOWN = -1;

    /**
     * Central atom index.
     */
    private final int a1;

    /**
     * Shortest distances.
     */
    private final int [] shortestDistanceFromA1;

    /**
     * CTAB of the input structure.
     */
    private final int [][] ctab;

    /**
     * Construct.
     *
     * @param ctab Connection table, see {@link Molecule#getCtab()}. No defensive copy is made, <b>DO NOT MODIFY</b>
     * after construction
     * @param a1 Atom index 1
     */
    public FindShortestPaths(int [][] ctab, int a1) {
        this.a1 = a1;
        this.ctab = ctab;

        final int atomCount = ctab.length;

        this.shortestDistanceFromA1 = new int[atomCount];
        Arrays.fill(this.shortestDistanceFromA1, UNKNOWN);

        this.shortestDistanceFromA1[a1] = 0;

        while (true) {
            boolean updateFound = false;

            // run through all atoms for wich we have a shortest path length and update its neighbors
            for (int ai = 0; ai < atomCount; ai++) {
                if (this.shortestDistanceFromA1[ai] == UNKNOWN) {
                    continue;
                }

                for (int j = 0; j < ctab[ai].length; j++) {
                    final int n = ctab[ai][j];
                    if (this.shortestDistanceFromA1[n] == UNKNOWN) {
                        // we can update neighbor unconditionally
                        this.shortestDistanceFromA1[n] = this.shortestDistanceFromA1[ai] + 1;
                        updateFound = true;
                    } else if (this.shortestDistanceFromA1[ai] + 1 < this.shortestDistanceFromA1[n]) {
                        // a better path found
                        this.shortestDistanceFromA1[n] = this.shortestDistanceFromA1[ai] + 1;
                        updateFound = true;
                    }
                 }
            }

            if (!updateFound) {
                break;
            }
        }
    }

    /**
     * Check if a path exists to an other atom.
     *
     * @param a2 Other atom
     * @return {@code true} when the central atom and the specified atom are connected
     */
    public boolean isPathExistsTo(int a2) {
        return this.shortestDistanceFromA1[a2] != UNKNOWN;
    }

    /**
     * Get shortest path length to an other atom.
     *
     * @param a2 Other atom
     * @return length of the shortest path from the central atom
     * @throws IllegalArgumentException when no path exists
     */
    public int getShortestPathLengthTo(int a2) throws IllegalArgumentException {
        if (!isPathExistsTo(a2)) {
            throw new IllegalArgumentException("No path found between central atom " + a1 + " and " + a2);
        }
        return this.shortestDistanceFromA1[a2];
    }


    /**
     * Get shortest path an other atom.
     *
     * @param a2 Other atom
     * @return Atom indices along the path
     * @throws IllegalArgumentException when no path exists
     */
    public int [] getSingleShortestPathTo(int a2) throws IllegalArgumentException {
        if (!isPathExistsTo(a2)) {
            throw new IllegalArgumentException("No path found between central atom " + a1 + " and " + a2);
        }

        final int [] ret = new int[getShortestPathLengthTo(a2) + 1];

        ret[ret.length - 1] = a2;

        if (a1 == a2) {
            return ret;
        }


        // fill path backwards
        for (int i = ret.length - 2; i >= 0; i--) {
            // fill position i with atom which
            //  - is a neighbor of the next (filled) atom
            //  - and is one step closer to the centrum
            final int lastAtom = ret[i + 1];
            final int nextPathLength = getShortestPathLengthTo(lastAtom) - 1;
            for (int ni = 0; ni < this.ctab[lastAtom].length; ni++) {
                final int candidateAtom = this.ctab[lastAtom][ni];

                if (this.shortestDistanceFromA1[candidateAtom] == nextPathLength) {
                    ret[i] = candidateAtom;
                    break;
                }
            }
        }

        return ret;
    }

    @Override
    public String toString() {
        return "a1: " + this.a1 + ", shortest distances from a1: " + Arrays.toString(this.shortestDistanceFromA1);

    }


}
