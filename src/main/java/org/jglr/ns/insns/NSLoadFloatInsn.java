package org.jglr.ns.insns;

import java.io.*;

public class NSLoadFloatInsn extends NSInsn
{

    private float value;

    public NSLoadFloatInsn(float value)
    {
        super(FLOAD);
        this.value = value;
    }

    public float value()
    {
        return value;
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException
    {
        out.writeLong(Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public NSInsn read(DataInput in) throws IOException
    {
        value = Float.intBitsToFloat(in.readInt());
        return this;
    }
}
