package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.vm.*;

public class LineNumberInsn extends NSInsn {

    private int lineNumber;

    public LineNumberInsn(int line) {
        super(LINE_NUMBER);
        this.lineNumber = line;
    }

    public int number() {
        return lineNumber;
    }

    public String toString() {
        return super.toString() + " " + lineNumber;
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException {
        out.writeInt(lineNumber);
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException {
        lineNumber = in.readInt();
        return this;
    }
}
