package org.jglr.ns.insns;

import java.io.*;

public class StackInsn extends NSInsn
{

    public StackInsn(int opcode)
    {
        super(opcode);
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException
    {
        return this;
    }

    @Override
    public NSInsn read(DataInput in) throws IOException
    {
        return this;
    }

}
