package org.jglr.ns.insns;

import java.util.*;

import org.jglr.ns.types.*;

public class FunctionCallInsn extends NSInsn
{

    public static final String PREVIOUS    = "$PREV$";
    public static final String UNKNOWN_YET = "$UNKNOWN$";
    private String             name;
    private String             owner;
    private List<NSType>       types;

    public FunctionCallInsn(String functionName)
    {
        this(functionName, "std");
    }

    public FunctionCallInsn(String functionName, String functionOwner)
    {
        super(FUNCTION_CALL);
        this.types = new ArrayList<NSType>();
        this.name = functionName;
        this.owner = functionOwner;
    }

    public FunctionCallInsn functionOwner(String owner)
    {
        this.owner = owner;
        return this;
    }

    public String functionOwner()
    {
        return owner;
    }

    public String functionName()
    {
        return name;
    }

    public String toString()
    {
        return super.toString() + " " + owner + "::" + name;
    }

    public FunctionCallInsn types(List<NSType> types)
    {
        this.types = types;
        return this;
    }

    public List<NSType> types()
    {
        return types;
    }

}
