/*
 * Copyright 2020 ChemAxon Ltd.
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

import chemaxon.marvin.modelling.util.U;
import chemaxon.struc.Molecule;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;

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
            final int nextPathLength = i; // getShortestPathLengthTo(lastAtom) - 1;
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

    /**
     * Complete path.
     *
     * @param pathAtoms Atoms in the path
     * @param neighborChoices Neighbor choices made: element {@code [i]} contains which {@code ctab} member is chosen
     * for the next atom in the path to arrive
     * @param startFillFrom Last index in the arrays to complete; larger indices represent a valid path
     */
    private void completePath(int [] pathAtoms, int [] neighborChoices, int startFillFrom) {
        // fill path backwards
        for (int i = startFillFrom; i >= 0; i--) {
            if (i < startFillFrom) { neighborChoices[i] = 0; }

            // fill position i with atom which
            //  - is a neighbor of the next (filled) atom
            //  - and is one step closer to the centrum
            final int lastAtom = pathAtoms[i + 1];
            final int nextPathLength = i;
            for (int ni = neighborChoices[i]; ni < this.ctab[lastAtom].length; ni++) {
                final int candidateAtom = this.ctab[lastAtom][ni];

                if (this.shortestDistanceFromA1[candidateAtom] == nextPathLength) {
                    pathAtoms[i] = candidateAtom;
                    neighborChoices[i] = ni;
                    break;
                }
            }
        }
    }


    /**
     * Enumerate all shortest papth to an other atom.
     *
     * @param a2 Other atom
     * @return Iterator of shortest paths.
     * @throws IllegalArgumentException when no path exists
     */
    public Iterator<int []> enumerateShortestPathsTo(int a2) throws IllegalArgumentException {
        if (!isPathExistsTo(a2)) {
            throw new IllegalArgumentException("No path found between central atom " + a1 + " and " + a2);
        }



        final int[] pathAtoms = new int[ getShortestPathLengthTo(a2) + 1 ];
        final int[] neighborChoices = new int[ getShortestPathLengthTo(a2) + 1];

        pathAtoms[pathAtoms.length - 1] = a2;

        if (a1 == a2) {
            return Iterators.singletonIterator(pathAtoms);
        }


        final int startFillFrom = pathAtoms.length - 2;
        completePath(pathAtoms, neighborChoices, startFillFrom);


        return new AbstractIterator<int[]>() {
            boolean pathAtomsValid = true;

            @Override
            protected int[] computeNext() {
                if (pathAtomsValid) {
                    pathAtomsValid = false;
                    return U.clone(pathAtoms);
                }

                // try to bump neighbor choices
                boolean found = false;
                for (int i = 0; !found && i < pathAtoms.length - 1; i++) {
                    final int nextAtomInPath = pathAtoms[i + 1];

                    while (neighborChoices[i] < ctab[nextAtomInPath].length - 1) {
                        neighborChoices[i]++;
                        if (getShortestPathLengthTo(ctab[nextAtomInPath][neighborChoices[i]]) == i) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        completePath(pathAtoms, neighborChoices, i);
                        return U.clone(pathAtoms);
                    }
                }

                return endOfData();
            }
        };
    }

    /**
     * Atom count.
     *
     * @return Atom count
     */
    public int getAtomCount() {
        return ctab.length;
    }

    /**
     * Union of shortest paths from single atom.
     *
     *
     * @param startAtom Start atom index.
     * @return Atom indices from the union of all of the shortest paths starting from the specified atom
     */
    public BitSet unionOfShortestPaths(int startAtom) {
        final BitSet start = new BitSet();
        start.set(startAtom);
        return unionOfShortestPaths(start);
    }

    /**
     * Union of shortest paths from multiple atoms.
     *
     *
     * @param startAtoms Start atom indices. An arbitrary set of atoms.
     * @return Atom indices from the union of all of the shortest paths starting from the specified atoms
     */
    public BitSet unionOfShortestPaths(BitSet startAtoms) {
        BitSet ret = new BitSet(getAtomCount());

        ret.or(startAtoms);

        BitSet currentSet = startAtoms;
        while (true) {
            boolean stepMade = false;
            BitSet nextSet = new BitSet(getAtomCount());

            for (int i = currentSet.nextSetBit(0); i >= 0; i = currentSet.nextSetBit(i+1)) {
                // operate on index i here
                final int distanceI = getShortestPathLengthTo(i);
                if (distanceI == 0) {
                    break;
                }
                final int prevDistance = distanceI - 1;

                for (int neighborIndex = 0; neighborIndex < ctab[i].length; neighborIndex++) {
                    final int neighborAtom = this.ctab[i][neighborIndex];
                    final int neghborDistance = getShortestPathLengthTo(neighborAtom);
                    if (neghborDistance == prevDistance) {
                        stepMade = true;
                        nextSet.set(neighborAtom);
                    }
                }

                if (i == Integer.MAX_VALUE) {
                    break; // or (i+1) would overflow
                }
            }

            ret.or(nextSet);
            currentSet = nextSet;

            if (!stepMade) {
                break;
            }
        }
        return ret;
    }

}
