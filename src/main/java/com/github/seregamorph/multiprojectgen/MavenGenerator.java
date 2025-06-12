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

        File rootDir = new File("generated");
        if (rootDir.exists()) {
            throw new IllegalStateException(rootDir + " already exists, delete first");
        }
        rootDir.mkdirs();
        MavenWriter.writeProjects(rootDir, graph);
    }
}
