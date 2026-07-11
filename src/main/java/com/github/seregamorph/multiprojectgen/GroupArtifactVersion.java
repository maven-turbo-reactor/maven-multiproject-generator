package com.github.seregamorph.multiprojectgen;

import org.jspecify.annotations.Nullable;

/**
 * @author Sergey Chernov
 */
public record GroupArtifactVersion(String groupId, String artifactId, @Nullable String version) {
}
