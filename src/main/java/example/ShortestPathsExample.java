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
import static com.chemaxon.calculations.util.MU.ofSmiles;
import java.util.Arrays;

public class ShortestPathsExample {

    /**
     * Example molecule - caffein.
     *
     * Source: https://en.wikipedia.org/wiki/Fumaric_acid
     */
    public static final String FUMARIC_ACID_SMILES = "C(=C/C(=O)O)\\C(=O)O";

    public static void main(String [] args) {
        System.out.println("Find shortest paths in " + FUMARIC_ACID_SMILES);
        System.out.println();
        
        final Molecule m = ofSmiles(FUMARIC_ACID_SMILES);

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
             }
         }

    }
}
