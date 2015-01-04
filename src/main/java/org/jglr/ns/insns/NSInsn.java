package org.jglr.ns.insns;

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
}
