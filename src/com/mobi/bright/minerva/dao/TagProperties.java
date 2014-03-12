package com.mobi.bright.minerva.dao;



public final class TagProperties {
    final String name;
    final String value;

    public TagProperties(String name,
                         String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
