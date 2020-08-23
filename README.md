Enumerate shortest paths example
================================


This example code supplements [Topology Analyser Plugin](https://apidocs.chemaxon.com/jchem/doc/dev/java/api/index.html?chemaxon/marvin/calculations/TopologyAnalyserPlugin.html)
`shortestPath` functionality with retrieval of atoms on the shortest path. When multiple, equal length shortest paths are
present all of them can be enumerated.


**IMPORTANT**: This example is provided **AS-IS**. Please note that in the future this functionality might be made available
in the plugin and/or through other utility classes, possibly in an incompatible way.


Getting started
---------------

Make sure that a valid ChemAxon license (evaluation or production) for `Geometry Plugin Group` is available and
[installed](https://docs.chemaxon.com/Installing+Licenses).

  * When you have a [JChem](https://chemaxon.com/products/jchem-engines) distribution
    [downloaded](https://chemaxon.com/products/jchem-engines/download), point
    the build to the contained `lib/jchem.jar`:

    ``` bash
    ./gradlew -PcxnJchemJar=../jchem/lib/jchem.jar runShortestPathsExample
    ```

    Note that some dependencies of this example (like [junit](https://mvnrepository.com/artifact/junit/junit)) is
    retrieved from the public
    [Maven Central repository](https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:maven_central).

  * ChemAxon [public repository (`hub.chemaxon.com`)](https://docs.chemaxon.com/display/docs/Public+Repository) can
    also be used to retrieve dependencies. Make sure your [ChemAxon pass](https://pass.chemaxon.com/login) email address
    is available and you acquire Public Repository API key from <https://accounts.chemaxon.com/my/settings>.

    ``` bash
    ./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> runShortestPathsExample
    ```

Output of the example is expected to be:


``` bash
Find shortest paths in CN1C=NC2=C1C(=O)N(C(=O)N2C)C     Caffeine

Using central atom # 0
    FindShortestPaths instance: a1: 0, shortest distances from a1: [0, 1, 2, 3, 3, 2, 3, 4, 4, 5, 6, 4, 5, 5]
        0 - 1 length: 1, path: [0, 1]
        0 - 2 length: 2, path: [0, 1, 2]
        0 - 3 length: 3, path: [0, 1, 2, 3]
        0 - 4 length: 3, path: [0, 1, 5, 4]
        0 - 5 length: 2, path: [0, 1, 5]
        0 - 6 length: 3, path: [0, 1, 5, 6]
        0 - 7 length: 4, path: [0, 1, 5, 6, 7]
        0 - 8 length: 4, path: [0, 1, 5, 6, 8]
        0 - 9 length: 5, path: [0, 1, 5, 6, 8, 9]
                         all paths:
                                [0, 1, 5, 6, 8, 9]
                                [0, 1, 5, 4, 11, 9]
                         union of all paths:
                                {0, 1, 4, 5, 6, 8, 9, 11}
        0 - 10 length: 6, path: [0, 1, 5, 6, 8, 9, 10]
                         all paths:
                                [0, 1, 5, 6, 8, 9, 10]
                                [0, 1, 5, 4, 11, 9, 10]
                         union of all paths:
                                {0, 1, 4, 5, 6, 8, 9, 10, 11}
        0 - 11 length: 4, path: [0, 1, 5, 4, 11]
        0 - 12 length: 5, path: [0, 1, 5, 4, 11, 12]
        0 - 13 length: 5, path: [0, 1, 5, 6, 8, 13]
Using central atom # 1
    FindShortestPaths instance: a1: 1, shortest distances from a1: [1, 0, 1, 2, 2, 1, 2, 3, 3, 4, 5, 3, 4, 4]
        1 - 2 length: 1, path: [1, 2]
        1 - 3 length: 2, path: [1, 2, 3]
        1 - 4 length: 2, path: [1, 5, 4]
        1 - 5 length: 1, path: [1, 5]
        1 - 6 length: 2, path: [1, 5, 6]
        1 - 7 length: 3, path: [1, 5, 6, 7]
        1 - 8 length: 3, path: [1, 5, 6, 8]
        1 - 9 length: 4, path: [1, 5, 6, 8, 9]
                         all paths:
                                [1, 5, 6, 8, 9]
                                [1, 5, 4, 11, 9]
                         union of all paths:
                                {1, 4, 5, 6, 8, 9, 11}
        1 - 10 length: 5, path: [1, 5, 6, 8, 9, 10]
                         all paths:
                                [1, 5, 6, 8, 9, 10]
                                [1, 5, 4, 11, 9, 10]
                         union of all paths:
                                {1, 4, 5, 6, 8, 9, 10, 11}
        1 - 11 length: 3, path: [1, 5, 4, 11]
        1 - 12 length: 4, path: [1, 5, 4, 11, 12]
        1 - 13 length: 4, path: [1, 5, 6, 8, 13]

...
```

Running tests
-------------

Sanity tests are available in `src/test/java/`. Use `test` task to run them:

  * Local JChem configutation:

    ``` bash
    ./gradlew -PcxnJchemJar=../jchem/lib/jchem.jar test
    ```

  * ChemAxon public repository configuration:

    ``` bash
    ./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> test
    ```


Licensing
---------

The content of this project (this git repository) is distributed under the Apache License 2.0. Some dependencies of this
project are **ChemAxon proprietary products** which are **not** covered by this license.
Please note that redistribution of ChemAxon proprietary products is not allowed.
