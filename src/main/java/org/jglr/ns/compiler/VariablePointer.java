package org.jglr.ns.compiler;

import java.util.*;

public class VariablePointer {

    public static enum VariablePointerMode
    {
        FIELD, VARIABLE;
    }

    private Stack<VariablePointerMode> modes;
    private Stack<Object> values;

    public VariablePointer() {
        modes = new Stack<VariablePointerMode>();
        modes.push(VariablePointerMode.VARIABLE);

        values = new Stack<Object>();
    }

    public VariablePointerMode peekMode() {
        return modes.peek();
    }

    public VariablePointer pushMode(VariablePointerMode mode) {
        modes.push(mode);
        return this;
    }

    public Object peekRawValue() {
        return values.peek();
    }

    public VariablePointer pushRawValue(Object o) {
        values.push(o);
        return this;
    }

    public VariablePointer pushVariable(int var) {
        pushMode(VariablePointerMode.VARIABLE);
        pushRawValue(var);
        return this;
    }

    public VariablePointer pushField(FieldInfo field) {
        pushMode(VariablePointerMode.FIELD);
        pushRawValue(field);
        return this;
    }

    public FieldInfo peekField() {
        if (peekMode() != VariablePointerMode.FIELD)
            throw new IllegalStateException("Trying to access a field while in mode " + peekMode().name().toLowerCase());
        return (FieldInfo) values.peek();
    }

    public Object popValue() {
        modes.pop();
        return values.pop();
    }

    public int peekVarId() {
        if (peekMode() != VariablePointerMode.VARIABLE)
            throw new IllegalStateException("Trying to access a var ID while in mode " + peekMode().name().toLowerCase());
        return (int) values.peek();
    }

    public boolean isField() {
        return peekMode() == VariablePointerMode.FIELD;
    }

    public boolean isVar() {
        return peekMode() == VariablePointerMode.VARIABLE;
    }

}
