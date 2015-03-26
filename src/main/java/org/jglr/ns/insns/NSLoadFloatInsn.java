package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.vm.*;

public class NSLoadFloatInsn extends NSInsn {

    private float value;

    public NSLoadFloatInsn(float value) {
        super(FLOAD);
        this.value = value;
    }

    public float value() {
        return value;
    }

    public String toString() {
        return super.toString() + " " + value;
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException {
        out.writeLong(Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException {
        value = Float.intBitsToFloat(in.readInt());
        return this;
    }
}
