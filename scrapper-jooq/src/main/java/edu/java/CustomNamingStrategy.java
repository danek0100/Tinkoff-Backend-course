package edu.java;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class CustomNamingStrategy extends DefaultGeneratorStrategy {
    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        return toCamelCase(super.getJavaClassName(definition, mode));
    }

    private String toCamelCase(final String input) {
        StringBuilder result = new StringBuilder();
        for (String part : input.split("_")) {
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}
