package org.jglr.ns.insns;

import org.jglr.ns.types.*;

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

    // TODO: Read/Write
}
