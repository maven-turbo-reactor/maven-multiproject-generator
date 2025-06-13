package com.github.seregamorph.multiprojectgen;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Sergey Chernov
 */
public class MavenGenerator {

    public static void main(String[] args) throws IOException {
        int startIndex = 1;
        int depth = 8;
        int children = 3;
        int tail = 10;
        var graph = GraphGenerator.generateGraph(depth, startIndex, children, tail);
        graph.edges().forEach((node, deps) -> {
            System.out.println(node + " " + deps);
        });
        long edgesNum = graph.edges().values().stream()
                .mapToLong(Collection::size)
                .sum();
        System.out.println("Number of modules: " + graph.edges().size());
        System.out.println("Number of edges: " + edgesNum);

        File rootDir = new File("generated");
        if (rootDir.exists()) {
            throw new IllegalStateException(rootDir.getAbsolutePath() + " directory already exists, delete first");
        }
        rootDir.mkdirs();
        MavenWriter.writeProjects(rootDir, graph);
        System.out.println("Generated project can be found in " + rootDir.getAbsolutePath());
    }
}
