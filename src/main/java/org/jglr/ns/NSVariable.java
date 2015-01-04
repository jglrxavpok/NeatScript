package org.jglr.ns;

import org.jglr.ns.types.*;

public class NSVariable
{

    private int      varIndex;
    private String   name;
    private NSType   type;
    private NSObject value;

    public NSVariable(NSType type, String name, int varIndex)
    {
        this.type = type;
        this.name = name;
        this.varIndex = varIndex;
    }

    public NSVariable value(NSObject val)
    {
        this.value = val;
        return this;
    }

    public NSObject value()
    {
        return value;
    }

    public int varIndex()
    {
        return varIndex;
    }

    public String name()
    {
        return name;
    }

    public NSType type()
    {
        return type;
    }

}
