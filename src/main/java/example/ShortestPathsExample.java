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

import chemaxon.struc.Molecule;
import static com.chemaxon.calculations.util.MU.ofSmiles;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;

public class ShortestPathsExample {

    /**
     * Example molecule - Fumaric acid.
     *
     * Source: https://en.wikipedia.org/wiki/Fumaric_acid
     */
    public static final String FUMARIC_ACID_SMILES = "C(=C/C(=O)O)\\C(=O)O\tFumaric acid";

    /**
     * Example molecule - Caffeine.
     *
     * Source: https://en.wikipedia.org/wiki/Caffeine
     */
    public static final String CAFFEINE_SMILES = "CN1C=NC2=C1C(=O)N(C(=O)N2C)C\tCaffeine";


    private static void run_on_smiles(String smi) {
        System.out.println("Find shortest paths in " + smi);
        System.out.println();

        final Molecule m = ofSmiles(smi);

        for (int i = 0; i < m.getAtomCount(); i++) {
            System.out.println("Using central atom # " + i);
            final FindShortestPaths fsp = new FindShortestPaths(m.getCtab(), i);

            System.out.println("    FindShortestPaths instance: " + fsp.toString());


            for (int j = i + 1; j < m.getAtomCount(); j++) {
                System.out.println(
                    "        " + i + " - " + j +
                    " length: " + fsp.getShortestPathLengthTo(j) +
                    ", path: " + Arrays.toString(fsp.getSingleShortestPathTo(j))
                );

                final List<int []> allPaths = Lists.newArrayList(fsp.enumerateShortestPathsTo(j));

                if (allPaths.size() > 1) {
                    System.out.println("                         all paths:");
                    allPaths.forEach(p -> System.out.println("                                " + Arrays.toString(p)));
                }
            }
        }
        System.out.println();
        System.out.println();
    }

    public static void main(String [] args) {
        run_on_smiles(CAFFEINE_SMILES);
        run_on_smiles(FUMARIC_ACID_SMILES);
    }
}
