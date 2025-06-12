package com.github.seregamorph.multiprojectgen;

import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Chernov
 */
public record Graph(Map<String, Set<String>> edges) {

}
