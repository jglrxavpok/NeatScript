package org.jglr.ns.insns;

public class NSNewInsn extends FunctionCallInsn
{

    public NSNewInsn()
    {
        super("$");
    }

    public int getOpcode()
    {
        return NEW;
    }

}
