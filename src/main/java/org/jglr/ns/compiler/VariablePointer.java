package org.jglr.ns.compiler;

public class VariablePointer
{

    public static enum VariablePointerMode
    {
        FIELD, VARIABLE;
    }

    private VariablePointerMode mode;
    private Object              value;

    public VariablePointer()
    {
        mode = VariablePointerMode.VARIABLE;
    }

    public VariablePointerMode mode()
    {
        return mode;
    }

    public VariablePointer mode(VariablePointerMode mode)
    {
        this.mode = mode;
        return this;
    }

    public Object rawValue()
    {
        return value;
    }

    public VariablePointer rawValue(Object o)
    {
        this.value = o;
        return this;
    }

    public VariablePointer putVariable(int var)
    {
        mode(VariablePointerMode.VARIABLE);
        rawValue(var);
        return this;
    }

    public VariablePointer putField(FieldInfo field)
    {
        mode(VariablePointerMode.FIELD);
        rawValue(field);
        return this;
    }

    public FieldInfo asField()
    {
        if(mode() != VariablePointerMode.FIELD)
            throw new IllegalStateException("Trying to access a field while in mode " + mode().name().toLowerCase());
        return (FieldInfo) value;
    }

    public int asVarId()
    {
        if(mode() != VariablePointerMode.VARIABLE)
            throw new IllegalStateException("Trying to access a var ID while in mode " + mode().name().toLowerCase());
        return (int) value;
    }

    public boolean isField()
    {
        return mode() == VariablePointerMode.FIELD;
    }

    public boolean isVar()
    {
        return mode() == VariablePointerMode.VARIABLE;
    }
}
