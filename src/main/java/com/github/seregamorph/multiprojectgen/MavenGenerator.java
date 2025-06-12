package com.github.seregamorph.multiprojectgen;

import java.util.Collection;

/**
 * @author Sergey Chernov
 */
public class MavenGenerator {

    public static void main(String[] args) {
        int startIndex = 1;
        int depth = 5;
        int children = 5;
        var graph = GraphGenerator.generateGraph(depth, startIndex, children);
        graph.edges().forEach((node, deps) -> {
            System.out.println(node + " " + deps);
        });
        long edgesNum = graph.edges().values().stream()
                .flatMap(Collection::stream)
                .count();
        System.out.println("Number of modules: " + graph.edges().keySet().size());
        System.out.println("Number of edges: " + edgesNum);

        //generateProject(graph);
    }

}
