package com.github.seregamorph.multiprojectgen;

import com.google.common.math.IntMath;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Sergey Chernov
 */
public class GraphGenerator {

    /**
     * Generates "diamond"-shaped dependency graph
     *
     * @param depth depth of levels from left node to the max width
     * @param startIndex left node index (can be useful for chained diamonds)
     * @param children number of child nodes
     * @return
     */
    static Graph generateGraph(int depth, int startIndex, int children) {
        /*
        Sample graph with
        depth=3 (grows 1..3, then shrinks 3..5),
        startIndex=1 (left node name offset)
        and children=2 (scaling while going deep)

                 3_0
                /   \
             2_0     4_0
            /   \   /   \
           /     3_1     \
        1_0               5_0
           \     3_2     /
            \   /   \   /
             2_1     4_1
                \   /
                 3_3
         */

        var edges = new TreeMap<String, Set<String>>();
        for (int i = 0; i < depth; i++) {
            int name = startIndex + i;
            int pow = IntMath.pow(children, i);
            for (int j = 0; j < pow; j++) {
                var node = name + "_" + j;
                Set<String> deps;
                if (i == 0) {
                    deps = Set.of();
                } else {
                    int prevName = name - 1;
                    deps = Set.of(prevName + "_" + (j / children));
                }
                edges.put(node, deps);
            }
        }
        for (int i = depth - 2; i >= 0; i--) {
            int name = startIndex + depth + depth - i - 2;
            int pow = IntMath.pow(children, i);
            for (int j = 0; j < pow; j++) {
                var node = name + "_" + j;
                int prevName = name - 1;
                var deps = new LinkedHashSet<String>();
                for (int d = 0; d < children; d++) {
                    String s = prevName + "_" + (j * children + d);
                    deps.add(s);
                }
                edges.put(node, deps);
            }
        }
        return new Graph(edges);
    }
}
