package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.*;

public abstract class NSInsn implements NSOps
{
    private int opcode;

    public NSInsn(int opcode)
    {
        this.opcode = opcode;
    }

    public int getOpcode()
    {
        return opcode;
    }

    public String toString()
    {
        return NSOps.name(opcode);
    }

    public abstract NSInsn write(DataOutput out) throws IOException;

    public abstract NSInsn read(DataInput in) throws IOException;
}
