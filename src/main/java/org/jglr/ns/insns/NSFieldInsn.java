package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.vm.*;

public class NSFieldInsn extends NSInsn
{

    private String fieldName;

    public NSFieldInsn(String fieldName)
    {
        this(GET_FIELD, fieldName);
    }

    public NSFieldInsn(int opcode, String fieldName)
    {
        super(opcode);
        this.fieldName = fieldName;
    }

    public String fieldName()
    {
        return fieldName;
    }

    public String toString()
    {
        return super.toString() + " " + fieldName;
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException
    {
        out.writeUTF(fieldName);
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException
    {
        fieldName = in.readUTF();
        return this;
    }

}
