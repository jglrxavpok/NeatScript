package org.jglr.ns.compiler;

import org.jglr.ns.types.*;

public class FieldInfo {

    private String owner;
    private String name;
    private NSType type;

    public FieldInfo(String name, String owner, NSType type) {
        name(name);
        owner(owner);
        type(type);
    }

    public FieldInfo owner(String owner) {
        this.owner = owner;
        return this;
    }

    public FieldInfo name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return name;
    }

    public String owner() {
        return owner;
    }

    public NSType type() {
        return type;
    }

    public FieldInfo type(NSType type) {
        this.type = type;
        return this;
    }
}
