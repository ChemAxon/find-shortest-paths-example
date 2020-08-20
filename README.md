Enumerate shortest paths example
================================


This example code supplements [Topology Analyser Plugin](https://apidocs.chemaxon.com/jchem/doc/dev/java/api/index.html?chemaxon/marvin/calculations/TopologyAnalyserPlugin.html)
`shortestPath` functionality with retrieval of atoms on the shortest path. When multiple, equal length shortest paths are 
present all of them can be enumerated.


**IMPORTANT**: This example is provided **AS-IS**. Please note that in the future this functionality might be made available
in the plugin, possibly in an incompatible way.


Getting started
---------------

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
   
    
Licensing
---------

The content of this project (this git repository) is distributed under the Apache License 2.0. Some dependencies of this
project are **ChemAxon proprietary products** which are **not** covered by this license. 
Please note that redistribution of ChemAxon proprietary products is not allowed.