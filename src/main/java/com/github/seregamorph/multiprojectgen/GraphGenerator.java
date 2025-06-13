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
     * @param tail number of tail nodes
     * @return
     */
    static Graph generateGraph(int depth, int startIndex, int children, int tail) {
        /*
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
         */

        var edges = new TreeMap<String, Set<String>>();
        for (int i = 0; i < depth; i++) {
            int prefixName = startIndex + i;
            int pow = IntMath.pow(children, i);
            for (int suffixName = 0; suffixName < pow; suffixName++) {
                var node = prefixName + "_" + suffixName;
                Set<String> deps;
                if (i == 0) {
                    deps = Set.of();
                } else {
                    int prevName = prefixName - 1;
                    deps = Set.of(prevName + "_" + (suffixName / children));
                }
                edges.put(node, deps);
            }
        }
        for (int i = depth - 2; i >= 0; i--) {
            int prefixName = startIndex + depth + depth - i - 2;
            int pow = IntMath.pow(children, i);
            for (int suffixName = 0; suffixName < pow; suffixName++) {
                var node = prefixName + "_" + suffixName;
                int prevName = prefixName - 1;
                var deps = new LinkedHashSet<String>();
                for (int d = 0; d < children; d++) {
                    String s = prevName + "_" + (suffixName * children + d);
                    deps.add(s);
                }
                edges.put(node, deps);
            }
        }
        for (int i = depth + depth - 1; i < depth + depth + tail - 1; i++) {
            int prevName = startIndex + i - 1;
            int name = startIndex + i;
            var prevNode = prevName + "_0";
            var node = name + "_0";
            edges.put(node, Set.of(prevNode));
        }
        return new Graph(edges);
    }
}
