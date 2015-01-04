package org.jglr.ns.insns;

public class NSVarInsn extends NSInsn
{

    private int varIndex;

    public NSVarInsn(int opcode, int varIndex)
    {
        super(opcode);
        this.varIndex = varIndex;
    }

    public int varIndex()
    {
        return varIndex;
    }

    public String toString()
    {
        return super.toString() + " " + varIndex;
    }
}
