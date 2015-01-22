package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.*;
import org.jglr.ns.vm.*;

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
        return NSOps.name(getOpcode());
    }

    public abstract NSInsn write(DataOutput out) throws IOException;

    public abstract NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException;
}
