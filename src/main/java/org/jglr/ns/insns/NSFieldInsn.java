package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.vm.*;

public class NSFieldInsn extends NSInsn
{

    private String owner;
    private String name;

    public NSFieldInsn(int opcode, String owner, String name)
    {
        super(opcode);
        this.owner = owner;
        this.name = name;
    }

    public String name()
    {
        return name;
    }

    public String owner()
    {
        return owner;
    }

    public String toString()
    {
        return super.toString() + " " + owner + "::" + name;
    }

    @Override
    public NSInsn write(DataOutput out) throws IOException
    {
        out.writeUTF(owner);
        out.writeUTF(name);
        return this;
    }

    @Override
    public NSInsn read(NSVirtualMachine vm, DataInput in) throws IOException
    {
        owner = in.readUTF();
        name = in.readUTF();
        return this;
    }

}
