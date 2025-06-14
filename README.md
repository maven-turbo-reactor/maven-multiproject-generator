# maven-multiproject-generator

## To use the tool:
* Use JDK17
* Build the tool
```shell
mvn clean install
```
* Generate the project
```shell
# if exists, rm -r generated
java -jar target/maven-multiproject-generator-exec.jar
```
The generated project is in the `generated` directory.

To adjust parameters, make changes in `MavenGenerator` class (no CLI support so far).

The generated dependency tree has "diamond" shape - first it scales from 1 to N^M where N is number of direct
child nodes, M is depth, then shrinks back to 1:
```
        Sample graph with
        depth=3 (grows 1..3, then shrinks 3..5),
        startIndex=1 (left node name offset),
        children=2 (scaling while going deep)
        and tail=3

                 3_0
                /   \
             2_0     4_0
            /   \   /   \
           /     3_1     \
        1_0               5_0--6_0--7_0--8_0
           \     3_2     /
            \   /   \   /
             2_1     4_1
                \   /
                 3_3
```

Then do a performance checking by building the project.
```
cd generated
# this takes around 30 sec for Maven3 and 1m45s for Maven4
mvn clean install
```