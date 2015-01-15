package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.vm.*;

public class NSLoadIntInsn extends NSInsn
{

    private int value;

    public NSLoadIntInsn(int value)
    {
        super(ILOAD);
        this.value = value;
    }

    public int value()
    {
        return value;
    }

    public String toString()
    {
        return super.toString() + " " + value;
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException
    {
        out.writeInt(value);
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException
    {
        value = in.readInt();
        return this;
    }

}
