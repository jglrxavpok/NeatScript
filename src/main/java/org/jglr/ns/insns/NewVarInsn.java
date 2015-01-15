package org.jglr.ns.insns;

import java.io.*;

import org.jglr.ns.types.*;
import org.jglr.ns.vm.*;

public class NewVarInsn extends NSVarInsn
{

    private String name;
    private NSType type;

    public NewVarInsn(NSType type, String name, int varIndex)
    {
        super(NEW_VAR, varIndex);
        this.type = type;
        this.name = name;
    }

    public NSType type()
    {
        return type;
    }

    public String name()
    {
        return name;
    }

    public String toString()
    {
        return super.toString() + " " + type().getID() + " " + name();
    }

    public NSInsn write(DataOutput output) throws IOException
    {
        super.write(output);
        output.writeUTF(name);
        output.writeUTF(type.getID());
        return this;
    }

    public NSInsn read(NSVirtualMachine vm, DataInput input) throws IOException
    {
        super.read(vm, input);
        name = input.readUTF();
        try
        {
            type = vm.getType(input.readUTF());
        }
        catch(NSClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return this;
    }
}
