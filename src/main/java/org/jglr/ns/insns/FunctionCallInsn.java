package org.jglr.ns.insns;

public class FunctionCallInsn extends NSInsn
{

    public static final String PREVIOUS = "$PREV$";
    private String             name;
    private String             owner;

    public FunctionCallInsn(String functionName)
    {
        this(functionName, "std");
    }

    public FunctionCallInsn(String functionName, String functionOwner)
    {
        super(FUNCTION_CALL);
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

}
