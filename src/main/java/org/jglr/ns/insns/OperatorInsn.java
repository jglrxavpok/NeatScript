package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.*;
import org.jglr.ns.vm.*;

public class OperatorInsn extends NSInsn {

    private NSOperator operator;

    public OperatorInsn(NSOperator operator) {
        super(OPERATOR);
        this.operator = operator;
    }

    public NSOperator operator() {
        return operator;
    }

    public String toString() {
        return super.toString() + " " + operator.toString();
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException {
        out.writeUTF(operator.toString());
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException {
        operator = NSOperator.fromID(in.readUTF());
        return this;
    }

}
