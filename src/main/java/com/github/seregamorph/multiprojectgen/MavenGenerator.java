package com.github.seregamorph.multiprojectgen;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        var libraries = loadLibrariesDictionary();

        MavenWriter.writeProjects(rootDir, graph, libraries);
        System.out.println("Generated project can be found in " + rootDir.getAbsolutePath());
    }

    private static List<GroupArtifactVersion> loadLibrariesDictionary() {
        var file = new File("libraries.txt");
        try (var br = new BufferedReader(new FileReader(file))) {
            String line;
            List<GroupArtifactVersion> result = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    result.add(new GroupArtifactVersion(parts[0], parts[1], parts[2]));
                } else if (parts.length == 2) {
                    // inherit dependency version from spring-boot-dependencies
                    result.add(new GroupArtifactVersion(parts[0], parts[1], null));
                } else {
                    throw new IllegalArgumentException("Invalid line " + line);
                }
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading " + file, e);
        }
    }
}
