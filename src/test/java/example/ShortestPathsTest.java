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

import chemaxon.marvin.calculations.TopologyAnalyserPlugin;
import chemaxon.struc.Molecule;
import static com.chemaxon.calculations.util.MU.ofSmiles;
import com.google.common.collect.Lists;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class ShortestPathsTest {

    /**
     * A large structure.
     *
     * Source: https://en.wikipedia.org/wiki/Vancomycin
     */
    public static final String VANCOMYCIN_SMILES = "C[C@H]1[C@H]([C@@](C[C@@H](O1)O[C@@H]2[C@H]([C@@H]([C@H](O[C@H]2Oc3c4cc5cc3Oc6ccc(cc6Cl)[C@H]([C@H](C(=O)N[C@H](C(=O)N[C@H]5C(=O)N[C@@H]7c8ccc(c(c8)-c9c(cc(cc9O)O)[C@H](NC(=O)[C@H]([C@@H](c1ccc(c(c1)Cl)O4)O)NC7=O)C(=O)O)O)CC(=O)N)NC(=O)[C@@H](CC(C)C)NC)O)CO)O)O)(C)N)O";

    /**
     * A large multi fragment structure.
     *
     * Source: https://en.wikipedia.org/wiki/Ramoplanin
     * Source: https://en.wikipedia.org/wiki/Vindesine
     * Source: https://en.wikipedia.org/wiki/Voacamine
     */
    public static final String LARGE_MULTIFRAG_SMILES = "C[C@@H]1C(=O)N[C@H](C(=O)O[C@@H]([C@@H](C(=O)N[C@@H](C(=O)N[C@@H](C(=O)N[C@@H](C(=O)N[C@H](C(=O)N[C@@H](C(=O)N[C@H](C(=O)N[C@H](C(=O)N[C@@H](C(=O)N[C@H](C(=O)N[C@@H](C(=O)N[C@H](C(=O)NCC(=O)N[C@H](C(=O)N1)CC(C)C)c2ccc(cc2)O)[C@@H](C)O)c3ccc(cc3)O[C@@H]4[C@H]([C@H]([C@@H]([C@H](O4)CO)O)O)O[C@@H]5[C@H]([C@H]([C@@H]([C@H](O5)CO)O)O)O)CCCN)Cc6ccccc6)[C@H](C)O)c7ccc(cc7)O)c8ccc(cc8)O)[C@@H](C)O)CCCN)c9ccc(cc9)O)NC(=O)[C@H](CC(=O)N)NC(=O)/C=C\\C=C\\CC(C)C)C(=O)N)c1ccc(c(c1)Cl)O.O=C(OC)[C@]4(c2c(c1ccccc1[nH]2)CCN3C[C@](O)(CC)C[C@@H](C3)C4)c5c(OC)cc6c(c5)[C@@]89[C@@H](N6C)[C@@](O)(C(=O)N)[C@H](O)[C@@]7(/C=C\\CN([C@@H]78)CC9)CC.CCC1CC2CC3(C1N(C2)CCC4=C3NC5=CC(=C(C=C45)OC)C6CC\\7C(C(CC8=C6NC9=CC=CC=C89)N(C/C7=C/C)C)C(=O)OC)C(=O)OC";

    /**
     * Simple benzol.
     */
    public static final String C6_SMILES = "C1CCCCC1";

    /**
     * Biphenyl.
     *
     * Zero based atom indices:
     * <pre><code>
     *          3 -- 4          7 -- 8
     *        /        \      /        \
     *       2          5 -- 6          9
     *        \        /      \        /
     *          1 -- 0         11 -- 10
     * </code></pre>
     *
     */
    public static final String BIPHENYL_SMILES = "C1CCCCC1-C1CCCCC1";


    public static void ensure_consistent_path(final Molecule m, int a1, int a2, int [] path) {
        assertThat(path.length, greaterThan(0));
        assertThat(path[0], is(a1));
        assertThat(path[path.length - 1], is(a2));

        for (int i = 1; i < path.length; i++) {
            assertThat("Valid atom index in path", path[i], greaterThan(-1));
            assertThat("Valid atom index in path", path[i], lessThan(m.getAtomCount()));


            assertThat("Successive atoms in path are connected", m.getBondTable().getBondIndex(path[i - 1], path[i]), greaterThan(-1));

            for (int j = 0; j < i; j++) {
                assertThat("No repetition in path", path[i], not(path[j]));
                if (j < i - 1) {
                    assertThat("No bond to previous atoms", m.getBondTable().getBondIndex(path[j], path[i]), is(-1));
                }
            }
        }
    }

    public static void ensure_consistency_with_topologyanalyser(final Molecule m) throws Exception {
        final TopologyAnalyserPlugin plugin = new TopologyAnalyserPlugin();
        plugin.setMolecule(m);
        plugin.run();

        final int atomCount = m.getAtomCount();


        // ensure consistent behavior with all possible (including self and swapped) atom indices
        for (int a1 = 0; a1 < atomCount; a1++) {
            final FindShortestPaths fp = new FindShortestPaths(m.getCtab(), a1);

            for (int a2 = 0; a2 < atomCount; a2++) {
                final int tap_path_length = plugin.getShortestPath(a1, a2);
                if (fp.isPathExistsTo(a2)) {
                    assertThat("Same path length should be found", fp.getShortestPathLengthTo(a2), is(tap_path_length));
                    ensure_consistent_path(m, a1, a2, fp.getSingleShortestPathTo(a2));

                    // check path enumeration
                    final List<int []> allPaths = Lists.newArrayList(fp.enumerateShortestPathsTo(a2));


                    assertThat(allPaths.size(), greaterThan(0));

                    final int a1_f = a1;
                    final int a2_f = a2;
                    allPaths.stream().forEachOrdered(p -> ensure_consistent_path(m, a1_f, a2_f, p));

                    for (int i = 0; i < allPaths.size(); i++) {
                        for (int j = i + 1; j < allPaths.size(); j++) {
                            assertThat("Enumerated paths are unique", allPaths.get(i), not(allPaths.get(j)));
                        }
                    }


                } else {
                    assertThat("Plugin found disconnected fragment", tap_path_length, is(Integer.MAX_VALUE));
                }
            }
        }
    }

    @Test
    public void run_on_vancomycin() throws Exception {
        ensure_consistency_with_topologyanalyser(ofSmiles(VANCOMYCIN_SMILES));
    }

    @Test
    public void large_multifrag() throws Exception {
        ensure_consistency_with_topologyanalyser(ofSmiles(LARGE_MULTIFRAG_SMILES));
    }


    @Test
    public void run_on_c6() {
        final Molecule m = ofSmiles(C6_SMILES);

        final FindShortestPaths fp = new FindShortestPaths(m.getCtab(), 0);

        final List<int []> allPaths = Lists.newArrayList(fp.enumerateShortestPathsTo(3));

        assertThat("Two pathes found", allPaths.size(), is(2));
        assertThat(allPaths, containsInAnyOrder(
                new int [] { 0, 1, 2, 3 },
                new int [] { 0, 5, 4, 3 }
        ));

    }

    @Test
    public void run_on_biphenyl() {
        final Molecule m = ofSmiles(BIPHENYL_SMILES);

        final FindShortestPaths fp = new FindShortestPaths(m.getCtab(), 2);

        final List<int []> allPaths = Lists.newArrayList(fp.enumerateShortestPathsTo(9));

        // allPaths.stream().forEachOrdered(a -> System.err.println(Arrays.toString(a)));
        assertThat("Four pathes found", allPaths.size(), is(4));
        assertThat(allPaths, containsInAnyOrder(
                new int [] { 2, 3, 4, 5, 6, 7, 8, 9 },
                new int [] { 2, 3, 4, 5, 6, 11, 10, 9 },
                new int [] { 2, 1, 0, 5, 6, 7, 8, 9 },
                new int [] { 2, 1, 0, 5, 6, 11, 10, 9 }
        ));

    }


}
