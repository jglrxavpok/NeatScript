package org.jglr.ns;

import org.jglr.ns.insns.Label;
import org.jglr.ns.types.*;

public class NSVariable {

    private int varIndex;
    private String name;
    private NSType type;
    private NSObject value;
    private Label startLabel;
    private Label endLabel;

    public NSVariable(NSType type, String name, int varIndex) {
        this(type, name, varIndex, null);
    }

    public NSVariable(NSType type, String name, int varIndex, NSObject value) {
        this.type = type;
        this.name = name;
        this.varIndex = varIndex;
        value(value);
    }

    public NSVariable value(NSObject val) {
        this.value = val;
        return this;
    }

    public NSObject value() {
        return value;
    }

    public int varIndex() {
        return varIndex;
    }

    public String name() {
        return name;
    }

    public NSType type() {
        return type;
    }

    public NSVariable startLabel(Label label) {
        startLabel = label;
        return this;
    }

    public NSVariable endLabel(Label label) {
        endLabel = label;
        return this;
    }

    public Label endLabel() {
        return endLabel;
    }

    public Label startLabel() {
        return startLabel;
    }
}
