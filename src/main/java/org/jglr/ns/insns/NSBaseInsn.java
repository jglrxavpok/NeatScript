package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.vm.*;

public class NSBaseInsn extends NSInsn
{

    public NSBaseInsn(int opcode)
    {
        super(opcode);
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException
    {
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException
    {
        return this;
    }

}
