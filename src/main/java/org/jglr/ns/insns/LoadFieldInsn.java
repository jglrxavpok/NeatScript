package org.jglr.ns.insns;

import java.io.*;

public class LoadFieldInsn extends NSInsn
{

    private String fieldName;

    public LoadFieldInsn(String fieldName)
    {
        this(GET_FIELD, fieldName);
    }

    public LoadFieldInsn(int opcode, String fieldName)
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
    public NSInsn read(DataInput in) throws IOException
    {
        fieldName = in.readUTF();
        return this;
    }

}
