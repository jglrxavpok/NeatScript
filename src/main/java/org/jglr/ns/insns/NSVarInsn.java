package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.vm.*;

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

    @Override
    public NSInsn write(DataOutput out) throws IOException
    {
        out.writeInt(varIndex);
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException
    {
        varIndex = in.readInt();
        return this;
    }
}
