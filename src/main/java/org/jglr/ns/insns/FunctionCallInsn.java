package org.jglr.ns.insns;


public class FunctionCallInsn extends NSInsn
{

    private String name;

    public FunctionCallInsn(String functionName)
    {
        super(FUNCTION_CALL);
        this.name = functionName;
    }

    public String functionName()
    {
        return name;
    }

    public String toString()
    {
        return super.toString() + " " + name;
    }

}
