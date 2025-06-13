package com.github.seregamorph.multiprojectgen;

import org.intellij.lang.annotations.Language;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sergey Chernov
 */
public class MavenWriter {

    public static void writeProjects(File rootDir, Graph graph) throws IOException {
        writeRootPom(new File(rootDir, "pom.xml"), graph);
        for (Map.Entry<String, Set<String>> entry : graph.edges().entrySet()) {
            String nodeId = entry.getKey();
            var dir = dir(nodeId);
            writePom(new File(rootDir, dir + "/pom.xml"), artifactId(nodeId),
                    entry.getValue().stream().map(MavenWriter::artifactId).toList());
        }
    }

    private static void writePom(File pomFile, String artifactId, Collection<String> compileDepArtifactIds) throws IOException {
        @Language("XML") var contents = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                
                    <parent>
                        <groupId>com.example.performance.test</groupId>
                        <artifactId>root</artifactId>
                        <version>1.0-SNAPSHOT</version>
                    </parent>

                    <artifactId>""" + artifactId + """
                </artifactId>
                
                    <dependencies>
                """
                + (
                compileDepArtifactIds.stream().map(depArtifactId ->
                                    "        <dependency>\n"
                                        + "            <groupId>com.example.performance.test</groupId>\n"
                                        + "            <artifactId>" + depArtifactId + """
                                </artifactId>
                                            <version>1.0-SNAPSHOT</version>
                                        </dependency>
                                """)
                        .collect(Collectors.joining("\n"))
        ) + """
                    </dependencies>
                </project>
                """;
        pomFile.getParentFile().mkdirs();
        Files.write(pomFile.toPath(), contents.getBytes(StandardCharsets.UTF_8));
    }

    private static void writeRootPom(File pomFile, Graph graph) throws IOException {
        @Language("XML") var contents = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                
                    <groupId>com.example.performance.test</groupId>
                    <artifactId>root</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <packaging>pom</packaging>
                
                    <properties>
                        <maven.compiler.source>17</maven.compiler.source>
                        <maven.compiler.target>17</maven.compiler.target>
                        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    </properties>
                
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.apache.maven.plugins</groupId>
                                <artifactId>maven-enforcer-plugin</artifactId>
                                <version>3.3.0</version>
                                <executions>
                                    <execution>
                                        <id>enforce</id>
                                        <configuration>
                                            <rules>
                                                <DependencyConvergence>
                                                    <uniqueVersions>false</uniqueVersions>
                                                </DependencyConvergence>
                                            </rules>
                                        </configuration>
                                        <goals>
                                            <goal>enforce</goal>
                                        </goals>
                                    </execution>
                                </executions>
                            </plugin>
                        </plugins>
                    </build>

                    <dependencyManagement>
                        <dependencies>
                            <dependency>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-dependencies</artifactId>
                                <version>3.5.0</version>
                                <type>pom</type>
                                <scope>import</scope>
                            </dependency>
                        </dependencies>
                    </dependencyManagement>

                    <modules>
                """
                + (
                graph.edges().keySet().stream().map(nodeId -> "        <module>" + dir(nodeId) + "</module>")
                        .collect(Collectors.joining("\n"))
        ) + """
                
                    </modules>
                </project>
                """;
        Files.write(pomFile.toPath(), contents.getBytes(StandardCharsets.UTF_8));
    }

    private static String dir(String nodeId) {
        var tokens = nodeId.split("_");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Invalid node id " + nodeId);
        }
        return "module_" + tokens[0] + "/module_" + tokens[1];
    }

    private static String artifactId(String nodeId) {
        return "module_" + nodeId;
    }
}
